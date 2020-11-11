/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

import java.math.BigDecimal;

import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
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

    public static boolean validStoreItem(Material material) {
        // We want to only sell raw goods
        if (EnchantmentTarget.TOOL.includes(material) ||
                EnchantmentTarget.WEARABLE.includes(material) ||
                EnchantmentTarget.WEAPON.includes(material) ||
                EnchantmentTarget.BREAKABLE.includes(material) ||
                EnchantmentTarget.ARMOR.includes(material) ||
                material.toString().contains("HORSE")) {
            return false;
        } else {
            return true;
        }
    }

    public static Material materialFactory(String name) {
        return Material.matchMaterial(name.toUpperCase(), false);
    }

}
