/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

import com.wjbolles.AdminMarket;
import com.wjbolles.eco.ItemListing;
import com.wjbolles.eco.ListingManager;
import com.wjbolles.eco.TransactionManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopOpCommandExecutor implements CommandExecutor {
    private AdminMarket plugin;
    private ListingManager lm;
    private TransactionManager tm;
    
    public ShopOpCommandExecutor(AdminMarket plugin) {
        this.plugin = plugin;
        this.lm = plugin.getListingManager();
        this.tm = plugin.getTransactionManager();
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (args[0].equalsIgnoreCase("add")) {
                return addCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("help")) {
                return helpCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("update")) {
                if (args[1].equalsIgnoreCase("inventory")) {
                    return updateInventory(sender, args);
                } else if (args[1].equalsIgnoreCase("baseprice")) {
                    return updateBasePrice(sender, args);
                } else if (args[1].equalsIgnoreCase("equilibrium")) {
                    return updateEquilibrium(sender, args);
                } 
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sender.sendMessage("Command not recognized!");
        }
        return false;
    }
    
    private boolean updateEquilibrium(CommandSender sender, String[] args) {
        // TODO: Permissions this later
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to do this!");
            return false;
        }
        ItemStack stack = CommandUtil.parseItemStack(args[2]);
        if (stack == null) {
            sender.sendMessage("Item not in the shop!");
            return false;
        }
        int equilibrium;
        try {
            equilibrium = Integer.parseInt(args[3]);
        } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }
        ItemListing listing = lm.getListing(stack);
        listing.setEquilibrium(equilibrium);
        return true;
    }
    private boolean helpCommand(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return false;
    }
    private boolean updateBasePrice(CommandSender sender, String[] args) {
        // TODO: Permissions this later
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to do this!");
            return false;
        }
        ItemStack stack = CommandUtil.parseItemStack(args[2]);
        if (stack == null) {
            sender.sendMessage("Item not in the shop!");
            return false;
        }
        double basePrice;
        try {
            basePrice = Double.parseDouble(args[3]);
        } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }
        ItemListing listing = lm.getListing(stack);
        listing.setBasePrice(basePrice);
        return true;
    }
    
    private boolean updateInventory(CommandSender sender, String[] args) {
        // TODO: Permissions this later
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to do this!");
            return false;
        }
        ItemStack stack = CommandUtil.parseItemStack(args[2]);
        if (stack == null) {
            sender.sendMessage("Item not in the shop!");
            return false;
        }
        int inventory;
        try {
            inventory = Integer.parseInt(args[3]);
        } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }
        ItemListing listing = lm.getListing(stack);
        listing.setInventory(inventory);
        return true;
    }

    private boolean addCommand(CommandSender sender, String[] args) {
        // TODO: Permissions this later
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to do this!");
        }
        
        ItemStack stack = CommandUtil.parseItemStack(args[1]);
        if (stack == null) {
            sender.sendMessage("Item not in the shop!");
            return false;
        }
        double basePrice = 0;
        boolean isInfinite = false;
        
        try {
            basePrice = Double.parseDouble(args[2]);
            isInfinite = Boolean.parseBoolean(args[3]);
        } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }
        
        return tm.addItems((Player) sender, stack, basePrice, isInfinite);
    }
}