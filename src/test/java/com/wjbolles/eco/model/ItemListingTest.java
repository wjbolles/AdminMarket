/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco.model;

import com.wjbolles.AdminMarketConfig;
import com.wjbolles.AdminMarketTest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
// import static org.mockito.Mockito.when;

public class ItemListingTest extends AdminMarketTest {

    private ItemListing listing;
    // private final short TYPE = 1;
    private ItemStack stack = new ItemStack(Material.STONE, 1);
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
        listing = new ItemListing(stack,isInfinite, config);
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
        listing = new ItemListing(stack, isInfinite, config);
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
        config.setMaxPercentBasePrice(1.33);

        // Act
        listing = new ItemListing(stack,false, config);
        listing.setInventory(64);
        Double price = 10.0;
        listing.setBasePrice(price);

        // Assert
        assertEquals(expectedSellPrice, listing.getSellPrice(), 0.01);

    }

    @Test
    public void equlibriumFormulaWorks() throws Exception {
        config.setSalesTax(0.0);
        listing = new ItemListing(stack,false, config);
        listing.setBasePrice(10.0);
        listing.setValueAddedTax(0.0);
        listing.setInventory(1000);
        listing.setEquilibrium(1000);

        assertEquals(listing.getBuyPrice(), listing.getSellPrice(), 0.01);
    }
}
