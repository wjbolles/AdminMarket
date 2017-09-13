/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command.actions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.wjbolles.AdminMarket;
import com.wjbolles.command.CommandUtil;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.dao.ItemListingYamlDao;
import com.wjbolles.eco.model.ItemListing;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;

public class QueryActions {
    private AdminMarket plugin;
    private Logger log;
    private DecimalFormat df = new DecimalFormat("#.00");
    private ItemListingDao listingDao;
    public QueryActions(AdminMarket plugin) {
        this.plugin = plugin;
        listingDao = plugin.getListingDao();

        df.setGroupingUsed(true);
        df.setGroupingSize(3);
    }

    public boolean listCommand(CommandSender sender, String[] args) {
        int page;
        if (args.length == 1) {
            page = 1;
        } else {
            try {
                page = Integer.parseInt(args[1]);
            } catch (Exception e) {
                sender.sendMessage("Parameter not recognized!");
                return false;
            }        
        }
        
        StringBuilder sb = new StringBuilder();
        HashMap<String, ItemListing> listings = listingDao.getAllListings();
        List<String> keys = new ArrayList<String>(listings.keySet());
        
        for(String key : keys) {
            ItemInfo info = Items.itemByStack(listings.get(key).getStack());
            String label = info.toString().replaceAll(" " , "");
            Double buyPrice = listings.get(key).getBuyPrice();
            Double basePrice = listings.get(key).getBasePrice();
            Double sellPrice = listings.get(key).getSellPrice();
            int inventory = listings.get(key).getInventory();
            boolean isInfinite = listings.get(key).isInfinite();
            
            sb.append(ChatColor.GRAY).append(label);
            if (CommandUtil.safeDoubleEqualsZero(buyPrice)) {
                sb.append(ChatColor.WHITE).append(" B: n/a");
            } else {
                if (isInfinite || !plugin.getPluginConfig().getShouldUseFloatingPrices()) {
                    sb.append(ChatColor.WHITE).append(" B: -$").append(df.format(buyPrice));
                } else if (buyPrice < basePrice) {
                    sb.append(ChatColor.GREEN).append(" B: -$").append(df.format(buyPrice));
                } else {
                    sb.append(ChatColor.RED).append(" B: -$").append(df.format(buyPrice));
                }
                
            }
            if (CommandUtil.safeDoubleEqualsZero(sellPrice)) {
                sb.append(ChatColor.WHITE).append(" S: n/a");
            } else {
                if (isInfinite) {
                    sb.append(ChatColor.WHITE).append(" S: +$").append(df.format(sellPrice));
                } else if (sellPrice > basePrice) {
                    sb.append(ChatColor.GREEN).append(" S: +$").append(df.format(sellPrice));
                } else {
                    sb.append(ChatColor.RED).append(" S: +$").append(df.format(sellPrice));
                }
            }
            if (isInfinite) {
                sb.append(ChatColor.WHITE).append(" Inv: inf");
            } else {
                sb.append(ChatColor.WHITE).append(" Inv: ").append(inventory);
            }
            sb.append("\n");
        }
        
        ChatPage cp = ChatPaginator.paginate(sb.toString(), page);
        
        sender.sendMessage("[AdminMarket][Page "+page+" of " + cp.getTotalPages()+"] Prices frequently change.");
        sender.sendMessage("Use /shop price [item] for more price details.");
        for(String s : cp.getLines()) {
            sender.sendMessage(s);
        }
        
        return true;
    }
    
    public boolean priceCommand(CommandSender sender, String[] args) {
        ItemStack stack = CommandUtil.parseItemStack(args[1]);
        
        if (stack == null) {
            sender.sendMessage(ChatColor.RED + "Item not recognized!");
            return false;
        }

        ItemListing listing = listingDao.findItemListing(stack);

        if (listing == null) {
            sender.sendMessage(ChatColor.RED + "This item is not in the shop.");
            return true;
        }
        
        ItemInfo info = Items.itemByStack(stack);
        String label = info.toString().replaceAll(" " , "");
        
        double buyPrice = listing.getBuyPrice();
        double sellPrice = listing.getSellPrice();
        double basePrice = listing.getBasePrice();
        
        String msg = label + " Inv: " + ChatColor.WHITE + listing.getInventory();
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
