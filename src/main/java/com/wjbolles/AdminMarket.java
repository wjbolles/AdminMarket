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

import com.wjbolles.command.QueryCommands;
import com.wjbolles.command.ShopOpCommandExecutor;
import com.wjbolles.command.TransactionCommands;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.dao.ItemListingYamlDao;
import com.wjbolles.eco.economy.EconomyWrapper;
import org.bukkit.plugin.java.JavaPlugin;

import com.wjbolles.adminmarket.utils.Consts;
import com.wjbolles.command.ShopCommandExecutor;
import com.wjbolles.eco.economy.VaultWrapperImpl;
import com.wjbolles.tools.PreloadedAliasesManager;

public class AdminMarket extends JavaPlugin {
    
    private EconomyWrapper economy;
    private TransactionCommands transactionCommands;
    private QueryCommands listingManager;
    private Config config;
    private ItemListingDao listingDao;
    
    @Override
    public void onEnable() {
        createDirectory();

        economy = new VaultWrapperImpl(this);
        config = new Config();
        
        PreloadedAliasesManager.initialize(this);
        listingManager = new QueryCommands(this);
        transactionCommands = new TransactionCommands(this);

        listingDao = new ItemListingYamlDao(this);
        listingDao.loadItems();

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
    
    public EconomyWrapper getEconomyWrapper() {
        return economy;
    }
    
    public QueryCommands getListingManager() {
        return listingManager;
    }
    
    public TransactionCommands getTransactionCommands() {
        return transactionCommands;
    }

    public Logger getLog() {
        return super.getLogger();
    }

    public ItemListingDao getListingDao(){return listingDao;}

    public Config getPluginConfig() {return config;}

}
