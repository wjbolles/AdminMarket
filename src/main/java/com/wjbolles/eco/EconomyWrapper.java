/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco;

import org.bukkit.OfflinePlayer;

public interface EconomyWrapper {
    
    public double getBalance(String player);
    public double getBalance(OfflinePlayer player);
    
    public void deposit(String player, double amount);
    public void deposit(OfflinePlayer player, double amount);
    
    public void withdraw(String player, double amount);
    public void withdraw(OfflinePlayer player, double amount);
}
