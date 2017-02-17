/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco;

import java.text.DecimalFormat;
import java.util.logging.Logger;

import com.wjbolles.AdminMarket;
import com.wjbolles.Config;
import com.wjbolles.command.CommandUtil;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class TransactionManager {
    private AdminMarket plugin;
    private Config config;
    private ListingManager lm;
    private Logger log;
    private final DecimalFormat df = new DecimalFormat("#.00");

    public TransactionManager (AdminMarket plugin) {
        this.plugin = plugin;
        config = plugin.getPluginConfig();
        lm = plugin.getListingManager();
        log = plugin.getLog();
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
    }

    // Note: This stack should have an amount of 1.
    public boolean buyItems(Player player, ItemStack stack, int amount) {
        ItemListing listing = lm.getListing(stack);

        if (listing == null) {
            player.sendMessage(ChatColor.RED + "This item is not in the shop.");
            return true;
        }

        if (!listing.isInfinite() && listing.getInventory() == 0) {
            player.sendMessage(ChatColor.RED + "This item is out of stock.");
            return true;
        }

        double playerBalance = plugin.getEconomy().getBalance(player);
        if (playerBalance < listing.getTotalBuyPrice(amount)) {
            player.sendMessage(ChatColor.RED + "You cannot afford this item.");
            return true;
        }

        // Don't oversell the inventory
        if (!listing.isInfinite() && listing.getInventory() < amount) {
            amount = listing.getInventory();
        }

        // Working with inventories is difficult. It's easier to just add them
        // one at a time
        // and check if we've run out of room than parse the inventory first.
        ItemStack individualItem = new ItemStack(stack.getType(), 1, stack.getDurability());

        int amountPurchased;
        for (amountPurchased = 0; amountPurchased < amount; amountPurchased++) {
            // If it is not empty, it ran out of space and was unable to add
            // anymore.
            // Use the amount purchased to adjust the inventory and charge the
            // player.
            if (!player.getInventory().addItem(individualItem).isEmpty()) {
                break;
            }
        }

        if (amountPurchased == 0) {
            player.sendMessage(ChatColor.RED  + "There is no room in your inventory!");
            return true;
        }
        
        double totalCost = listing.getTotalBuyPrice(amountPurchased);
        double originalPrice = listing.getBuyPrice();
        if (!listing.isInfinite()) {
            listing.removeInventory(amountPurchased);
        }
        
        plugin.getEconomy().withdraw(player, totalCost);
        plugin.getEconomy().deposit(config.getTreasuryAccount(), totalCost);
        
        player.sendMessage(
            "Purchased: " + amountPurchased + " for: "
            + ChatColor.RED + "-$"
            + df.format(amountPurchased * listing.getBuyPrice())
        );
        
        if (!listing.isInfinite()) {
            notifyPriceChange(stack, originalPrice, listing.getBuyPrice());
        }
        
        return true;
    }

    public boolean sellHand(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        ItemStack stack = new ItemStack(hand.getType(), 1, hand.getDurability());

        ItemListing listing = lm.getListing(stack);

        if (listing == null || CommandUtil.safeDoubleEqualsZero(listing.getSellPrice())) {
            player.sendMessage("This item is not in the shop.");
            return true;
        }

        double serverBalance = plugin.getEconomy().getBalance(config.getTreasuryAccount());
        double totalCost = listing.getTotalSellPrice(hand.getAmount());
        double originalPrice = listing.getSellPrice();
        
        if (!listing.isInfinite()) {
            if (serverBalance < totalCost) {
                player.sendMessage(ChatColor.RED + "The server treasury cannot afford this transaction.");
                return true;
            } else {
                plugin.getEconomy().withdraw(config.getTreasuryAccount(), totalCost);
            }

            listing.addInventory(hand.getAmount());
        }

        plugin.getEconomy().deposit(player, totalCost);
        
        // Clear hand
        player.getInventory().setItemInMainHand(null);
        player.sendMessage("Sold for: " + ChatColor.GREEN + "+$" + df.format(totalCost));
        
        if (!listing.isInfinite()) {
            notifyPriceChange(stack, originalPrice, listing.getSellPrice());
        }
        
        return true;
    }

    private void notifyPriceChange(ItemStack stack, double originalPrice, double basePrice) {
        ItemInfo info = Items.itemByStack(stack);
        String label = info.toString().replaceAll(" " , "");
        
        double difference = basePrice - originalPrice;
        String msg;
        
        if (difference < 0) {
            msg = label + " price change! " + ChatColor.RED + df.format(difference);
        } else {
            msg = label + " price change! " + ChatColor.GREEN + "+" + df.format(difference);
        }
        if(difference > 0.1) {
            plugin.getServer().broadcastMessage(msg);
        }
    }

    public boolean addItems(Player sender, ItemStack stack,
            double basePrice, boolean isInfinite) {
        try {
            ItemListing listing = new ItemListing(stack, isInfinite, config);
            listing.setBasePrice(basePrice);
            lm.addListing(stack, listing);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean sellAll(Player player) {

        PlayerInventory inventory = player.getInventory();
        
        double totalSold = 0;
        
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack == null) {
                continue;
            }
            
            double serverBalance = plugin.getEconomy().getBalance(config.getTreasuryAccount());
            
            // The key for this assumes the stack has an amount of 1
            ItemListing listing = lm.getListing(new ItemStack(stack.getType(), 1, stack.getDurability()));
            
            if (listing == null || CommandUtil.safeDoubleEqualsZero(listing.getSellPrice())) {
                continue;
            }
            
            double subTotal = listing.getTotalSellPrice(stack.getAmount());
            
            if (!listing.isInfinite()) {
                if (serverBalance < subTotal) {
                    player.sendMessage(ChatColor.RED + "The server treasury cannot afford this transaction.");
                    break;
                } else {
                    plugin.getEconomy().withdraw("towny-server", subTotal);
                }

                listing.addInventory(stack.getAmount());
            }
            
            // Delete item from inventory slot
            inventory.setItem(i, null);
            totalSold += subTotal;
        }

        plugin.getEconomy().deposit(player, totalSold);
        player.sendMessage("Sold for: " + ChatColor.GREEN + "+$" + df.format(totalSold));
        return true;
    }

    public boolean sellItem(Player player, ItemStack stack, int amount) {
        ItemListing listing = lm.getListing(stack);

        if (listing == null) {
            player.sendMessage(ChatColor.RED + "This item is not in the shop.");
            return true;
        }

        double serverBalance = plugin.getEconomy().getBalance(config.getTreasuryAccount());

        // TODO: Find out how much the server CAN afford if possible
        if (!listing.isInfinite()) {
            if (serverBalance < listing.getTotalSellPrice(amount)) {
                player.sendMessage(ChatColor.RED + "The server treasury cannot afford this transaction.");
                return true;
            }
        }

        // Working with inventories is difficult. It's easier to just remove them
        // one at a time
        ItemStack individualItem = new ItemStack(stack.getType(), 1, stack.getDurability());

        int amountSold;
        for (amountSold = 0; amountSold < amount; amountSold++) {
            // If it is not empty, it ran out of items and was unable to sell
            // anymore.
            // Use the amount purchased to charge the player.
            if (!player.getInventory().removeItem(individualItem).isEmpty()) {
                break;
            }
        }

        if (amountSold == 0) {
            player.sendMessage(ChatColor.RED  + "This item is not in your inventory!");
            return true;
        }
        double originalPrice = listing.getSellPrice();
        listing.addInventory(amountSold);
        
        double totalSold = listing.getTotalSellPrice(amountSold);

        plugin.getEconomy().deposit(player, totalSold);
        if (!listing.isInfinite()) {
            plugin.getEconomy().withdraw(config.getTreasuryAccount(), totalSold);
        }
        
        player.sendMessage("Sold: " + amountSold + " for: "
                + ChatColor.GREEN + "+$"
                + df.format(totalSold));
        
        if (!listing.isInfinite()) {
            notifyPriceChange(stack, originalPrice, listing.getSellPrice());
        }
        
        return true;
    }
}
