/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command.actions;

import com.wjbolles.AdminMarket;
import com.wjbolles.command.CommandUtil;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.economy.EconomyWrapper;
import com.wjbolles.eco.model.ItemListing;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.DecimalFormat;
// import java.util.logging.Logger;

public class TransactionActions {
    private final AdminMarket plugin;
    //private Logger log;
    private final DecimalFormat df = new DecimalFormat("#.00");
    private final ItemListingDao listingDao;
    private final EconomyWrapper economyWrapper;
    private final Server server;

    public TransactionActions(AdminMarket plugin) {
        this.plugin = plugin;
        this.listingDao = plugin.getListingDao();
        this.economyWrapper = plugin.getEconomyWrapper();
        this.server = plugin.getServer();

        this.df.setGroupingUsed(true);
        this.df.setGroupingSize(3);
    }

    private boolean transactionPermitted(Player player, ItemListing listing, int amount) {
        if (listing == null) {
            player.sendMessage(ChatColor.RED + "This item is not in the shop.");
            return false;
        }

        if (!listing.isInfinite() && listing.getInventory() == 0) {
            player.sendMessage(ChatColor.RED + "This item is out of stock.");
            return false;
        }

        double playerBalance = economyWrapper.getBalance(player);
        if (playerBalance < listing.getTotalBuyPrice(amount)) {
            player.sendMessage(ChatColor.RED + "You cannot afford this item.");
            return false;
        }
        return true;
    }

    private int updatePlayerInventoryWithPurchase(Player player, ItemListing listing, int amount){
        // Working with inventories is difficult. It's easier to just add them
        // one at a time
        // and check if we've run out of room than parse the inventory first.
        ItemStack individualItem = new ItemStack(listing.getMaterial(), 1);

        int amountActuallyPurchased;
        for (amountActuallyPurchased = 0; amountActuallyPurchased < amount; amountActuallyPurchased++) {
            // If it is not empty, it ran out of space and was unable to add
            // anymore.
            // Use the amount purchased to adjust the inventory and charge the
            // player.
            if (!player.getInventory().addItem(individualItem).isEmpty()) {
                break;
            }
        }
        return amountActuallyPurchased;
    }

    public void buyItems(Player player, Material material, int requestedAmount) throws Exception {
        ItemListing listing = listingDao.findItemListing(material);
        if(!transactionPermitted(player, listing, requestedAmount)) { return; }

        // Don't oversell the inventory
        if (!listing.isInfinite() && listing.getInventory() < requestedAmount) {
            requestedAmount = listing.getInventory();
        }

        int amountActuallyPurchased = updatePlayerInventoryWithPurchase(player, listing, requestedAmount);
        if (amountActuallyPurchased == 0) {
            player.sendMessage(ChatColor.RED  + "There is no room in your inventory!");
            return;
        }
        
        double subTotal = listing.getTotalBuyPrice(amountActuallyPurchased);
        if (!listing.isInfinite()) { // Don't remove inventory until the full price is assessed
            listing.removeInventory(amountActuallyPurchased);
        }
        double salesTax = subTotal * plugin.getPluginConfig().getSalesTax();
        double valueAddedTax = listing.getValueAddedTax() * amountActuallyPurchased;
        double totalCost = subTotal + salesTax + valueAddedTax;

        String treasury = plugin.getPluginConfig().getTreasuryAccount();
        economyWrapper.withdraw(player, totalCost);
        economyWrapper.deposit(treasury, totalCost);
        
        player.sendMessage(
            ChatColor.GRAY + listing.getMaterialAsString() + ChatColor.WHITE +
                " Purchased: " + ChatColor.BLUE + amountActuallyPurchased + ChatColor.WHITE +
                " Subtotal: "  + ChatColor.RED + "$" + df.format(subTotal)
        );
        player.sendMessage(
                "Sales Tax: "+ ChatColor.RED +"$" + df.format(salesTax) + ChatColor.WHITE + " " +
                "VAT: "+ ChatColor.RED +"$" + df.format(valueAddedTax));
        player.sendMessage(
                "Total: "+ ChatColor.RED +"$" + df.format(totalCost)
        );

        listingDao.updateItemListing(listing);
    }

