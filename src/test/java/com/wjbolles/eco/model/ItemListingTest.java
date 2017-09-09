/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco.model;

import com.wjbolles.AdminMarketTest;
import com.wjbolles.Config;
import com.wjbolles.command.ShopCommandExecutorTest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class ItemListingTest extends AdminMarketTest {
    ItemListing listing;
    ItemStack stack;
    Config config;
    int amount = 30;

    @BeforeMethod
    public void setup() throws Exception {
        logger = Logger.getLogger(ShopCommandExecutorTest.class.getName());
        preparePluginMock();

        short type = 1;
        config = mock(Config.class);
        ItemStack stack = new ItemStack(Material.STONE, 1, type);
    }

    @Test
    public void removeInventoryTest() throws Exception {
        // Arrange
        boolean isInfinite = false;
        when(config.getShouldUseFloatingPrices()).thenReturn(true);

        // Act
        listing = new ItemListing(stack,isInfinite,config);
        listing.setInventory(amount);
        listing.removeInventory(10);

        // Assert
        assertEquals(listing.getInventory(), amount-10,"Should be ten fewer");
    }

    @Test
    public void getSellPriceInfiniteTest() throws Exception {
        // Arrange
        boolean isInfinite = true;
        when(config.getShouldUseFloatingPrices()).thenReturn(true);

        // Act
        listing = new ItemListing(stack,isInfinite,config);
        Double price = 10.0;
        listing.setBasePrice(price);

        // Assert
        assertEquals(listing.getSellPrice(), price, 0.01, "Should be unchanged");
    }

    @Test
    public void getSellPriceFiniteTest() throws Exception {
        // Arrange
        // Hand calculated: 13.0888
        double expectedSellPrice = 13.0888;
        when(config.getShouldUseFloatingPrices()).thenReturn(true);
        when(config.getMaxPercentBasePrice()).thenReturn(1.33);

        // Act
        listing = new ItemListing(stack,false,config);
        listing.setInventory(64);
        Double price = 10.0;
        listing.setBasePrice(price);

        // Assert
        assertEquals(listing.getSellPrice(), expectedSellPrice,0.01, "Should equal expected price");

    }
}
