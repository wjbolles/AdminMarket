/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco.model;

import com.wjbolles.AdminMarketConfig;

import org.bukkit.inventory.ItemStack;

public class ItemListing {

    private static final int DEFAULT_EQUILIBRIUM = 1000;
    private static final int DEFAULT_BASE_PRICE = 0;
    private static final int DEFAULT_VALUE_ADDED_TAX = 0;
    private static final int DEFAULT_INVENTORY = 0;

    private final ItemStack stack;
    private boolean isInfinite;
    private int inventory;
    private double basePrice;
    private double valueAddedTax;
    private int equilibrium;
    private AdminMarketConfig config;

    /**
     * @param stack
     * @param isInfinite
     * @param config
     */
    public ItemListing(ItemStack stack, boolean isInfinite, AdminMarketConfig config) {
        this.stack = stack;
        this.isInfinite = isInfinite;
        this.basePrice = DEFAULT_BASE_PRICE;
        this.inventory = DEFAULT_INVENTORY;
        this.valueAddedTax = DEFAULT_VALUE_ADDED_TAX;
        this.equilibrium = DEFAULT_EQUILIBRIUM;
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

    public void removeInventory(int inventory) throws IllegalArgumentException {
        if (this.inventory - inventory < 0) {
            throw new IllegalArgumentException();
        }
        this.inventory -= inventory;
    }

    public double getSellPrice() {
        return getSellPrice(this.inventory);
    }

    private double getSellPrice(int inventory) {
        if(config.getUseFloatingPrices() && !isInfinite) {
            double slope = (basePrice - basePrice * config.getMaxPercentBasePrice())/ -equilibrium;
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
        double buyPrice;

        if(config.getUseFloatingPrices() && !isInfinite) {
            double slope = (basePrice - basePrice * config.getMaxPercentBasePrice())/ -equilibrium;
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

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = stack.hashCode();
        result = 31 * result + (isInfinite ? 1 : 0);
        result = 31 * result + inventory;
        temp = Double.doubleToLongBits(basePrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(valueAddedTax);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + equilibrium;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemListing that = (ItemListing) o;

        if (isInfinite != that.isInfinite) return false;
        if (inventory != that.inventory) return false;
        if (Double.compare(that.basePrice, basePrice) != 0) return false;
        if (Double.compare(that.valueAddedTax, valueAddedTax) != 0) return false;
        if (equilibrium != that.equilibrium) return false;
        return stack.equals(that.stack);
    }

    @Override
    public String toString() {
        return "ItemListing{" +
                "stack=" + stack.getType().name() +
                ", isInfinite=" + isInfinite +
                ", inventory=" + inventory +
                ", basePrice=" + basePrice +
                ", valueAddedTax=" + valueAddedTax +
                ", equilibrium=" + equilibrium +
                ", config=" + config +
                '}';
    }
}