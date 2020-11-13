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
import com.wjbolles.command.actions.QueryActions;
import com.wjbolles.command.actions.TransactionActions;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ShopCommandExecutor implements CommandExecutor {
    private final QueryActions queryActions;
    private final TransactionActions transactionActions;

    public ShopCommandExecutor(AdminMarket plugin) {
        this.queryActions = plugin.getQueryActions();
        this.transactionActions = plugin.getTransactionActions();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            switch(args[0].toLowerCase()) {
                case "buy":
                    return buyCommand(sender, args);
                case "sell":
                    return sellCommand(sender, args);
                case "list":
                    return queryActions.listCommand(sender, args);
                case "quote":
                    return queryActions.quoteCommand(sender, args);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sender.sendMessage("Command not recognized!");
        } catch (Exception e) {
            sender.sendMessage("An unexpected error has occurred.");
            e.printStackTrace();
        }
        return false;
    }

    private boolean sellCommand(CommandSender sender, String[] args) throws Exception {
        if(sender instanceof ConsoleCommandSender){ // Console has nothing to sell
            sender.sendMessage(ChatColor.RED + "Only players can do this command!");
            return true;
        }
    /*
     * sellCommand Input Validation
     */
        if(args.length < 2 || args.length > 3) {
            sender.sendMessage("Usage: /shop sell [hand|all|<type> <number>]");
            return true;
        }
    /*
     * sellCommand Execution
     */
        switch(args[1].toLowerCase()) {
            case "hand":
                if(args.length != 2) { sender.sendMessage("Usage: /shop sell hand"); }
                transactionActions.sellHand((Player) sender);
                break;
            case "all":
                if(args.length != 2) { sender.sendMessage("Usage: /shop sell all"); }
                transactionActions.sellAll((Player) sender);
                break;
            default:
                if(args.length != 3) { sender.sendMessage("Usage: /shop sell <type> <number>");}
                return sellMaterialCommand(sender, args);
        }
        return true;
    }

    private boolean sellMaterialCommand(CommandSender sender, String[] args){
        Material material = CommandUtil.materialFactory(args[1]);
        int amount;
    /*
     * sellMaterialCommand Input Validation
     */
        if (material == null) { sender.sendMessage(ChatColor.RED + "Item not recognized!"); return false; }
        try {
            amount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Amount not recognized!");
            return false;
        }
    /*
     * sellMaterialCommand Execution
     */
        transactionActions.sellItem((Player) sender, material, amount);
        return true;
    }

    private boolean buyCommand(CommandSender sender, String[] args) throws Exception {
        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage(ChatColor.RED + "Only players can do this command!");
            return false;
        }

        Material material = CommandUtil.materialFactory(args[1]);
        int amount;
    /*
     * buyCommand Input Validation
     */
        if(args.length != 3) { sender.sendMessage("Usage: /shop buy <type> <number>"); return true; }
        if(material == null) { sender.sendMessage(ChatColor.RED + "Item not recognized!"); return false; }
        try {
            amount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Amount not recognized!");
            return false;
        }
    /*
     * buyCommand Execution
     */
        transactionActions.buyItems((Player) sender, material, amount);

        return true;
    }
}
