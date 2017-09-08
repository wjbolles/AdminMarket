/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

import java.util.logging.Logger;

import com.wjbolles.Config;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.wjbolles.AdminMarketTest;
import com.wjbolles.eco.model.ItemListing;

import static org.testng.Assert.*;

public class ShopCommandExecutorTest extends AdminMarketTest {
    
    @BeforeMethod
    public void setup() {
        logger = Logger.getLogger(ShopCommandExecutorTest.class.getName());
        preparePluginMock();
    }

    @Test
    public void testGetListing() throws Exception {
        // Arrange
        // Economic stuff...
        Config config = new Config();
        ItemStack stack = new ItemStack(Material.STONE, 1, (short) 1);
        ItemListing listing = new ItemListing(stack, true, config);
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
    
    @AfterMethod
    public void tearDown() {
        // Delete the ~/plugins directory for the next test
        deleteDirectory(workingDir);
    }
}
