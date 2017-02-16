/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles;

import java.io.File;
import java.util.logging.Logger;

import com.wjbolles.command.ShopOpCommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import com.wjbolles.adminmarket.utils.Consts;
import com.wjbolles.command.ShopCommandExecutor;
import com.wjbolles.eco.EconomyWrapper;
import com.wjbolles.eco.ListingManager;
import com.wjbolles.eco.TransactionManager;
import com.wjbolles.eco.impl.VaultWrapper;
import com.wjbolles.tools.PreloadedAliasesManager;

public class AdminMarket extends JavaPlugin {
    
    private EconomyWrapper economy;
    private TransactionManager transactionManager;
    private ListingManager listingManager;
    private Config config;
    
    @Override
    public void onEnable() {
        createDirectory();
        economy = new VaultWrapper(this);
        config = new Config();
        
        PreloadedAliasesManager.initialize(this);
        listingManager = new ListingManager(this);
        transactionManager = new TransactionManager(this);
        
        setupCommands();
    }

    private void setupCommands() {
        this.getCommand("shop").setExecutor(new ShopCommandExecutor(this));
        this.getCommand("shopop").setExecutor(new ShopOpCommandExecutor(this));
    }

    public static void createDirectory() {
        File confDir = new File(Consts.PLUGIN_CONF_DIR);
        if (!confDir.exists()) {
            confDir.mkdir();
        }
        
        File itemsDir = new File((Consts.PLUGIN_ITEMS_DIR));
        if (!itemsDir.exists()) {
            itemsDir.mkdir();
        }    
    }

    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    }
    
    public EconomyWrapper getEconomy() {
        return economy;
    }
    
    public ListingManager getListingManager() {
        return listingManager;
    }
    
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public Logger getLog() {
        return super.getLogger();
    }
    
    public Config getPluginConfig() {
        return config;
    }
}
