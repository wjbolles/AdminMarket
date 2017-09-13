/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command.executors;

import com.wjbolles.AdminMarket;
import com.wjbolles.command.CommandUtil;
import com.wjbolles.command.actions.QueryActions;
import com.wjbolles.command.actions.TransactionActions;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopCommandExecutor implements CommandExecutor {
    private QueryActions queryActions;
    private TransactionActions transactionActions;

    public ShopCommandExecutor(AdminMarket plugin) {
        this.queryActions = plugin.getQueryActions();
        this.transactionActions = plugin.getTransactionActions();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (args[0].equalsIgnoreCase("buy")) {
                return buyCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("sell")) {
                return sellCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("list")) {
                return queryActions.listCommand(sender, args);
            } else if (args[0].equalsIgnoreCase("price")) {
                return queryActions.priceCommand(sender, args);
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
                transactionActions.sellHand((Player) sender);
                return true;
            } else if (args[1].equalsIgnoreCase("all")) {
                if(args.length != 2) {
                    sender.sendMessage("Usage: /shop sell all");
                }
                transactionActions.sellAll((Player) sender);
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
                transactionActions.sellItem((Player) sender, stack, amount);
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
            transactionActions.buyItems((Player) sender, stack, amount);
        }catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("An unexpected error occurred");
        }
        return true;
    }
}
