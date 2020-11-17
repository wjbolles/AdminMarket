/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.adminmarket.eco.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyWrapper {
    
    double getBalance(String player);
    double getBalance(OfflinePlayer player);
    
    void deposit(String player, double amount);
    void deposit(OfflinePlayer player, double amount);
    
    void withdraw(String player, double amount);
    void withdraw(OfflinePlayer player, double amount);
}
