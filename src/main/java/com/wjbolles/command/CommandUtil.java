/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

import java.math.BigDecimal;

import com.wjbolles.tools.PreloadedAliasesManager;

import org.bukkit.inventory.ItemStack;

public class CommandUtil {
    @SuppressWarnings("deprecation")
    public static ItemStack parseItemStack(String item) {
        ItemStack stack = null;
        
        if (item.contains(":")) {
            try {
                int itemId = Integer.parseInt(item.split(":")[0]);
                short durability = Short.parseShort(item.split(":")[1]);
                stack = new ItemStack(itemId, 1, durability);
            } catch (Exception e){
                // Not interested in logging NumberFormatExceptions
            }
        } else {
            stack = PreloadedAliasesManager.aliasMap.get(item.toLowerCase());
        }
        
        return stack;
    }
    
    public static boolean safeDoubleEqualsZero(double d) {
        return (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(d)) == 0);
    }
}
