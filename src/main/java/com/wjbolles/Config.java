/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles;

import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config implements AdminMarketConfig {

    public static final String SETTING_TREASURY_ACCOUNT = "treasuryAccount";
    public static final String SETTING_SALES_TAX = "salesTax";
    public static final String SETTING_MAX_PERCENT_BASE_PRICE = "maxPercentBasePrice";
    public static final String SETTING_USE_FLOATING_PRICES = "useFloatingPrices";
    public static final String SETTING_STORAGE = "storage";

    private JavaPlugin plugin;

    Config(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private MemoryConfiguration getConfig() {
        return plugin.getConfig();
    }

    ConfigurationOptions options() {
        return getConfig().options();
    }

    @Override
    public String getTreasuryAccount() {
        return  getConfig().getString(SETTING_TREASURY_ACCOUNT);
    }

    @Override
    public double getSalesTax() {
        return getConfig().getDouble(SETTING_SALES_TAX);
    }

    @Override
    public double getMaxPercentBasePrice() {
        return getConfig().getDouble(SETTING_MAX_PERCENT_BASE_PRICE);
    }

    @Override
    public boolean getUseFloatingPrices() {
        return getConfig().getBoolean(SETTING_USE_FLOATING_PRICES);
    }

    @Override
    public String getStorage() { return getConfig().getString(SETTING_STORAGE); }

    @Override
    public void setTreasuryAccount(String treasuryAccount) {
        getConfig().set(SETTING_TREASURY_ACCOUNT, treasuryAccount);
        plugin.saveConfig();
    }

    @Override
    public void setSalesTax(double salesTax) {
        getConfig().set(SETTING_SALES_TAX, salesTax);
        plugin.saveConfig();
    }

    @Override
    public void setMaxPercentBasePrice(double maxPercentBasePrice) {
        getConfig().set(SETTING_MAX_PERCENT_BASE_PRICE, maxPercentBasePrice);
        plugin.saveConfig();
    }

    @Override
    public void setUseFloatingPrices(boolean useFloatingPrices) {
        getConfig().set(SETTING_USE_FLOATING_PRICES, useFloatingPrices);
        plugin.saveConfig();
    }

    @Override
    public void setStorage(String storage) {
        getConfig().set(SETTING_USE_FLOATING_PRICES, storage);
        plugin.saveConfig();
    }
}
