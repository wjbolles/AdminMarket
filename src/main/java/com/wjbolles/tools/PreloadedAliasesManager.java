/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;

import com.wjbolles.AdminMarket;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PreloadedAliasesManager {

    private static AdminMarket plugin;
    public static HashMap<String, ItemStack> aliasMap = new HashMap<String, ItemStack>();
    private static final String ALIAS_STORE = "/com/wjbolles/data/preloaded_aliases.txt";
    private static BufferedReader reader;

    public static void initialize(AdminMarket plugin) {
        PreloadedAliasesManager.plugin = plugin;
        plugin.getLog().info("Loading aliases...");
        loadAliasFile();
        parseFile();
    }

    private static void loadAliasFile() {
        InputStream is = PreloadedAliasesManager.class.getResourceAsStream(ALIAS_STORE);
        InputStreamReader stream = new InputStreamReader(is);
        reader = new BufferedReader(stream);
    }

    private static void parseFile() {
        String line;
        try {
            line = reader.readLine();
            while (line != null) { 
                String alias = line.split(",")[0];
                String material  = line.split(",")[1];
                short durability = Short.parseShort(line.split(",")[2]);
                ItemStack stack = new ItemStack(Material.getMaterial(material), 1, durability);
                aliasMap.put(alias, stack);
                
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            plugin.getLog().log(Level.WARNING, "Loading aliases failed!", e);
        }
    }
}
