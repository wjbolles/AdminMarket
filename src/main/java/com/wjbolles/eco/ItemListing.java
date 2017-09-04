/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco;

import java.io.File;

import com.wjbolles.Config;
import com.wjbolles.adminmarket.utils.Consts;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ItemListing {
    
    private boolean isInfinite;
    private int inventory;
    private final ItemStack stack;
    private double basePrice;
    private double valueAddedTax;
    private int equilibrium;
    private YamlConfiguration itemcfg = new YamlConfiguration();
    private File itemConf;
    private Config config;
    
    public ItemListing(ItemStack stack, boolean isInfinite, Config config) throws Exception {
        this.stack = stack;
        this.isInfinite = isInfinite;
        this.basePrice = 0;
        this.inventory = 0;
        this.valueAddedTax = 0;
        this.equilibrium = 1000;
        this.config = config;
        
        initNewConf();
    }
    
    public ItemListing(File itemConf, Config config) throws Exception {
        this.itemConf = itemConf;
        itemcfg.load(itemConf);
        
        this.stack = new ItemStack(Material.getMaterial(itemcfg.getString("material")), 1, (short) itemcfg.getInt("durability"));
        this.isInfinite = itemcfg.getBoolean("isInfinite");
        this.basePrice = itemcfg.getDouble("basePrice");
        this.inventory = itemcfg.getInt("inventory");
        this.equilibrium = itemcfg.getInt("equilibrium");
        this.valueAddedTax = itemcfg.getDouble("valueAddedTax");
        this.config = config;
    }
    
    private void initNewConf() throws Exception {
        itemConf = new File(Consts.PLUGIN_ITEMS_DIR + File.separatorChar +
                this.stack.getType()+"-"+
                this.stack.getDurability()+".yml");
        
        if(itemConf.exists()) {
            throw new IllegalStateException();
        }
        itemConf.createNewFile();
        
        itemcfg.load(itemConf);
        itemcfg.set("material", stack.getType().toString());
        itemcfg.set("durability", stack.getDurability());
        itemcfg.set("isInfinite", Boolean.valueOf(this.isInfinite));
        itemcfg.set("inventory", Integer.valueOf(this.inventory));
        itemcfg.set("equilibrium", Integer.valueOf(this.equilibrium));
        itemcfg.set("basePrice",  Double.valueOf(this.basePrice));
        itemcfg.save(itemConf);    
    }

    public boolean buyItem(int amount) {
        if (isInfinite) {
            return true;
        } else if (amount < inventory) {
            inventory -= amount;
            
            try {
                itemcfg.load(itemConf);
                itemcfg.set("inventory", Integer.valueOf(this.inventory));
                itemcfg.save(itemConf);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        
        return false;
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public void setInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
        try {
            itemcfg.load(itemConf);
            itemcfg.set("isInfinite", Boolean.valueOf(this.isInfinite));
            itemcfg.save(itemConf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getInventory() {
        return inventory;
    }

    public void addInventory(int inventory) {
        this.inventory += inventory;
        try {
            itemcfg.load(itemConf);
            itemcfg.set("inventory", Integer.valueOf(this.inventory));
            itemcfg.save(itemConf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeInventory(int inventory) {
        if (this.inventory - inventory < 0) {
            throw new IllegalArgumentException();
        }
        this.inventory -= inventory;
        try {
            itemcfg.load(itemConf);
            itemcfg.set("inventory", Integer.valueOf(this.inventory));
            itemcfg.save(itemConf);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
        try {
            itemcfg.load(itemConf);
            itemcfg.set("basePrice", Double.valueOf(this.basePrice));
            itemcfg.save(itemConf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public double getBasePrice() {
        return this.basePrice;
    }
    
    public ItemStack getStack() {
        return stack;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
        try {
            itemcfg.load(itemConf);
            itemcfg.set("inventory", Integer.valueOf(this.inventory));
            itemcfg.save(itemConf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getEquilibrium() {
        return this.equilibrium;
    }
    
    public void setEquilibrium(int equilibrium) {
        this.equilibrium = equilibrium;
        try {
            itemcfg.load(itemConf);
            itemcfg.set("equilibrium", Integer.valueOf(this.equilibrium));
            itemcfg.save(itemConf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deleteConf() {
        itemConf.delete();
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
