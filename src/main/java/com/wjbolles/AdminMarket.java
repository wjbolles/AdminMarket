/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles;

import java.io.File;
import java.util.logging.Logger;

import com.wjbolles.command.actions.ImportActions;
import com.wjbolles.command.actions.ItemListingActions;
import com.wjbolles.command.actions.QueryActions;
import com.wjbolles.command.executors.ShopOpCommandExecutor;
import com.wjbolles.command.actions.TransactionActions;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.dao.SqliteItemListingDao;
import com.wjbolles.eco.dao.YamlItemListingDao;
import com.wjbolles.eco.economy.EconomyWrapper;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.wjbolles.adminmarket.utils.Constants;
import com.wjbolles.command.executors.ShopCommandExecutor;
import com.wjbolles.eco.economy.VaultEconomyWrapper;
import org.bukkit.plugin.java.JavaPluginLoader;

public class AdminMarket extends JavaPlugin {

    private EconomyWrapper economy;
    private ItemListingDao listingDao;

    private TransactionActions transactionActions;
    private QueryActions queryActions;
    private ItemListingActions itemListingActions;
    private ImportActions importActions;

    /**
     * Constructor used by Minecraft
     */
    public AdminMarket() {
        super();
    }

    /**
     * Constructor used for unit testing
     *
     * @param loader      The PluginLoader to use.
     * @param description The Description file to use.
     * @param dataFolder  The folder that other datafiles can be found in.
     * @param file        The location of the plugin.
     */
    public AdminMarket(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        createDirectory();
        setupConfig();

        this.economy = getEconomyWrapper();
        this.listingDao = itemListingDaoFactory();
        this.queryActions = new QueryActions(this);
        this.transactionActions = new TransactionActions(this);
        this.itemListingActions = new ItemListingActions(this);
        this.importActions = new ImportActions(this);

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

    private ItemListingDao itemListingDaoFactory() {
        if (this.getPluginConfig().getStorage().equals("sqlite")) {
            return new SqliteItemListingDao(this);
        } else {
            return new YamlItemListingDao(this);
        }
    }

    public void createDirectory() {
        File confDir = new File(Constants.PLUGIN_CONF_DIR);
        if (!confDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            confDir.mkdir();
        }

        File itemsDir = new File((Constants.PLUGIN_ITEMS_DIR));
        if (!itemsDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            itemsDir.mkdir();
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public EconomyWrapper getEconomyWrapper() {
        if (economy == null) {
            this.economy = new VaultEconomyWrapper(this);
        }
        return economy;
    }

    public void setEconomyWrapper(EconomyWrapper economy) {
        this.economy = economy;
    }

    public QueryActions getQueryActions() {
        return queryActions;
    }

    public void setQueryActions(QueryActions queryActions) {
        this.queryActions = queryActions;
    }

    public ImportActions getImportActions() {
        return importActions;
    }

    public void setImportActions(ImportActions importActions) {
        this.importActions = importActions;
    }

    public TransactionActions getTransactionActions() {
        return transactionActions;
    }

    public void setTransactionActions(TransactionActions transactionActions) {
        this.transactionActions = transactionActions;
    }

    public Logger getLog() {
        return super.getLogger();
    }

    public ItemListingDao getListingDao() {
        return listingDao;
    }

    public void setListingDao(ItemListingDao listingDao) {
        this.listingDao = listingDao;
    }

    public AdminMarketConfig getPluginConfig() {
        return new Config(this);
    }

    public ItemListingActions getItemListingActions() {
        return itemListingActions;
    }

    public void setItemListingActions(ItemListingActions itemListingActions) {
        this.itemListingActions = itemListingActions;
    }
}
