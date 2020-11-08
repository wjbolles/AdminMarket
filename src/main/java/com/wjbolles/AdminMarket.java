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

import com.wjbolles.command.actions.ItemListingActions;
import com.wjbolles.command.actions.QueryActions;
import com.wjbolles.command.executors.ShopOpCommandExecutor;
import com.wjbolles.command.actions.TransactionActions;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.dao.ItemListingYamlDao;
import com.wjbolles.eco.economy.EconomyWrapper;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.wjbolles.adminmarket.utils.Consts;
import com.wjbolles.command.executors.ShopCommandExecutor;
import com.wjbolles.eco.economy.VaultEconomyWrapperImpl;
// import com.wjbolles.tools.PreloadedAliasesManager;
import org.bukkit.plugin.java.JavaPluginLoader;

public class AdminMarket extends JavaPlugin {
    
    private EconomyWrapper economy;
    private ItemListingDao listingDao;

    private TransactionActions transactionActions;
    private QueryActions queryActions;
    private ItemListingActions itemListingActions;

    /**
     * This is for unit testing.
     * @param loader The PluginLoader to use.
     * @param description The Description file to use.
     * @param dataFolder The folder that other datafiles can be found in.
     * @param file The location of the plugin.
     */
    public AdminMarket(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        createDirectory();
        setupConfig();

        // PreloadedAliasesManager.initialize(this);

        this.economy = getEconomyWrapper();
        this.listingDao = new ItemListingYamlDao(this);
        this.queryActions = new QueryActions(this);
        this.transactionActions = new TransactionActions(this);
        this.itemListingActions = new ItemListingActions(this);

        setupCommands();
    }

    private void setupConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void setupCommands() {
        this.getCommand("shop").setExecutor(new ShopCommandExecutor(this));
        this.getCommand("shopop").setExecutor(new ShopOpCommandExecutor(this));
    }

    public void setEconomyWrapper(EconomyWrapper economy) {
        this.economy = economy;
    }

    public void createDirectory() {
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
        saveConfig();
    }
    
    public EconomyWrapper getEconomyWrapper() {
        if (economy == null){
            this.economy = new VaultEconomyWrapperImpl(this);
        }
        return economy;
    }
    
    public QueryActions getQueryActions() {
        return queryActions;
    }

    public void setListingDao(ItemListingDao listingDao) {
        this.listingDao = listingDao;
    }

    public void setTransactionActions(TransactionActions transactionActions) {
        this.transactionActions = transactionActions;
    }

    public void setItemListingActions(ItemListingActions itemListingActions) {
        this.itemListingActions = itemListingActions;
    }

    public void setQueryActions(QueryActions queryActions) {
        this.queryActions = queryActions;
    }

    public TransactionActions getTransactionActions() {
        return transactionActions;
    }

    public Logger getLog() {
        return super.getLogger();
    }

    public ItemListingDao getListingDao(){return listingDao;}

    public AdminMarketConfig getPluginConfig() {
        return new Config(this);
    }

    public ItemListingActions getItemListingActions() { return itemListingActions; }
}
