/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.logging.Logger;

import com.wjbolles.command.executors.ShopCommandExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.wjbolles.AdminMarketTest;
import com.wjbolles.eco.model.ItemListing;

public class ShopCommandExecutorTest extends AdminMarketTest {
    
    @Before
    public void setup() {
    }

    @Test
    public void testGetListing() throws Exception {
        // Arrange
        // Economic stuff...
        ItemStack stack = new ItemStack(Material.STONE, 1, (short) 1);
        ItemListing listing = new ItemListing(stack, true, plugin.getPluginConfig());
        listing.setBasePrice(10.0);
        //plugin.getListingManager().addListing(stack, listing);
        Player player = mock(Player.class);
        
        // Act
        ShopCommandExecutor ex = new ShopCommandExecutor(plugin);
        ex.onCommand(player, null, null, new String[]{"list"});
        
        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, atLeastOnce()).sendMessage(captor.capture());
        
        for(String s : captor.getAllValues()) {
            System.out.println("Debug: " + s);
        }
        
        assertTrue(true);
    }
}
