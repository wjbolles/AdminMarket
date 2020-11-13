/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco.model;

import com.wjbolles.AdminMarketConfig;

import org.bukkit.Material;

public class ItemListing {

    private static final int DEFAULT_EQUILIBRIUM = 1000;
    private static final int DEFAULT_BASE_PRICE = 0;
    private static final int DEFAULT_VALUE_ADDED_TAX = 0;
    private static final int DEFAULT_INVENTORY = 0;

    private final Material material;
    private boolean isInfinite;
    private int inventory;
    private double basePrice;
    private double valueAddedTax;
    private int equilibrium;
    private final AdminMarketConfig config;

    public ItemListing(Material material, boolean isInfinite, AdminMarketConfig config) {
        this.material = material;
        this.isInfinite = isInfinite;
        this.basePrice = DEFAULT_BASE_PRICE;
        this.inventory = DEFAULT_INVENTORY;
        this.valueAddedTax = DEFAULT_VALUE_ADDED_TAX;
        this.equilibrium = DEFAULT_EQUILIBRIUM;
        this.config = config;
    }

    protected ItemListing(Material material,
                       boolean isInfinite,
                       double basePrice,
                       int inventory,
                       double valueAddedTax,
                       int equilibrium,
                       AdminMarketConfig config) {
        this.material = material;
        this.isInfinite = isInfinite;
        this.basePrice = basePrice;
        this.inventory = inventory;
        this.valueAddedTax = valueAddedTax;
        this.equilibrium = equilibrium;
        this.config = config;
    }

    public double getValueAddedTax() {
        return valueAddedTax;
    }

    public void setValueAddedTax(double valueAddedTax) {
        this.valueAddedTax = valueAddedTax;
    }

    public String getMaterialAsString(){
        return material.toString();
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

    private double getCurrentFloatingPrice(double inventory) {
        double slope = (basePrice - basePrice * (1+config.getMaxPercentBasePrice()))/ -equilibrium;
        double sellPrice = basePrice * (1+config.getMaxPercentBasePrice()) - slope * inventory;

        double floor = basePrice - basePrice*config.getMaxPercentBasePrice();

        if (sellPrice < floor) {
            sellPrice = floor;
        }
        return sellPrice;
    }

    public double getSellPrice() {
        return getSellPrice(this.inventory);
    }

    private double getSellPrice(int inventory) {
        return getBuyPrice(inventory+1);
    }

    public double getBuyPrice() {
        return getBuyPrice(this.inventory);
    }

    private double getBuyPrice(int inventory) {
        if(config.getUseFloatingPrices() && !isInfinite) {
            return getCurrentFloatingPrice(inventory);
        } else {
            return basePrice;
        }
    }

    public double getBasePrice() {
        return this.basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public Material getMaterial() {
        return material;
    }

    public int getEquilibrium() {
        return this.equilibrium;
    }

    public void setEquilibrium(int equilibrium) {
        this.equilibrium = equilibrium;
    }

    private double integrate(int a_int, int b_int) {
        double a = 0.5 + a_int;    // shifting the bounds 0.5 helps ensure
        double b = 0.5 + b_int; // the calculated price is more accurate (https://introcs.cs.princeton.edu/java/93integration/TrapezoidalRule.java.html)
        long N = Math.round(Math.log((b-a)) / Math.log(2)) * 2; // Do more rounds the more inventory there is for greater precision
        if (N < 4) {
            N = 4;
        }
        double h = (b - a) / N;
        double sum = 0.5 * (getCurrentFloatingPrice(a) + getCurrentFloatingPrice(b));
        for (int i = 1; i < N; i++) {
            double x = a + h * i;
            sum = sum + getCurrentFloatingPrice(x);
        }

        return (double) Math.round(sum * h * 100)/ 100;
    }

    public double getTotalSellPrice(int amount){
        if (isInfinite) {
            return getSellPrice() * amount;
        }
        double totalPrice = integrate(inventory, inventory+amount);

        return totalPrice;
    }

    public double getTotalBuyPrice(int amount){
        if (isInfinite) {
            return getSellPrice() * amount;
        }
        double totalPrice = integrate(inventory-amount, inventory);

        return totalPrice;
    }

    // Replaced with more efficient method that uses
    // integration to approximate the total cost
    @Deprecated
    public double getTotalSellPriceLegacy(int amount) {
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

    // Replaced with more efficient method that uses
    // integration to approximate the total cost
    @Deprecated
    public double getTotalBuyPriceLegacy(int amount) {
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
        result = material.hashCode();
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
        return material.equals(that.material);
    }

    @Override
    public String toString() {
        return "ItemListing{" +
                "material=" + material.toString() +
                ", isInfinite=" + isInfinite +
                ", inventory=" + inventory +
                ", basePrice=" + basePrice +
                ", valueAddedTax=" + valueAddedTax +
                ", equilibrium=" + equilibrium +
                ", config=" + config +
                '}';
    }
}