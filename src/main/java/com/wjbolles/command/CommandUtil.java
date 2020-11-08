/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

import java.math.BigDecimal;

// import com.wjbolles.tools.PreloadedAliasesManager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CommandUtil {
    public static ItemStack parseItemStack(String item) {
        ItemStack stack = null;
        
        Material material = Material.getMaterial(item);
        /*
        if (material == null) {
            stack = PreloadedAliasesManager.aliasMap.get(item.toLowerCase());
        }
        */
        stack = new ItemStack(material, 1);

        return stack;
    }
    
    public static boolean safeDoubleEqualsZero(double d) {
        return (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(d)) == 0);
    }

}
