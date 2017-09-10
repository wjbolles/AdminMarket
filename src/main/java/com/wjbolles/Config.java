/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles;

import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    public static final String SETTING_TREASURY_ACCOUNT = "treasuryAccount";
    public static final String SETTING_SALES_TAX = "salesTax";
    public static final String SETTING_MAX_PERCENT_BASE_PRICE = "maxPercentBasePrice";
    public static final String SETTING_USE_FLOATING_PRICES = "useFloatingPrices";

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

    public String getTreasuryAccount() {
        return  getConfig().getString(SETTING_TREASURY_ACCOUNT);
    }

    public double getSalesTax() {
        return getConfig().getDouble(SETTING_SALES_TAX);
    }

    public double getMaxPercentBasePrice() {
        return getConfig().getDouble(SETTING_MAX_PERCENT_BASE_PRICE);
    }

    public boolean getShouldUseFloatingPrices() {
        return getConfig().getBoolean(SETTING_USE_FLOATING_PRICES);
    }

    public void setTreasuryAccount(String treasuryAccount) {
        getConfig().set(SETTING_TREASURY_ACCOUNT, treasuryAccount);
    }

    public void setSalesTax(double salesTax) {
        getConfig().set(SETTING_SALES_TAX, salesTax);
    }

    public void setMaxPercentBasePrice(double maxPercentBasePrice) {
        getConfig().set(SETTING_MAX_PERCENT_BASE_PRICE, maxPercentBasePrice);
    }

    public void setUseFloatingPrices(boolean useFloatingPrices) {
        getConfig().set(SETTING_USE_FLOATING_PRICES, useFloatingPrices);
    }   
}
