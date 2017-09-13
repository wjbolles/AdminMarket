/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco.dao;

import com.wjbolles.AdminMarketTest;
import com.wjbolles.Config;
import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ItemListingDaoYamlTest extends AdminMarketTest {

    private final short MATERIAL_TYPE = 1;

    YamlConfiguration yamlConf;
    String correctFileName;
    ItemListing listing;
    ItemStack stack = new ItemStack(Material.STONE, 1, MATERIAL_TYPE);;

    int amount = 30;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private void arrangeInsert() throws Exception {
        // Conf file
        yamlConf = new YamlConfiguration();
        correctFileName = stack.getType()+"-"+stack.getDurability()+".yml";

        // Listing
        boolean isInfinite = false;
        Config config = plugin.getPluginConfig();
        when(config.getShouldUseFloatingPrices()).thenReturn(true);
        listing = new ItemListing(stack,true,config);
    }

    @Test
    public void insertItemListingTest() throws Exception {
        // Arrange
        arrangeInsert();

        // Act
        plugin.getListingDao().insertItemListing(listing);

        // Assert
        // Verify listing is retrievable
        assertEquals(listing, plugin.getListingDao().findItemListing(stack));
    }

    @Test
    public void insertItemListingTest2() throws Exception {
        // Arrange
        arrangeInsert();

        // Act
        plugin.getListingDao().insertItemListing(listing);

        // Assert
        // Verify conf filename is correct
        File generatedConf = ((ItemListingYamlDao)plugin.getListingDao()).getListingConfFile(listing);
        assertEquals(generatedConf.getName(), correctFileName);
    }

    @Test
    public void insertItemListingTest3() throws Exception {
        // Arrange
        arrangeInsert();

        // Act
        plugin.getListingDao().insertItemListing(listing);

        // Assert
        // Verify new parameters were stored correctly
        File generatedConf = ((ItemListingYamlDao)plugin.getListingDao()).getListingConfFile(listing);

        yamlConf.load(generatedConf);
        assertEquals(listing.getStack().getType().toString(), yamlConf.get("material"));
        assertEquals(listing.getStack().getDurability(), (short) yamlConf.getInt("durability"));
        assertEquals(listing.isInfinite(), yamlConf.get("isInfinite"));
        assertEquals(listing.getInventory(), yamlConf.get("inventory"));
        assertEquals(listing.getEquilibrium(), yamlConf.get("equilibrium"));
        assertEquals(listing.getBasePrice(), yamlConf.get("basePrice"));
    }

    @Test
    public void updateItemListing() throws Exception {
        // Arrange
        arrangeInsert();

        // Act
        Config config = plugin.getPluginConfig();
        plugin.getListingDao().insertItemListing(listing);

        ItemListing updatedListing = new ItemListing(stack, false, config);
        updatedListing.setInventory(40);
        updatedListing.setInfinite(true);
        updatedListing.setBasePrice(3.0);

        plugin.getListingDao().updateItemListing(updatedListing);

        // Verify new parameters were stored correctly
        File generatedConf = ((ItemListingYamlDao)plugin.getListingDao()).getListingConfFile(listing);

        yamlConf.load(generatedConf);

        // Assert
        assertEquals(updatedListing.getStack().getType().toString(), yamlConf.get("material"));
        assertEquals(updatedListing.getStack().getDurability(), (short) yamlConf.getInt("durability"));
        assertEquals(updatedListing.isInfinite(), yamlConf.get("isInfinite"));
        assertEquals(updatedListing.getInventory(), yamlConf.get("inventory"));
        assertEquals(updatedListing.getEquilibrium(), yamlConf.get("equilibrium"));
        assertEquals(updatedListing.getBasePrice(), yamlConf.get("basePrice"));
    }

    @Test
    public void updateItemListing2() throws Exception {
        // Arrange
        arrangeInsert();

        // Act
        plugin.getListingDao().insertItemListing(listing);
        Config config = plugin.getPluginConfig();
        ItemListing updatedListing = new ItemListing(stack, false, config);
        updatedListing.setInventory(40);
        updatedListing.setInfinite(true);
        updatedListing.setBasePrice(3.0);
        plugin.getListingDao().updateItemListing(updatedListing);

        ItemListing foundListing = plugin.getListingDao().findItemListing(stack);

        // Assert
        assertEquals(updatedListing, foundListing);
    }
}
