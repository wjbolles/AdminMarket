/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.After;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginManager.class, AdminMarket.class, Permission.class, Bukkit.class,
        PluginDescriptionFile.class, JavaPluginLoader.class })
public abstract class AdminMarketTest {

    protected AdminMarketSpyFactory pluginFactory = new AdminMarketSpyFactory();
    protected AdminMarket plugin = pluginFactory.getPlugin();
    protected PlayerSpyFactory playerFactory = new PlayerSpyFactory();
    protected Player player = playerFactory.getPlayer();

    public static final File pluginDirectory = new File("plugins/AdminMarket");
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        directory.delete();
    }

    @After
    public void tearDown() {
        AdminMarketSpyFactory.unregisterServer();
        // Delete the ~/plugins directory for the next test
        deleteDirectory(pluginDirectory.getParentFile());
    }
}
