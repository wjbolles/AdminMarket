/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

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

public class QueryCommands {
    private AdminMarket plugin;
    private Logger log;
    private DecimalFormat df = new DecimalFormat("#.00");
    private HashMap<String, ItemListing> listings = new HashMap<String, ItemListing>();
    private ItemListingDao listingDao;
    public QueryCommands(AdminMarket plugin) {
        this.plugin = plugin;
        this.log = plugin.getLog();
        log.info("Loading items...");
        listingDao = new ItemListingYamlDao(plugin);
        listingDao.loadItems();
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
        List<String> keys = new ArrayList<String>(listings.keySet());
        
        for(String key : keys) {
            ItemInfo info = Items.itemByStack(listings.get(key).getStack());
            String label = info.toString().replaceAll(" " , "");
            Double buyPrice = listings.get(key).getBuyPrice();
            Double basePrice = listings.get(key).getBasePrice();
            Double sellPrice = listings.get(key).getSellPrice();
            int inventory = listings.get(key).getInventory();
            boolean isInfinite = listings.get(key).isInfinite();
            
            sb.append(ChatColor.GRAY + label);
            if (CommandUtil.safeDoubleEqualsZero(buyPrice)) {
                sb.append(ChatColor.WHITE + " B: n/a");
            } else {
                if (isInfinite || plugin.getPluginConfig().shouldUseFloatingPrices() == false) {
                    sb.append(ChatColor.WHITE + " B: -$" + df.format(buyPrice));
                } else if (buyPrice < basePrice) {
                    sb.append(ChatColor.GREEN + " B: -$" + df.format(buyPrice));
                } else {
                    sb.append(ChatColor.RED + " B: -$" + df.format(buyPrice));
                }
                
            }
            if (CommandUtil.safeDoubleEqualsZero(sellPrice)) {
                sb.append(ChatColor.WHITE + " S: n/a");
            } else {
                if (isInfinite) {
                    sb.append(ChatColor.WHITE + " S: +$" + df.format(sellPrice));
                } else if (sellPrice > basePrice) {
                    sb.append(ChatColor.GREEN + " S: +$" + df.format(sellPrice));
                } else {
                    sb.append(ChatColor.RED + " S: +$" + df.format(sellPrice)); 
                }
            }
            if (isInfinite) {
                sb.append(ChatColor.WHITE + " Inv: inf");
            } else {
                sb.append(ChatColor.WHITE + " Inv: "+inventory);
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
            sb.append(ChatColor.WHITE + "B: n/a");
        } else {
            if (listing.isInfinite()) {
                sb.append(ChatColor.WHITE + "B: -$" + df.format(buyPrice));
            } else if (buyPrice < basePrice) {
                sb.append(ChatColor.GREEN + "B: -$" + df.format(buyPrice));
            } else {
                sb.append(ChatColor.RED + " B: -$" + df.format(buyPrice));
            }
            
        }
        if (CommandUtil.safeDoubleEqualsZero(sellPrice)) {
            sb.append(ChatColor.WHITE + " S: n/a");
        } else {
            if (listing.isInfinite()) {
                sb.append(ChatColor.WHITE + " S: +$" + df.format(sellPrice));
            } else if (sellPrice > basePrice) {
                sb.append(ChatColor.GREEN + " S: +$" + df.format(sellPrice));
            } else {
                sb.append(ChatColor.RED + " S: +$" + df.format(sellPrice)); 
            }
        }
                
        sender.sendMessage(sb.toString());
        
        return true;
    }
}
