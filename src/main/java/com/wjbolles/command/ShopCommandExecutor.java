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
    private QueryCommands lm;
    private TransactionCommands transactionCommands;

    public ShopCommandExecutor(AdminMarket plugin) {
        this.lm = plugin.getListingManager();
        this.transactionCommands = plugin.getTransactionCommands();
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

        try {
            if(args.length < 2 || args.length > 3) {
                sender.sendMessage("Usage: /shop sell [hand|all|<type> <number>]");
                return true;
            }
            if (args[1].equalsIgnoreCase("hand")) {
                if(args.length != 2) {
                    sender.sendMessage("Usage: /shop sell hand");
                }
                transactionCommands.sellHand((Player) sender);
                return true;
            } else if (args[1].equalsIgnoreCase("all")) {
                if(args.length != 2) {
                    sender.sendMessage("Usage: /shop sell all");
                }
                transactionCommands.sellAll((Player) sender);
                return true;
            } else {
                if(args.length != 3) {
                    sender.sendMessage("Usage: /shop sell <type> <number>");
                    return true;
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
                transactionCommands.sellItem((Player) sender, stack, amount);
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("An unexpected error occurred");
        }
        return true;
    }

    private boolean buyCommand(CommandSender sender, String[] args) {
        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage(ChatColor.RED + "Only players can do this command!");
            return false;
        }

        if(args.length != 3) {
            sender.sendMessage("Usage: /shop sell <type> <number>");
            return true;
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

        try {
            transactionCommands.buyItems((Player) sender, stack, amount);
        }catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("An unexpected error occurred");
        }
        return true;
    }
}
