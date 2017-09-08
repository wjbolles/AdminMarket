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
import com.wjbolles.command.ShopCommandExecutorTest;
import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class ItemListingDaoYamlTest extends AdminMarketTest {
    YamlConfiguration yamlConf;
    String correctFileName;
    ItemListing listing;
    ItemStack stack;
    Config config;
    int amount = 30;
    ItemListingDao listingDao;

    @BeforeMethod
    public void setup() throws Exception {
        logger = Logger.getLogger(ShopCommandExecutorTest.class.getName());
        preparePluginMock();

        short type = 1;
        config = mock(Config.class);
        stack = new ItemStack(Material.STONE, 1, type);

        listingDao = new ItemListingYamlDao(plugin);
    }

    private void arrangeInsert() throws Exception {

        // Conf file
        yamlConf = new YamlConfiguration();
        correctFileName = stack.getType()+"-"+stack.getDurability()+".yml";

        // Listing
        boolean isInfinite = false;
        when(config.shouldUseFloatingPrices()).thenReturn(true);
        listing = new ItemListing(stack,true,config);

    }

    @Test
    public void insertItemListingTest() throws Exception {
        // Arrange
        arrangeInsert();

        // Act
        listingDao.insertItemListing(listing);

        // Assert
        // Verify listing is retrievable
        assertEquals(listingDao.findItemListing(stack), listing);
    }

    @Test
    public void insertItemListingTest2() throws Exception {
        // Arrange
        arrangeInsert();

        // Act
        listingDao.insertItemListing(listing);

        // Assert
        // Verify conf filename is correct
        File generatedConf = ((ItemListingYamlDao)listingDao).getListingConfFile(listing);
        assertEquals(generatedConf.getName(), correctFileName);
    }

    @Test
    public void insertItemListingTest3() throws Exception {
        // Arrange
        arrangeInsert();

        // Act
        listingDao.insertItemListing(listing);

        // Assert
        // Verify new parameters were stored correctly
        File generatedConf = ((ItemListingYamlDao)listingDao).getListingConfFile(listing);

        yamlConf.load(generatedConf);
        assertEquals(yamlConf.get("material"), listing.getStack().getType().toString());
        assertEquals((short) yamlConf.getInt("durability"), listing.getStack().getDurability());
        assertEquals(yamlConf.get("isInfinite"), listing.isInfinite());
        assertEquals(yamlConf.get("inventory"), listing.getInventory());
        assertEquals(yamlConf.get("equilibrium"), listing.getEquilibrium());
        assertEquals(yamlConf.get("basePrice"), listing.getBasePrice());
    }
}
