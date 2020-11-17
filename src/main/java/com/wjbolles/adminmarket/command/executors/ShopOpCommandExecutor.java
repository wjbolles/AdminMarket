/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.adminmarket.command.executors;

import com.wjbolles.adminmarket.AdminMarket;

import com.wjbolles.adminmarket.command.CommandUtil;
import com.wjbolles.adminmarket.command.actions.ImportActions;
import com.wjbolles.adminmarket.command.actions.ItemListingActions;
import com.wjbolles.adminmarket.eco.dao.ItemListingDao;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to do this!");
            return false;
        }

        try {
            switch(args[0].toLowerCase()) {
                case "add":
                    if(args.length != 4) {
                        sender.sendMessage("Usage: /shopop add <type> <basePrice> <isinfinite>");
                        return true;
                    }
                    return addCommand(sender, args);
                case "remove":
                    if(args.length != 2) {
                        sender.sendMessage("Usage: /shopop remove <type>");
                        return true;
                    }
                    return removeCommand(sender, args);
                case "help":
                    return helpCommand(sender, args);
                case "import":
                    return importCommand(sender, args);
                case "conf":
                    return confCommand(sender, args);
                case "update":
                    switch(args[1].toLowerCase()) {
                        case "inventory":
                            if (args.length != 4) {
                                sender.sendMessage("Usage: /shopop update inventory <type> <number>");
                                return true;
                            }
                            return updateInventoryCommand(sender, args);
                        case "baseprice":
                            if (args.length != 4) {
                                sender.sendMessage("Usage: /shopop update baseprice <type> <number>");
                                return true;
                            }
                            return updateBasePriceCommand(sender, args);
                        case "equilibrium":
                            if (args.length != 4) {
                                sender.sendMessage("Usage: /shopop update equilibrium <type> <number>");
                                return true;
                            }
                            return updateEquilibriumCommand(sender, args);
                    }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sender.sendMessage("Command not recognized!");
        } catch (Exception e) {
            sender.sendMessage("An unexpected error has occurred.");
            e.printStackTrace();
        }
        return false;
    }

    private boolean confCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage("Usage: /shopop conf <setting> <number>");
            return true;
        }
        double salesTax = Double.parseDouble(args[2]);
        switch(args[1].toLowerCase()) {
            case "salestax":
                plugin.getPluginConfig().setSalesTax(salesTax);
                sender.sendMessage("Sales Tax updated to " +salesTax*100+"%");
                return true;
        }
        return true;
    }

    private boolean addCommand(CommandSender sender, String[] args) throws Exception {
        double basePrice = 0;
        boolean isInfinite = false;
        Material material = CommandUtil.materialFactory(args[1]);

    /*
     * addCommand Input Validation
     */
        if (material == null) { sender.sendMessage("Item not in the shop!"); return false; }
        if (!CommandUtil.isValidStoreItem(material)) {
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

    /*
     * addCommand Execution
     */
        if(listingDao.listingExists(material)) {
            sender.sendMessage("Item is already in shop.");
        } else {
            itemListingActions.addItem(material, basePrice, isInfinite);
            sender.sendMessage("Successfully added.");
        }

        return true;
    }

    private boolean removeCommand(CommandSender sender, String[] args) throws Exception {
        Material material = CommandUtil.materialFactory(args[1]);
    /*
     * removeCommand Input Validation
     */
        if (material == null) { sender.sendMessage("Item not in the shop!"); return false; }
    /*
     * removeCommand Execution
     */
        if(!listingDao.listingExists(material)) {
            itemListingActions.removeItem(material);
            sender.sendMessage("Successfully removed.");
        } else {
            sender.sendMessage("No items found to remove.");
        }

        return true;
    }

    private boolean helpCommand(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean importCommand(CommandSender sender, String[] args) {
        if (new ImportActions(plugin).importItemListings()) {
            sender.sendMessage("Items imported");
        } else {
            sender.sendMessage("Items not imported, review the template file and try again.");
        }
        return true;
    }
    private boolean updateInventoryCommand(CommandSender sender, String[] args) throws Exception {
        Material material = CommandUtil.materialFactory(args[2]);
        int inventory;
    /*
     * updateInventoryCommand Input Validation
     */
        if (material == null) { sender.sendMessage("Item not in the shop!"); return false; }

        try {
            inventory = Integer.parseInt(args[3]);
        } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }
    /*
     * updateInventoryCommand Execution
     */
        itemListingActions.updateItemInventory(material, inventory);

        sender.sendMessage("Successfully updated.");
        return true;
    }

    private boolean updateBasePriceCommand(CommandSender sender, String[] args) throws Exception {
        Material material = CommandUtil.materialFactory(args[2]);
        double basePrice;
    /*
     * updateBasePriceCommand Input Validation
     */
        if (material == null) { sender.sendMessage("Item not in the shop!"); return false; }

        try {
            basePrice = Double.parseDouble(args[3]);
        } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }
    /*
     * updateBasePriceCommand Execution
     */
        itemListingActions.updateItemBasePrice(material, basePrice);

        sender.sendMessage("Successfully updated.");
        return true;
    }
    private boolean updateEquilibriumCommand(CommandSender sender, String[] args) throws Exception {
        Material material = CommandUtil.materialFactory(args[2]);
        int equilibrium;
    /*
     * updateEquilibriumCommand Input Validation
     */
        if (material == null) { sender.sendMessage("Item not in the shop!"); return false; }
        try { equilibrium = Integer.parseInt(args[3]); } catch (Exception e) {
            sender.sendMessage("Parameter not recognized!");
            return false;
        }
    /*
     * updateEquilibriumCommand Execution
     */
        itemListingActions.updateItemEquilibrium(material, equilibrium);

        sender.sendMessage("Successfully updated.");
        return true;
    }
}