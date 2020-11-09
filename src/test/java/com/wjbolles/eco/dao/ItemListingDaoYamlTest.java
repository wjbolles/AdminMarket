/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco.dao;

import com.wjbolles.AdminMarketConfig;
import com.wjbolles.AdminMarketTest;
import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class ItemListingDaoYamlTest extends AdminMarketTest {

    YamlConfiguration yamlConf;
    String correctFileName;
    ItemListing listing;
    ItemStack stack = new ItemStack(Material.STONE, 1);

    int amount = 30;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private void arrangeInsert() throws Exception {
        // Conf file
        yamlConf = new YamlConfiguration();
        correctFileName = stack.getType()+".yml";

        // Listing
        AdminMarketConfig config = plugin.getPluginConfig();
        config.setUseFloatingPrices(true);
        listing = new ItemListing(stack,true, config);
    }

    @Test
    public void insertItemListingTest_interface_default() throws Exception {
        arrangeInsert();

        plugin.getListingDao().insertItemListing(listing);

        assertEquals("Verify listing is retrievable", listing, plugin.getListingDao().findItemListing(stack));
    }

    @Test
    public void insertItemListingTest_interface_custom() throws Exception {
        arrangeInsert();

        listing.setInfinite(false);
        listing.setInventory(10);
        listing.setEquilibrium(100);
        listing.setBasePrice(20.0);
        listing.setValueAddedTax(30);

        plugin.getListingDao().insertItemListing(listing);

        assertEquals("Verify listing is retrievable", listing, plugin.getListingDao().findItemListing(stack));
    }

    @Test
    public void insertItemListingTest_yamlName() throws Exception {
        arrangeInsert();

        plugin.getListingDao().insertItemListing(listing);
        File generatedConf = ((ItemListingYamlDao)plugin.getListingDao()).getListingConfFile(listing);

        assertEquals("Verify conf filename is correct", generatedConf.getName(), correctFileName);
    }

    @Test
    public void insertItemListingTest_yaml() throws Exception {
        arrangeInsert();

        plugin.getListingDao().insertItemListing(listing);

        // Assert
        // Verify new parameters were stored correctly
        File generatedConf = ((ItemListingYamlDao)plugin.getListingDao()).getListingConfFile(listing);

        yamlConf.load(generatedConf);
        assertEquals("Material in YAML should match what was stored",
                listing.getStack().getType().toString(), yamlConf.get("material"));
        assertEquals("Infinite in YAML should match what was stored",
                true, yamlConf.get("isInfinite"));
        assertEquals("Inventory in YAML should match what was stored",
                0, yamlConf.get("inventory"));
        assertEquals("Equilibrium in YAML should match what was stored",
                1000, yamlConf.get("equilibrium"));
        assertEquals("Base Price in YAML should match what was stored",
                0.0, yamlConf.get("basePrice"));
        assertEquals("Value Added Tax in YAML should match what was stored",
                0.0, yamlConf.get("valueAddedTax"));
    }

    @Test
    public void updateItemListingTest_interface_custom() throws Exception {
        arrangeInsert();

        plugin.getListingDao().insertItemListing(listing);
        assertEquals("Verify listing is retrievable", listing, plugin.getListingDao().findItemListing(stack));

        listing.setInfinite(false);
        listing.setInventory(10);
        listing.setEquilibrium(100);
        listing.setBasePrice(20.0);
        listing.setValueAddedTax(30);

        plugin.getListingDao().updateItemListing(listing);

        assertEquals("Verify listing is was updated", listing, plugin.getListingDao().findItemListing(stack));
    }

    @Test
    public void updateItemListingTest_yaml() throws Exception {
        arrangeInsert();

        plugin.getListingDao().insertItemListing(listing);

        listing.setInfinite(false);
        listing.setInventory(10);
        listing.setEquilibrium(100);
        listing.setBasePrice(20.0);
        listing.setValueAddedTax(30);

        plugin.getListingDao().updateItemListing(listing);

        // Assert
        // Verify new parameters were stored correctly
        File generatedConf = ((ItemListingYamlDao)plugin.getListingDao()).getListingConfFile(listing);

        yamlConf.load(generatedConf);
        assertEquals("Material in YAML should match what was stored",
                listing.getStack().getType().toString(), yamlConf.get("material"));
        assertEquals("Infinite in YAML should match what was stored",
                false, yamlConf.get("isInfinite"));
        assertEquals("Inventory in YAML should match what was stored",
                10, yamlConf.get("inventory"));
        assertEquals("Equilibrium in YAML should match what was stored",
                100, yamlConf.get("equilibrium"));
        assertEquals("Base Price in YAML should match what was stored",
                20.0, yamlConf.get("basePrice"));
        assertEquals("Value Added Tax in YAML should match what was stored",
                30.0, yamlConf.get("valueAddedTax"));
    }

    @Test
    public void deleteItemTest_interface() throws Exception {
        arrangeInsert();

        plugin.getListingDao().insertItemListing(listing);
        assertEquals("Item should have been added to the stack", stack, listing.getStack());

        plugin.getListingDao().deleteItemListing(listing);

        assertNull("Item should no longer be present", plugin.getListingDao().findItemListing(stack));
    }

    @Test
    public void deleteItemTest_yaml() throws Exception {
        arrangeInsert();

        plugin.getListingDao().insertItemListing(listing);
        assertEquals("Item should have been added to the stack", stack, listing.getStack());
        File conf = ((ItemListingYamlDao) plugin.getListingDao()).getListingConfFile(listing);
        assertTrue("Verify the file was written", conf.exists());

        plugin.getListingDao().deleteItemListing(listing);
        assertNull("Item should no longer be present", plugin.getListingDao().findItemListing(stack));
        assertFalse("Verify the file was removed", conf.exists());
    }
}
