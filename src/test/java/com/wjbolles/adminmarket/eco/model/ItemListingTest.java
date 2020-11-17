/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.adminmarket.eco.model;

import com.wjbolles.adminmarket.AdminMarketConfig;
import com.wjbolles.AdminMarketTest;
import org.bukkit.Material;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class ItemListingTest extends AdminMarketTest {

    private ItemListing listing;
    private Material material = Material.GRANITE;
    private AdminMarketConfig config = plugin.getPluginConfig();

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void removeInventoryTest() throws Exception {
        // Arrange
        boolean isInfinite = false;
        int amount = 30;

        // Act
        listing = new ItemListing(material, isInfinite, config);
        listing.setInventory(amount);
        listing.removeInventory(10);

        // Assert
        assertEquals(amount-10, listing.getInventory());
    }

    @Test
    public void getSellPriceInfiniteTest() throws Exception {
        // Arrange
        boolean isInfinite = true;
        config.setUseFloatingPrices(true);

        // Act
        listing = new ItemListing(material, isInfinite, config);
        Double price = 10.0;
        listing.setBasePrice(price);

        // Assert
        assertEquals(price, listing.getSellPrice(), 0.01);
    }

    @Test
    public void getSellPriceFiniteTest() throws Exception {
        // Arrange
        // Hand calculated: 13.0888
        double expectedSellPrice = 13.0888;
        config.setUseFloatingPrices(true);
        config.setMaxPercentBasePrice(0.33);

        // Act
        listing = new ItemListing(material,false, config);
        listing.setInventory(64);
        Double price = 10.0;
        listing.setBasePrice(price);

        // Assert
        assertEquals(expectedSellPrice, listing.getSellPrice(), 0.01);

    }

    @Test
    public void equilibriumFormulaCorrect() throws Exception {
        final double BASE_PRICE = 1_000.00;
        final double BUY_PRICE = 1_000.00;
        final double SELL_PRICE = 999.67;

        config.setSalesTax(0.0);
        config.setUseFloatingPrices(true);

        listing = new ItemListingBuilder(material)
                    .setValueAddedTax(0.0)
                    .setInventory(1000)
                    .setInfinite(false)
                    .setBasePrice(BASE_PRICE)
                    .setConfig(config)
                    .setEquilibrium(1000)
                    .build();

        assertEquals(BUY_PRICE, listing.getBuyPrice(), 0.001);
        assertEquals(SELL_PRICE, listing.getSellPrice(), 0.001);
    }
}
