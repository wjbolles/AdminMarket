/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles;

public class Config {
    
    private double salesTax;
    private double maxPercentBasePrice;
    private boolean useFloatingPrices;
    private String treasuryAccount;
    
    public Config() {
        this.salesTax = 0.03; // 3%
        this.maxPercentBasePrice = 1.33; // 133% Base
        this.useFloatingPrices = false;
        this.treasuryAccount = "towny-server";
    }

    public String getTreasuryAccount() {
        return treasuryAccount;
    }

    public void setTreasuryAccount(String treasuryAccount) {
        this.treasuryAccount = treasuryAccount;
    }

    public double getSalesTax() {
        return salesTax;
    }

    public void setSalesTax(double salesTax) {
        this.salesTax = salesTax;
    }

    public double getMaxPercentBasePrice() {
        return maxPercentBasePrice;
    }

    public void setMaxPercentBasePrice(double maxPercentBasePrice) {
        this.maxPercentBasePrice = maxPercentBasePrice;
    }

    public boolean shouldUseFloatingPrices() {
        return useFloatingPrices;
    }

    public void setUseFloatingPrices(boolean useFloatingPrices) {
        this.useFloatingPrices = useFloatingPrices;
    }   
}
