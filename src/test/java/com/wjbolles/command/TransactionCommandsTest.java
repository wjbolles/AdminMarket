/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.logging.Logger;

import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.wjbolles.AdminMarketTest;

public class TransactionCommandsTest extends AdminMarketTest {
    
    @BeforeMethod
    public void setup() {
        logger = Logger.getLogger(TransactionCommandsTest.class.getName());
        preparePluginMock();
        when(config.getTreasuryAccount()).thenReturn("towny-server");
        
        accounts.put("towny-server", 10000.0);
        accounts.put("ANY_PLAYER", 10000.0);
    }

    @Test
    public void testSellHandInfiniteListing() throws Exception {
        // Arrange
        ItemStack stack = new ItemStack(Material.STONE, 1, (short) 1);
        ItemListing listing = new ItemListing(stack, true, plugin.getPluginConfig());
        listing.setBasePrice(10.0);

        when(inventory.getItemInMainHand()).thenReturn(stack);
        when(listingDao.findItemListing(Matchers.any(ItemStack.class))).thenReturn(listing);

        // Act
        tm.sellHand(player);
        
        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, times(1)).sendMessage(captor.capture());
        assertEquals(captor.getValue(), "Sold for: §a+$10.00", "Should have sold for $10 dollars.");
        assertEquals(economy.getBalance(player), 10010.0, 0.001);
        assertEquals(economy.getBalance("towny-server"), 10000.0, 0.001);
    }
    
    @Test
    public void testSellHandLimitedListing() throws Exception {
        // Arrange
        // Economic stuff...
        when(config.getSalesTax()).thenReturn(0.0);

        ItemStack stack = new ItemStack(Material.STONE, 1, (short) 1);
        ItemListing listing = new ItemListing(stack, false, plugin.getPluginConfig());
        listing.setBasePrice(10.0);
        listing.setInventory(1000);
        listing.setEquilibrium(1000);

        when(inventory.getItemInMainHand()).thenReturn(stack);
        when(listingDao.findItemListing(Matchers.any(ItemStack.class))).thenReturn(listing);

        // Act
        tm.sellHand(player);
        
        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, times(1)).sendMessage(captor.capture());
        assertEquals(captor.getValue(), "Sold for: §a+$10.00", "Should have sold for $10 dollars.");
        assertEquals(economy.getBalance(player), 10000.0 + 10, 0.01);
        assertEquals(economy.getBalance("towny-server"), 10000.0 - 10, 0.01);
    }
    
    @Test
    public void testSellHandLargeStack() throws Exception {
        // Arrange
        // Economic stuff...
        ItemStack stack = new ItemStack(Material.STONE, 64, (short) 1);
        ItemListing listing = new ItemListing(stack, false, plugin.getPluginConfig());
        listing.setBasePrice(10.0);

        when(inventory.getItemInMainHand()).thenReturn(stack);
        when(listingDao.findItemListing(Matchers.any(ItemStack.class))).thenReturn(listing);

        // Act
        tm.sellHand(player);
        
        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, times(1)).sendMessage(captor.capture());
        assertEquals(captor.getValue(), "Sold for: §a+$640.00", "Should have sold for $640 dollars.");
        assertEquals(economy.getBalance(player), 10000.0 + 64*10, 0.001);
        assertEquals(economy.getBalance("towny-server"), 10000.0 - 64*10, 0.001);
    }    
    
    @AfterMethod
    public void tearDown() {
        // Delete the ~/plugins directory for the next test
        deleteDirectory(workingDir);
    }

}
