/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco.economy;

import com.wjbolles.AdminMarket;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultWrapperImpl implements EconomyWrapper {
    
    private static AdminMarket plugin;
    
    private Economy economy;
    
    public VaultWrapperImpl(AdminMarket plugin) {
        this.plugin = plugin;
        setupEconomy();
    }

    @SuppressWarnings("deprecation")
    public double getBalance(String player) {
        return economy.getBalance(player);
    }

    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    @SuppressWarnings("deprecation")
    public void deposit(String player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public void deposit(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    @SuppressWarnings("deprecation")
    public void withdraw(String player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public void withdraw(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }
    
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
