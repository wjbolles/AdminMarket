/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

import com.wjbolles.AdminMarket;
import com.wjbolles.eco.dao.ItemListingDao;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopCommandExecutor implements CommandExecutor {
    private AdminMarket plugin;
    private QueryCommands lm;
    private TransactionCommands transactionCommands;
    private ItemListingDao listingDao;
    
    public ShopCommandExecutor(AdminMarket plugin) {
        this.plugin = plugin;
        this.lm = plugin.getListingManager();
        this.transactionCommands = plugin.getTransactionCommands();
        this.listingDao = plugin.getListingDao();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (args[0].equalsIgnoreCase("buy")) {
                return buyCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("sell")) {
                return sellCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("list")) {
                return lm.listCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("price")) {
                return lm.priceCommand(sender, args);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sender.sendMessage("Command not recognized!");
        }
        return false;
    }

    private boolean sellCommand(CommandSender sender, String[] args) {
        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage(ChatColor.RED + "Only players can do this command!");
            return false;
        }
        
        if (args[1].equalsIgnoreCase("hand")) {
            return transactionCommands.sellHand((Player) sender);
        } else if (args[1].equalsIgnoreCase("all")) {
            return transactionCommands.sellAll((Player) sender);
        } else {
            ItemStack stack = CommandUtil.parseItemStack(args[1]);
            int amount;
            
            try {
                amount = Integer.parseInt(args[2]);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Amount not recognized!");
                return false;
            }
            
            if (stack == null) {
                sender.sendMessage(ChatColor.RED + "Item not recognized!");
                return false;
            }
            
            return transactionCommands.sellItem((Player) sender, stack, amount);
        }
    }

    private boolean buyCommand(CommandSender sender, String[] args) {
        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage(ChatColor.RED + "Only players can do this command!");
            return false;
        }
        
        ItemStack stack = CommandUtil.parseItemStack(args[1]);
        int amount;
        
        try {
            amount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Amount not recognized!");
            return false;
        }
        
        if (stack == null) {
            sender.sendMessage(ChatColor.RED + "Item not recognized!");
            return false;
        }
        
        return transactionCommands.buyItems((Player) sender, stack, amount);
    }
}
