/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.adminmarket.command;

import com.wjbolles.adminmarket.AdminMarketConfig;
import com.wjbolles.AdminMarketTest;
import com.wjbolles.adminmarket.command.actions.TransactionActions;
import com.wjbolles.adminmarket.eco.dao.ItemListingDao;
import com.wjbolles.adminmarket.eco.economy.BasicEconomyWrapper;
import com.wjbolles.adminmarket.eco.economy.EconomyWrapper;
import com.wjbolles.adminmarket.eco.model.ItemListing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TransactionActionsTest extends AdminMarketTest {

    @Mock
    private ItemListingDao listingDao;
    @Mock
    private AdminMarketConfig config;
    private EconomyWrapper economy;

    private HashMap<String, Double> accounts = new HashMap<String, Double>();

    private PlayerInventory inventory = player.getInventory();
    private TransactionActions transactionActions = new TransactionActions(plugin);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn("towny-server").when(config).getTreasuryAccount();
        accounts.put("towny-server", 10000.0);
        accounts.put("ANY_PLAYER", 10000.0);
        economy = new BasicEconomyWrapper(accounts);
        plugin.setEconomyWrapper(economy);
    }

    @Test
    public void testSellHandInfiniteListing() throws Exception {

        // Arrange
        ItemStack stack = new ItemStack(Material.GRANITE, 1);
        inventory.setItemInMainHand(stack);

        ItemListing itemListing = new ItemListing(stack.getType(), true, plugin.getPluginConfig());
        itemListing.setBasePrice(10.0);

        doReturn(itemListing).when(listingDao).findItemListing(ArgumentMatchers.any());

        plugin.setListingDao(listingDao);
        plugin.setTransactionActions(new TransactionActions(plugin));

        // Act
        plugin.getTransactionActions().sellHand(player);

        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, times(1)).sendMessage(captor.capture());
        assertEquals("Should have sold for $10 dollars.", "Sold for: §a+$10.00",captor.getValue());
        assertEquals(10010.0, economy.getBalance(player), 0.001);
        assertEquals(10000.0, economy.getBalance("towny-server"), 0.001);

        for(String s : captor.getAllValues()) {
            System.out.println("Debug: " + s);
        }
    }

    @Test
    public void testSellHandLimitedListing() throws Exception {
        // Arrange
        // Economic stuff...
        when(config.getSalesTax()).thenReturn(0.0);

        ItemStack stack = new ItemStack(Material.GRANITE, 1);
        ItemListing itemListing = new ItemListing(stack.getType(), true, config);
        itemListing.setBasePrice(10.0);
        itemListing.setInventory(1000);
        itemListing.setEquilibrium(1000);
        itemListing.setInfinite(false);

        doReturn(itemListing).when(listingDao).findItemListing(ArgumentMatchers.any());

        plugin.setListingDao(listingDao);
        plugin.setTransactionActions(new TransactionActions(plugin));

        inventory.setItemInMainHand(stack);
        when(listingDao.findItemListing(ArgumentMatchers.any(Material.class))).thenReturn(itemListing);

        // Act
        plugin.getTransactionActions().sellHand(player);
        
        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, times(1)).sendMessage(captor.capture());
        for(String s : captor.getAllValues()) {
            System.out.println("Debug: " + s);
        }
        assertEquals("Should have sold for $10 dollars.", "Sold for: §a+$10.00", captor.getValue());
        assertEquals(10000.0 + 10, economy.getBalance(player), 0.01);
        assertEquals(10000.0 - 10, economy.getBalance("towny-server"),0.01);
    }
    
    @Test
    public void testSellHandLargeStack() throws Exception {
        // Arrange
        // Economic stuff...
        when(config.getSalesTax()).thenReturn(0.0);

        ItemStack stack = new ItemStack(Material.GRANITE, 64);
        ItemListing itemListing = new ItemListing(stack.getType(), true, plugin.getPluginConfig());
        itemListing.setBasePrice(10.0);
        itemListing.setInventory(1000);
        itemListing.setEquilibrium(1000);
        itemListing.setInfinite(false);

        doReturn(itemListing).when(listingDao).findItemListing(ArgumentMatchers.any());

        plugin.setListingDao(listingDao);
        plugin.setTransactionActions(new TransactionActions(plugin));
        inventory.setItemInMainHand(stack);

        when(listingDao.findItemListing(ArgumentMatchers.any(Material.class))).thenReturn(itemListing);

        // Act
        plugin.getTransactionActions().sellHand(player);

        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, times(1)).sendMessage(captor.capture());
        for(String s : captor.getAllValues()) {
            System.out.println("Debug: " + s);
        }
        assertEquals("Should have sold for $640 dollars.", "Sold for: §a+$640.00", captor.getValue());
        assertEquals(10000.0 + 10*64, economy.getBalance(player), 0.01);
        assertEquals(10000.0 - 10*64, economy.getBalance("towny-server"),0.01);
    }

    @Test
    public void testSellAllStacks() throws Exception {
        // Arrange
        // Economic stuff...
        /*
        doReturn(true).when(config).getUseFloatingPrices();
        doReturn(.33).when(config).getMaxPercentBasePrice();
        */
        ItemStack stack = new ItemStack(Material.GRANITE, 64);
        ItemListing itemListing = new ItemListing(stack.getType(), true, plugin.getPluginConfig());
        itemListing.setBasePrice(10.0);
        itemListing.setInventory(1000);
        itemListing.setEquilibrium(1000);
        itemListing.setInfinite(false);

        doReturn(itemListing).when(listingDao).findItemListing(ArgumentMatchers.any());

        plugin.setListingDao(listingDao);
        plugin.setTransactionActions(new TransactionActions(plugin));

        inventory.setItem(0, stack);
        inventory.setItem(1, stack);

        when(listingDao.findItemListing(ArgumentMatchers.any(Material.class))).thenReturn(itemListing);

        // Act
        plugin.getTransactionActions().sellAll(player);

        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, times(1)).sendMessage(captor.capture());
        for(String s : captor.getAllValues()) {
            System.out.println("Debug: " + s);
        }
        assertEquals("Should have sold for $1280 dollars.", "Sold for: §a+$1,280.00", captor.getValue());
        assertEquals(10000.0 + 10*64*2, economy.getBalance(player), 0.01);
        assertEquals(10000.0 - 10*64*2, economy.getBalance("towny-server"),0.01);
    }
}
