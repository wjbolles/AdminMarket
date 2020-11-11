/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command.executors;

import com.wjbolles.AdminMarket;

import com.wjbolles.command.CommandUtil;
import com.wjbolles.command.actions.ImportActions;
import com.wjbolles.command.actions.ItemListingActions;
import com.wjbolles.eco.model.ItemListing;
import com.wjbolles.eco.dao.ItemListingDao;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class ShopOpCommandExecutor implements CommandExecutor {
    private AdminMarket plugin;
    private ItemListingActions itemListingActions;
    private ItemListingDao listingDao;

    public ShopOpCommandExecutor(AdminMarket plugin) {
        this.plugin = plugin;
        this.itemListingActions = plugin.getItemListingActions();
        this.listingDao = plugin.getListingDao();
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // TODO: Permissions this later
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to do this!");
            return false;
        }

        try {
            if (args[0].equalsIgnoreCase("add")) {
                if(args.length != 4) {
                    sender.sendMessage("Usage: /shopop add <type> <basePrice> <isinfinite>");
                    return true;
                }
                return addCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("remove")) {
                if(args.length != 2) {
                    sender.sendMessage("Usage: /shopop remove <type>");
                    return true;
                }
                return removeCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("help")) {
                return helpCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("import")) {
                return importCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("update")) {
                if (args[1].equalsIgnoreCase("inventory")) {
                    if(args.length != 4) {
                        sender.sendMessage("Usage: /shopop update inventory <type> <number>");
                        return true;
                    }
                    return updateInventory(sender, args);
                } else if (args[1].equalsIgnoreCase("baseprice")) {
                    if(args.length != 4) {
                        sender.sendMessage("Usage: /shopop update baseprice <type> <number>");
                        return true;
                    }
                    return updateBasePrice(sender, args);
                } else if (args[1].equalsIgnoreCase("equilibrium")) {
                    if(args.length != 4) {
                        sender.sendMessage("Usage: /shopop update equilibrium <type> <number>");
                        return true;
                    }
                    return updateEquilibrium(sender, args);
                } 
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sender.sendMessage("Command not recognized!");
        }
        return false;
    }

    private boolean addCommand(CommandSender sender, String[] args) {
        double basePrice = 0;
        boolean isInfinite = false;

        Material material = CommandUtil.materialFactory(args[1]);
        if (!CommandUtil.validStoreItem(material)) {
            sender.sendMessage("Only raw goods are permitted in the store");
            return true;
        }
        try {
            basePrice = Double.parseDouble(args[2]);
            isInfinite = Boolean.parseBoolean(args[3]);
        } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }

        if (material == null) {
            sender.sendMessage("Item not in the shop!");
            return false;
        }

        try {
            ItemListingDao listingDao = plugin.getListingDao();
            if(listingDao.findItemListing(material) == null) {
                itemListingActions.addItems(material, basePrice, isInfinite);
            } else {
                sender.sendMessage("Item is already in shop.");
                return true;
            }
        } catch (Exception e) {
            sender.sendMessage("An unexpected error has occurred.");
            e.printStackTrace();
        }
        sender.sendMessage("Successfully added.");
        return true;
    }
    private boolean removeCommand(CommandSender sender, String[] args) {
        Material material = CommandUtil.materialFactory(args[1]);
        if (material == null) {
            sender.sendMessage("Item type not found!");
            return false;
        }

        try {
            ItemListingDao listingDao = plugin.getListingDao();
            if(listingDao.findItemListing(material) != null) {
                itemListingActions.removeItems(material);
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
    private boolean helpCommand(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return false;
    }
    private boolean importCommand(CommandSender sender, String[] args) {
        boolean result = new ImportActions(plugin).importItemListings();
        if (result) {
            sender.sendMessage("Items imported");
        } else {
            sender.sendMessage("Items not imported, review the template file and try again.");
        }
        return result;
    }
    private boolean updateInventory(CommandSender sender, String[] args) {
        Material material = CommandUtil.materialFactory(args[2]);
        if (material == null) {
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
            ItemListing listing = listingDao.findItemListing(material);
            listing.setInventory(inventory);
            listingDao.updateItemListing(listing);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("An unexpected error occurred.");
        }
        sender.sendMessage("Successfully updated.");
        return true;
    }
    private boolean updateBasePrice(CommandSender sender, String[] args) {
        Material material = CommandUtil.materialFactory(args[2]);
        if (material == null) {
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
            ItemListing listing = listingDao.findItemListing(material);
            listing.setBasePrice(basePrice);
            listingDao.updateItemListing(listing);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("An unexpected error occurred.");
        }
        sender.sendMessage("Successfully updated.");
        return true;
    }
    private boolean updateEquilibrium(CommandSender sender, String[] args) {
        Material material = CommandUtil.materialFactory(args[2]);
        if (material == null) {
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
            ItemListing listing = listingDao.findItemListing(material);
            listing.setEquilibrium(equilibrium);
            listingDao.updateItemListing(listing);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("An unexpected error occurred.");
        }
        sender.sendMessage("Successfully updated.");
        return true;
    }
}