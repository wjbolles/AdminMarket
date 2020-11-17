/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.adminmarket.command.actions;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.wjbolles.adminmarket.AdminMarket;
import com.wjbolles.adminmarket.command.CommandUtil;
import com.wjbolles.adminmarket.eco.dao.ItemListingDao;
import com.wjbolles.adminmarket.eco.model.ItemListing;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;

public class QueryActions {
    private final AdminMarket plugin;
    private final DecimalFormat df = new DecimalFormat("#.00");
    private final ItemListingDao listingDao;

    public QueryActions(AdminMarket plugin) {
        this.plugin = plugin;
        listingDao = plugin.getListingDao();

        df.setGroupingUsed(true);
        df.setGroupingSize(3);
    }

    private Comparator<ItemListing> comparatorFactory(String sortBy) {
        if(sortBy.equalsIgnoreCase("abc")){
            return Comparator.comparing(ItemListing::getMaterialAsString)
                    .thenComparing(ItemListing::getMaterialAsString);
        } else {
            return Comparator.comparing(ItemListing::getBuyPrice)
                    .thenComparing(ItemListing::getBuyPrice);
        }
    }

    private List<ItemListing> getListings(String sortBy, String sortOrder){
        Map<String, ItemListing> listings = listingDao.getAllListings();

        Comparator<ItemListing> compareBy = comparatorFactory(sortBy);
        List<ItemListing> sortedListings = listings.values().stream()
                .sorted(compareBy)
                .collect(Collectors.toList());
        if(sortOrder.equals("desc")){
            Collections.reverse(sortedListings);
        }
        return sortedListings;
    }

    private double getTotalCost(ItemListing listing, int amount){
        double total = listing.getTotalBuyPrice(amount);
        total += total * plugin.getPluginConfig().getSalesTax();
        total += listing.getValueAddedTax();
        return total;
    }

    private void buildBuyPrice(StringBuilder sb, ItemListing listing){
        if (CommandUtil.safeDoubleEqualsZero(listing.getBuyPrice())) {
            sb.append(ChatColor.WHITE).append(" B: n/a");
        } else {
            if (listing.isInfinite()) {
                sb.append(ChatColor.WHITE).append("B: -$").append(df.format(listing.getBuyPrice()));
            } else {
                double total = getTotalCost(listing,1);
                // Individual Item Column
                if (listing.getInventory() == 0) {
                    sb.append(ChatColor.RED).append("Curr. B: Out of Stock");
                } else {
                    sb.append(ChatColor.RED).append("Curr. B: -$").append(df.format(total));
                }

                // Stack (64 max) Column
                total = getTotalCost(listing, 64);
                if (listing.getInventory() < 64) {
                    sb.append(ChatColor.RED).append(" | Out of Stock");
                } else {
                    sb.append(ChatColor.RED).append(" | -$").append(df.format(total));
                }

                // 10 Stacks (64 max) Column
                total = getTotalCost(listing, 640);
                if (listing.getInventory() < 640) {
                    sb.append(ChatColor.RED).append(" | Out of Stock");
                } else {
                    sb.append(ChatColor.RED).append(" | -$").append(df.format(total));
                }

            }
        }
        sb.append("\n");
    }

    private void buildSellPrice(StringBuilder sb, ItemListing listing){
        if (CommandUtil.safeDoubleEqualsZero(listing.getSellPrice())) {
            sb.append(ChatColor.WHITE).append("S: n/a");
        } else {
            if (listing.isInfinite()) {
                sb.append(ChatColor.WHITE).append("S: +$").append(df.format(listing.getSellPrice()));
            } else {
                sb.append(ChatColor.GREEN).append("Curr. S: +$").append(df.format(listing.getSellPrice()));
                sb.append(ChatColor.GREEN).append(" | +$").append(df.format(listing.getTotalSellPrice(64)));
                sb.append(ChatColor.GREEN).append(" | +$").append(df.format(listing.getTotalSellPrice(640)));
            }
        }
        sb.append("\n");
    }

    private void buildInventory(StringBuilder sb, ItemListing listing) {
        if (listing.isInfinite()) {
            sb.append(ChatColor.WHITE).append(" Inv: inf ");
        } else {
            sb.append(ChatColor.WHITE).append(" Inv: ").append(listing.getInventory());
        }
        sb.append("\n");
    }

    private void replyWithListings(CommandSender sender, StringBuilder sb, int page) {
        ChatPage cp = ChatPaginator.paginate(sb.toString(), page, ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, 9);

        sender.sendMessage("[AdminMarket][Page "+page+" of " + cp.getTotalPages()+"]");
        sender.sendMessage("Use /shop price [item] for more price details.");
        sender.sendMessage("Prices: <per 1 unit> | <per 1 stack> | <per 10 stacks>");
        for(String s : cp.getLines()) {
            sender.sendMessage(s);
        }
    }

    public boolean listCommand(CommandSender sender, String[] args) {
        int page;
        String sortBy = "abc";
        String sortOrder = "asc";
    /*
     * listCommand Input Validation
     */
        try {
            page = args.length == 1 ?  1 : Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }

        if(args.length == 4){
            if (args[3].equalsIgnoreCase("desc")){
                sortOrder = "desc";
            }
            if (args[2].equalsIgnoreCase("price")){
                sortBy = "price";
            }
        }
    /*
     * listCommand Execution
     */
        StringBuilder sb = new StringBuilder();
        List<ItemListing> listings = getListings(sortBy, sortOrder);

        for(ItemListing listing : listings) {
            sb.append(ChatColor.GRAY).append(listing.getMaterialAsString());
            sb.append(ChatColor.BLUE).append(" Base: $").append(df.format(listing.getBasePrice()));
            sb.append(ChatColor.AQUA).append(" VAT: $").append(listing.getValueAddedTax());
            buildInventory(sb, listing);
            buildBuyPrice(sb, listing);
            buildSellPrice(sb, listing);
        }

        replyWithListings(sender, sb, page);

        return true;
    }

    public boolean quoteCommand(CommandSender sender, String[] args) {
        int amount;
        Material material = CommandUtil.materialFactory(args[1]);
        StringBuilder sb = new StringBuilder();

        /*
         * addCommand Input Validation
         */
        if (material == null) { sender.sendMessage("Item not in the shop!"); return false; }
        try {
            amount =  Integer.parseInt(args[2]);
        } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }
        ItemListing listing = listingDao.findItemListing(material);
        sb.append(ChatColor.WHITE).append("Price quote for "+amount+" "+material.toString()+"\n");
        sb.append(ChatColor.RED).append("Curr. B: +$").append(df.format(listing.getTotalBuyPrice(amount)));
        sb.append(ChatColor.WHITE).append(" | ");
        sb.append(ChatColor.GREEN).append("S: +$").append(df.format(listing.getTotalSellPrice(amount)));

        sender.sendMessage(sb.toString());

        return true;
    }
}
