/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

import com.wjbolles.AdminMarketConfig;
import com.wjbolles.AdminMarketTest;
import com.wjbolles.Config;
import com.wjbolles.command.actions.TransactionActions;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.economy.BasicEconomyWrapperImpl;
import com.wjbolles.eco.economy.EconomyWrapper;
import com.wjbolles.eco.model.ItemListing;
import com.wjbolles.fakes.InventoryFake;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TransactionActionsTest extends AdminMarketTest {

    @Mock
    private ItemListingDao listingDao;
    private AdminMarketConfig config = plugin.getPluginConfig();
    private EconomyWrapper economy;

    private ItemListing itemListing;
    private HashMap<String, Double> accounts = new HashMap<String, Double>();

    private PlayerInventory inventory = player.getInventory();
    private TransactionActions transactionActions = new TransactionActions(plugin);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(config.getTreasuryAccount()).thenReturn("towny-server");
        accounts.put("towny-server", 10000.0);
        accounts.put("ANY_PLAYER", 10000.0);
        economy = new BasicEconomyWrapperImpl(accounts);
        plugin.setEconomyWrapper(economy);
    }

    @Test
    public void testSellHandInfiniteListing() throws Exception {
        Config config;

        // Arrange
        ItemStack stack = new ItemStack(Material.STONE, 1, (short) 1);
        doReturn(stack).when(inventory).getItemInMainHand();

        ItemListing itemListing = new ItemListing(stack, true, plugin.getPluginConfig());
        itemListing.setBasePrice(10.0);

        doReturn(itemListing).when(listingDao).findItemListing(Matchers.any());

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
    }

    @Test
    public void testSellHandLimitedListing() throws Exception {
        // Arrange
        // Economic stuff...
        when(config.getSalesTax()).thenReturn(0.0);

        ItemStack stack = new ItemStack(Material.STONE, 1, (short) 1);
        ItemListing itemListing = new ItemListing(stack, true, plugin.getPluginConfig());
        itemListing.setBasePrice(10.0);
        itemListing.setInventory(1000);
        itemListing.setEquilibrium(1000);

        doReturn(itemListing).when(listingDao).findItemListing(Matchers.any());

        plugin.setListingDao(listingDao);
        plugin.setTransactionActions(new TransactionActions(plugin));


        when(inventory.getItemInMainHand()).thenReturn(stack);
        when(listingDao.findItemListing(Matchers.any(ItemStack.class))).thenReturn(itemListing);

        // Act
        plugin.getTransactionActions().sellHand(player);
        
        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, times(1)).sendMessage(captor.capture());
        assertEquals("Should have sold for $10 dollars.", "Sold for: §a+$10.00", captor.getValue());
        assertEquals(10000.0 + 10, economy.getBalance(player), 0.01);
        assertEquals(10000.0 - 10, economy.getBalance("towny-server"),0.01);
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
        transactionActions.sellHand(player);
        
        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, times(1)).sendMessage(captor.capture());
        assertEquals(captor.getValue(), "Sold for: §a+$640.00", "Should have sold for $640 dollars.");
        assertEquals(economy.getBalance(player), 10000.0 + 64*10, 0.001);
        assertEquals(economy.getBalance("towny-server"), 10000.0 - 64*10, 0.001);
    }
}
