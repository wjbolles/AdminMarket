/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.fakes;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.logging.Logger;

import com.wjbolles.AdminMarketTest;
import com.wjbolles.command.TransactionCommandsTest;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InventoryFakeTest extends AdminMarketTest {

    PlayerInventory inventory;
    Player player;
    
    @BeforeMethod
    public void setup() { 
        logger = Logger.getLogger(TransactionCommandsTest.class.getName());
        preparePluginMock();
        
        player = mock(Player.class);
        inventory = new InventoryFake();
        when(player.getInventory()).thenReturn(inventory);
    }
    
    @Test
    public void testInsertSingleItem() {
        // Arrange
        ItemStack stack = new ItemStack(Material.STONE, 1, (short) 1);

        // Act
        player.getInventory().addItem(stack);
        
        // Assert
        assertEquals(player.getInventory().getItem(0), stack);
    }
    
    @Test
    public void testFillInventory() {
        
        ItemStack[] stack = new ItemStack[inventory.getSize()];
        for(int i = 0; i < stack.length; i++) {
            stack[i] = new ItemStack(Material.WOOL, 1, (short) i);
            player.getInventory().addItem(stack[i]);
        }
        
        for(int i = 0; i < stack.length; i++) {
            assertEquals(player.getInventory().getItem(i), stack[i]);
        }
    }
    
    @Test
    public void testOverflowInventory() {    
        ItemStack[] stack = new ItemStack[inventory.getSize()];
        for(int i = 0; i < stack.length; i++) {
            stack[i] = new ItemStack(Material.WOOL, 1, (short) i);
            player.getInventory().addItem(stack[i]);
        }
        
        // Should return a hashmap because no slots match
        assertFalse(player.getInventory().addItem(new ItemStack(Material.STONE, 1, (short) 1)).isEmpty());
    }
    
    @Test
    public void testOverflowAmount() {
        ItemStack[] stack = new ItemStack[inventory.getSize()];
        
        for(int i = 0; i < stack.length-1; i++) {
            stack[i] = new ItemStack(Material.STONE, 64, (short) 1);
            player.getInventory().addItem(stack[i]);
        }
        
        // Clear last slot for next test
        assertTrue(player.getInventory().addItem(new ItemStack(Material.STONE, 63, (short) 1)).isEmpty());
        assertTrue(player.getInventory().addItem(new ItemStack(Material.STONE, 1, (short) 1)).isEmpty());
        assertFalse(player.getInventory().addItem(new ItemStack(Material.STONE, 1, (short) 1)).isEmpty());
    }
}
