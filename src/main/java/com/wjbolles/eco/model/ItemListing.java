/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco.model;

import java.io.File;

import com.wjbolles.Config;
import com.wjbolles.adminmarket.utils.Consts;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ItemListing {
    
    private final ItemStack stack;
    private boolean isInfinite;
    private int inventory;
    private double basePrice;
    private double valueAddedTax;
    private int equilibrium;
    private Config config;

    public ItemListing(ItemStack stack, boolean isInfinite, Config config) throws Exception {
        this.stack = stack;
        this.isInfinite = isInfinite;
        this.basePrice = 0;
        this.inventory = 0;
        this.valueAddedTax = 0;
        this.equilibrium = 1000;
        this.config = config;
    }

    public double getValueAddedTax() {
        return valueAddedTax;
    }

    public void setValueAddedTax(double valueAddedTax) {
        this.valueAddedTax = valueAddedTax;
    }

    public boolean buyItem(int amount) {
        if (isInfinite) {
            return true;
        } else if (amount < inventory) {
            inventory -= amount;
            return true;
        }
        
        return false;
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public void setInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public void addInventory(int inventory) {
        this.inventory += inventory;
    }
    
    public void removeInventory(int inventory) {
        if (this.inventory - inventory < 0) {
            throw new IllegalArgumentException();
        }
        this.inventory -= inventory;
    }
    
    public double getSellPrice() {
        return getSellPrice(this.inventory);
    }

    private double getSellPrice(int inventory) {
        if(config.shouldUseFloatingPrices() && !isInfinite) {
            double slope = (basePrice - basePrice * config.getMaxPercentBasePrice())/-equilibrium;
            double sellPrice = basePrice * config.getMaxPercentBasePrice() - slope * inventory;
            if (sellPrice < basePrice * 0.40) {
                sellPrice = basePrice * 0.40;
            }
            return sellPrice;
        } else {
            return basePrice;
        }
    }
    
    public double getBuyPrice() {
        return getBuyPrice(this.inventory);
    }

    private double getBuyPrice(int inventory) {
        double buyPrice = 0;
        if(config.shouldUseFloatingPrices() && !isInfinite) {
            double slope = (basePrice - basePrice * config.getMaxPercentBasePrice())/-equilibrium;
            buyPrice = basePrice * config.getMaxPercentBasePrice() - slope * inventory;
            if (buyPrice < basePrice * 0.40) {
                buyPrice = basePrice * 0.40;
            }
        } else {
            buyPrice = basePrice;
        }
        buyPrice = buyPrice + buyPrice * config.getSalesTax();
        buyPrice = buyPrice + buyPrice * valueAddedTax;
        return buyPrice;
    }
    
    public double getBasePrice() {
        return this.basePrice;
    }
    
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public ItemStack getStack() {
        return stack;
    }
    
    public int getEquilibrium() {
        return this.equilibrium;
    }
    
    public void setEquilibrium(int equilibrium) {
        this.equilibrium = equilibrium;
    }

    public double getTotalSellPrice(int amount) {
        if (isInfinite) {
            return getSellPrice() * amount;
        }
        
        int inventory = this.getInventory();
        
        double total = 0;
        for(int i = 0; i < amount; i++) {
            total += getSellPrice(inventory);
            inventory++;
        }
        
        return total;
    }
    
    public double getTotalBuyPrice(int amount) {
        if (isInfinite) {
            return getBuyPrice() * amount;
        }
        
        int inventory = this.getInventory();
        
        double total = 0;
        for(int i = 0; i < amount; i++) {
            if (inventory == 0) {
                break;
            }
            total += getBuyPrice(inventory);
            inventory--;
        }
        
        return total;
    }
}