    public void sellHand(Player player) throws Exception {
        ItemStack hand = player.getInventory().getItemInMainHand();
        Material material = hand.getType();

        ItemListing listing = listingDao.findItemListing(material);

        if (listing == null || CommandUtil.safeDoubleEqualsZero(listing.getSellPrice())) {
            player.sendMessage("This item is not in the shop.");
            return;
        }

        double serverBalance = economyWrapper.getBalance(plugin.getPluginConfig().getTreasuryAccount());
        double totalCost = listing.getTotalSellPrice(hand.getAmount());
        double originalPrice = listing.getSellPrice();

        if (!listing.isInfinite()) {
            if (serverBalance < totalCost) {
                player.sendMessage(ChatColor.RED + "The server treasury cannot afford this transaction.");
                return;
            } else {
                economyWrapper.withdraw(plugin.getPluginConfig().getTreasuryAccount(), totalCost);
            }

            listing.addInventory(hand.getAmount());
        }

        economyWrapper.deposit(player, totalCost);
        
        // Clear hand
        player.getInventory().setItemInMainHand(null);
        player.sendMessage("Sold for: " + ChatColor.GREEN + "+$" + df.format(totalCost));
        
        if (!listing.isInfinite()) {
            notifyPriceChange(material, originalPrice, listing.getSellPrice());
        }
        listingDao.updateItemListing(listing);
    }

    private void notifyPriceChange(Material material, double originalPrice, double basePrice) {
        String label = material.toString();
        
        double difference = basePrice - originalPrice;
        String msg;
        
        if (difference < 0) {
            msg = label + " price change! " + ChatColor.RED + df.format(difference);
        } else {
            msg = label + " price change! " + ChatColor.GREEN + "+" + df.format(difference);
        }
        if(difference > 0.1) {
            server.broadcastMessage(msg);
        }
    }

    public void sellAll(Player player) throws Exception {

        PlayerInventory inventory = player.getInventory();
        
        double totalSold = 0;

        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack == null) {
                continue;
            }

            double serverBalance = economyWrapper.getBalance(plugin.getPluginConfig().getTreasuryAccount());

            ItemListing listing = listingDao.findItemListing(stack.getType());
            
            if (listing == null || CommandUtil.safeDoubleEqualsZero(listing.getSellPrice())) {
                continue;
            }
            
            double subTotal = listing.getTotalSellPrice(stack.getAmount());
            
            if (!listing.isInfinite()) {
                if (serverBalance < subTotal) {
                    player.sendMessage(ChatColor.RED + "The server treasury cannot afford this transaction.");
                    break;
                } else {
                    economyWrapper.withdraw(plugin.getPluginConfig().getTreasuryAccount(), subTotal);
                }

                listing.addInventory(stack.getAmount());
            }
            
            // Delete item from inventory slot
            inventory.setItem(i, null);
            totalSold += subTotal;
            listingDao.updateItemListing(listing);
        }

        economyWrapper.deposit(player, totalSold);
        player.sendMessage("Sold for: " + ChatColor.GREEN + "+$" + df.format(totalSold));
    }

    public void sellItem(Player player, Material material, int amount) {
        ItemListing listing = listingDao.findItemListing(material);

        if (listing == null) {
            player.sendMessage(ChatColor.RED + "This item is not in the shop.");
            return;
        }

        double serverBalance = economyWrapper.getBalance(plugin.getPluginConfig().getTreasuryAccount());

        // TODO: Find out how much the server CAN afford if possible
        if (!listing.isInfinite()) {
            if (serverBalance < listing.getTotalSellPrice(amount)) {
                player.sendMessage(ChatColor.RED + "The server treasury cannot afford this transaction.");
                return;
            }
        }

        // Working with inventories is difficult. It's easier to just remove them
        // one at a time
        ItemStack individualItem = new ItemStack(material, 1);

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
            return;
        }
        double originalPrice = listing.getSellPrice();
        listing.addInventory(amountSold);
        
        double totalSold = listing.getTotalSellPrice(amountSold);

        economyWrapper.deposit(player, totalSold);
        if (!listing.isInfinite()) {
            economyWrapper.withdraw(plugin.getPluginConfig().getTreasuryAccount(), totalSold);
        }
        
        player.sendMessage("Sold: " + amountSold + " for: "
                + ChatColor.GREEN + "+$"
                + df.format(totalSold));
        
        if (!listing.isInfinite()) {
            notifyPriceChange(material, originalPrice, listing.getSellPrice());
        }
    }
}
