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

public class ItemListingDaoSqliteTest extends AdminMarketTest {

    String correctFileName;
    ItemListing listing;
    ItemStack stack = new ItemStack(Material.STONE, 1);

    int amount = 30;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        plugin.setListingDao(new ItemListingSqliteDao(plugin));
    }

    private void arrangeInsert() throws Exception {
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
    public void deleteItemTest_interface() throws Exception {
        arrangeInsert();

        plugin.getListingDao().insertItemListing(listing);
        assertEquals("Item should have been added to the stack", stack, listing.getStack());

        plugin.getListingDao().deleteItemListing(listing);

        assertNull("Item should no longer be present", plugin.getListingDao().findItemListing(stack));
    }

}
