/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco.economy;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;

public class BasicEconomyWrapperImpl implements EconomyWrapper {
    
    private HashMap<String, Double> accounts;
    
    public BasicEconomyWrapperImpl(HashMap<String, Double> accounts) {
        this.accounts = accounts;
    }

    public double getBalance(String player) {
        return accounts.get(player);
    }

    public double getBalance(OfflinePlayer player) {
        return accounts.get(player.getName());
    }

    public void deposit(String player, double amount) {
        accounts.put(player, accounts.get(player) + amount);
    }

    public void deposit(OfflinePlayer player, double amount) {
        accounts.put(player.getName(), accounts.get(player.getName()) + amount);
    }

    public void withdraw(String player, double amount) {
        accounts.put(player, accounts.get(player) - amount);
    }

    public void withdraw(OfflinePlayer player, double amount) {
        accounts.put(player.getName(), accounts.get(player.getName()) - amount);
    }

}
