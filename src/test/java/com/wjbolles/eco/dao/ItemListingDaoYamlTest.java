/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco.dao;

import com.wjbolles.AdminMarket;
import com.wjbolles.AdminMarketTest;
import com.wjbolles.Config;
import com.wjbolles.command.ShopCommandExecutorTest;
import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class ItemListingDaoYamlTest extends AdminMarketTest {

    private final short MATERIAL_TYPE = 1;

    YamlConfiguration yamlConf;
    String correctFileName;
    ItemListing listing;
    ItemStack stack;
    Config config;
    int amount = 30;
    ItemListingDao listingDao;

    @BeforeMethod
    public void setup() throws Exception {

        try {
            logger = Logger.getLogger(ShopCommandExecutorTest.class.getName());
            preparePluginMock();
            this.config = mock(Config.class);
            this.stack = new ItemStack(Material.STONE, 1, MATERIAL_TYPE);
            this.listingDao = new ItemListingYamlDao(plugin);
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    private void arrangeInsert() throws Exception {

        // Conf file
        yamlConf = new YamlConfiguration();
        correctFileName = stack.getType()+"-"+stack.getDurability()+".yml";

        // Listing
        boolean isInfinite = false;
        when(config.getShouldUseFloatingPrices()).thenReturn(true);
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

    @Test
    public void updateItemListing() throws Exception {
        // Arrange
        arrangeInsert();

        // Act
        listingDao.insertItemListing(listing);

        ItemListing updatedListing = new ItemListing(stack, false, config);
        updatedListing.setInventory(40);
        updatedListing.setInfinite(true);
        updatedListing.setBasePrice(3.0);
        listingDao.updateItemListing(updatedListing);

        // Verify new parameters were stored correctly
        File generatedConf = ((ItemListingYamlDao)listingDao).getListingConfFile(listing);

        yamlConf.load(generatedConf);

        // Assert
        assertEquals(yamlConf.get("material"), updatedListing.getStack().getType().toString());
        assertEquals((short) yamlConf.getInt("durability"), updatedListing.getStack().getDurability());
        assertEquals(yamlConf.get("isInfinite"), updatedListing.isInfinite());
        assertEquals(yamlConf.get("inventory"), updatedListing.getInventory());
        assertEquals(yamlConf.get("equilibrium"), updatedListing.getEquilibrium());
        assertEquals(yamlConf.get("basePrice"), updatedListing.getBasePrice());

    }

    @Test
    public void updateItemListing2() throws Exception {
        // Arrange
        arrangeInsert();

        // Act
        listingDao.insertItemListing(listing);

        ItemListing updatedListing = new ItemListing(stack, false, config);
        updatedListing.setInventory(40);
        updatedListing.setInfinite(true);
        updatedListing.setBasePrice(3.0);
        listingDao.updateItemListing(updatedListing);

        ItemListing foundListing = listingDao.findItemListing(stack);

        // Assert
        assertEquals(foundListing, updatedListing);
    }
}
