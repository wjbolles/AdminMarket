/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command.actions;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.wjbolles.AdminMarket;
import com.wjbolles.command.CommandUtil;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.model.ItemListing;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;

public class QueryActions {
    private AdminMarket plugin;
    private DecimalFormat df = new DecimalFormat("#.00");
    private ItemListingDao listingDao;
    public QueryActions(AdminMarket plugin) {
        this.plugin = plugin;
        listingDao = plugin.getListingDao();

        df.setGroupingUsed(true);
        df.setGroupingSize(3);
    }

    private List<ItemListing> getListings(String sortBy, String sortOrder){
        Map<String, ItemListing> listings = listingDao.getAllListings();

        Comparator<ItemListing> compareBy = null;

        if(sortBy.equals("abc")){
            compareBy = Comparator.comparing(ItemListing::getMaterialAsString)
                    .thenComparing(ItemListing::getMaterialAsString);
        } else { // price
            compareBy = Comparator.comparing(ItemListing::getBuyPrice)
                    .thenComparing(ItemListing::getBuyPrice);
        }

        List<ItemListing> sortedListings = listings.values().stream()
                .sorted(compareBy)
                .collect(Collectors.toList()); // asc

        if(sortOrder.equals("desc")){
            Collections.reverse(sortedListings);
        }
int a = 0;
        return sortedListings;
    }

    private void buildBuyPrice(StringBuilder sb, ItemListing listing){
        sb.append(ChatColor.GRAY).append(listing.getMaterialAsString());
        if (CommandUtil.safeDoubleEqualsZero(listing.getBuyPrice())) {
            sb.append(ChatColor.WHITE).append(" B: n/a");
        } else {
            if (listing.isInfinite() ||
                    !plugin.getPluginConfig().getUseFloatingPrices() ||
                    CommandUtil.safeDoubleEqualsZero(listing.getBuyPrice()-listing.getBasePrice())) {
                sb.append(ChatColor.WHITE).append(" B: -$").append(df.format(listing.getBuyPrice()));
            } else if (listing.getBuyPrice() < listing.getBasePrice()) {
                sb.append(ChatColor.GREEN).append(" B: -$").append(df.format(listing.getBuyPrice()));
            } else {
                sb.append(ChatColor.RED).append(" B: -$").append(df.format(listing.getBuyPrice()));
            }
        }
    }

    private void buildSellPrice(StringBuilder sb, ItemListing listing){
        if (CommandUtil.safeDoubleEqualsZero(listing.getSellPrice())) {
            sb.append(ChatColor.WHITE).append(" S: n/a");
        } else {
            if (listing.isInfinite() || CommandUtil.safeDoubleEqualsZero(listing.getSellPrice()-listing.getBasePrice())) {
                sb.append(ChatColor.WHITE).append(" S: +$").append(df.format(listing.getSellPrice()));
            } else if (listing.getSellPrice() > listing.getBasePrice()) {
                sb.append(ChatColor.GREEN).append(" S: +$").append(df.format(listing.getSellPrice()));
            } else {
                sb.append(ChatColor.RED).append(" S: +$").append(df.format(listing.getSellPrice()));
            }
        }
    }

    private void buildInventory(StringBuilder sb, ItemListing listing) {
        if (listing.isInfinite()) {
            sb.append(ChatColor.WHITE).append(" Inv: inf");
        } else {
            sb.append(ChatColor.WHITE).append(" Inv: ").append(listing.getInventory());
        }
        sb.append("\n");
    }

    private void replyWithListings(CommandSender sender, StringBuilder sb, int page) {
        ChatPage cp = ChatPaginator.paginate(sb.toString(), page);
        sender.sendMessage("[AdminMarket][Page "+page+" of " + cp.getTotalPages()+"] Prices frequently change.");
        sender.sendMessage("Use /shop price [item] for more price details.");
        for(String s : cp.getLines()) {
            sender.sendMessage(s);
        }
    }

    public boolean listCommand(CommandSender sender, String[] args) {
        int page = 1;
        String sortBy = "abc";
        String sortOrder = "asc";

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

        StringBuilder sb = new StringBuilder();
        List<ItemListing> listings = getListings(sortBy, sortOrder);

        for(ItemListing listing : listings) {
            buildBuyPrice(sb, listing);
            buildSellPrice(sb, listing);
            buildInventory(sb, listing);
        }

        replyWithListings(sender, sb, page);

        return true;
    }

    public boolean priceCommand(CommandSender sender, String[] args) {
        Material material = CommandUtil.materialFactory(args[1]);
        
        if (material == null) {
            sender.sendMessage(ChatColor.RED + "Item not recognized!");
            return false;
        }

        ItemListing listing = listingDao.findItemListing(material);

        if (listing == null) {
            sender.sendMessage(ChatColor.RED + "This item is not in the shop.");
            return true;
        }

        double buyPrice = listing.getBuyPrice();
        double sellPrice = listing.getSellPrice();
        double basePrice = listing.getBasePrice();
        
        String msg = material.toString() + " Inv: " + ChatColor.WHITE + listing.getInventory();
        sender.sendMessage(msg);
        msg = ChatColor.BLUE + "Base: $" + basePrice + ChatColor.WHITE + " Tax: " + ChatColor.RED + plugin.getPluginConfig().getSalesTax()*100 + "%";
        sender.sendMessage(msg);
        
        StringBuilder sb = new StringBuilder();
        if (CommandUtil.safeDoubleEqualsZero(buyPrice)) {
            sb.append(ChatColor.WHITE).append("B: n/a");
        } else {
            if (listing.isInfinite()) {
                sb.append(ChatColor.WHITE).append("B: -$").append(df.format(buyPrice));
            } else if (buyPrice < basePrice) {
                sb.append(ChatColor.GREEN).append("B: -$").append(df.format(buyPrice));
            } else {
                sb.append(ChatColor.RED).append(" B: -$").append(df.format(buyPrice));
            }
            
        }
        if (CommandUtil.safeDoubleEqualsZero(sellPrice)) {
            sb.append(ChatColor.WHITE).append(" S: n/a");
        } else {
            if (listing.isInfinite()) {
                sb.append(ChatColor.WHITE).append(" S: +$").append(df.format(sellPrice));
            } else if (sellPrice > basePrice) {
                sb.append(ChatColor.GREEN).append(" S: +$").append(df.format(sellPrice));
            } else {
                sb.append(ChatColor.RED).append(" S: +$").append(df.format(sellPrice));
            }
        }
                
        sender.sendMessage(sb.toString());
        
        return true;
    }
}
