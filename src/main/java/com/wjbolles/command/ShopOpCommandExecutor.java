/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

import com.wjbolles.AdminMarket;

import com.wjbolles.eco.model.ItemListing;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.dao.ItemListingYamlDao;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class ShopOpCommandExecutor implements CommandExecutor {
    private AdminMarket plugin;
    private TransactionCommands tm;
    private ItemListingDao listingDao;
    public ShopOpCommandExecutor(AdminMarket plugin) {
        this.plugin = plugin;
        this.tm = plugin.getTransactionCommands();
        this.listingDao = plugin.getListingDao();
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (args[0].equalsIgnoreCase("add")) {
                return addCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("remove")) {
                return removeCommand(sender, args);
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
        if(args.length != 4) {
            sender.sendMessage("Usage: /shopop update equilibrium <type> <number>");
            return true;
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
        try {
            ItemListing listing = listingDao.findItemListing(stack);
            listing.setEquilibrium(equilibrium);
            listingDao.updateItemListing(listing);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("An unexpected error occurred.");
        }
        sender.sendMessage("Successfully updated.");
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
        if(args.length != 4) {
            sender.sendMessage("Usage: /shopop update baseprice <type> <number>");
            return true;
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
        try {
            ItemListing listing = listingDao.findItemListing(stack);
            listing.setBasePrice(basePrice);
            listingDao.updateItemListing(listing);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("An unexpected error occurred.");
        }
        sender.sendMessage("Successfully updated.");
        return true;
    }
    
    private boolean updateInventory(CommandSender sender, String[] args) {
        // TODO: Permissions this later
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to do this!");
            return false;
        }
        if(args.length != 4) {
            sender.sendMessage("Usage: /shopop update inventory <type> <number>");
            return true;
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

        try {
            ItemListing listing = listingDao.findItemListing(stack);
            listing.setInventory(inventory);
            listingDao.updateItemListing(listing);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("An unexpected error occurred.");
        }
        sender.sendMessage("Successfully updated.");
        return true;
    }

    private boolean removeCommand(CommandSender sender, String[] args) {
        // TODO: Permissions this later
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to do this!");
        }

        if(args.length != 2) {
            sender.sendMessage("Usage: /shopop remove <type>");
            return true;
        }

        // Args
        String itemStack = args[1];

        ItemStack stack = CommandUtil.parseItemStack(itemStack);
        if (stack == null) {
            sender.sendMessage("Item type not found!");
            return false;
        }

        try {
            ItemListingDao listingDao = plugin.getListingDao();
            if(listingDao.findItemListing(stack) != null) {
                tm.removeItems(stack);
            } else {
                sender.sendMessage("No items found to remove.");
            }
            sender.sendMessage("Successfully removed.");
        } catch (Exception e) {
            sender.sendMessage("An unexpected error has occurred.");
            e.printStackTrace();
        }
        return true;
    }

    private boolean addCommand(CommandSender sender, String[] args) {
        // TODO: Permissions this later
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to do this!");
        }

        if(args.length != 4) {
            sender.sendMessage("Usage: /shopop add <type> <basePrice> <isinfinite>");
            return true;
        }

        double basePrice = 0;
        boolean isInfinite = false;

        // Args
        String itemStack = args[1];
        try {
            basePrice = Double.parseDouble(args[2]);
            isInfinite = Boolean.parseBoolean(args[3]);
        } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }
        
        ItemStack stack = CommandUtil.parseItemStack(itemStack);
        if (stack == null) {
            sender.sendMessage("Item not in the shop!");
            return false;
        }

        try {
            ItemListingDao listingDao = plugin.getListingDao();
            if(listingDao.findItemListing(stack) == null) {
                tm.addItems(stack, basePrice, isInfinite);
            } else {
                sender.sendMessage("Item is already in shop.");
            }
        } catch (Exception e) {
            sender.sendMessage("An unexpected error has occurred.");
            e.printStackTrace();
        }
        sender.sendMessage("Successfully added.");
        return true;
    }
}