package com.wjbolles.eco.dao;

import com.wjbolles.AdminMarketConfig;
import com.wjbolles.AdminMarketTest;
import com.wjbolles.eco.model.ItemListing;
import com.wjbolles.eco.model.ItemListingBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public abstract class AbstractItemListingDaoTest extends AdminMarketTest {

    ItemListing listing;
    Material material = Material.GRANITE;
    Material material2 = Material.ANDESITE;

    @Before
    public void setUp() throws Exception {
        AdminMarketConfig config = plugin.getPluginConfig();
        config.setUseFloatingPrices(true);
        this.listing = new ItemListing(material, true, config);
        plugin.getListingDao().insertItemListing(listing);
    }

    @Test
    public void getAllListings() {
        HashMap<String, ItemListing> allListings = plugin.getListingDao().getAllListings();
        assertEquals("Verify listing is retrievable",
                this.listing,
                allListings.get(listing.getMaterialAsString()));
    }

    @Test
    public void findItemListing() {
        ItemListing listing = plugin.getListingDao().findItemListing(material);
        assertEquals("Verify listing is retrievable",
                this.listing,
                listing);
    }

    @Test
    public void insertItemListing() throws Exception {
        listing = new ItemListingBuilder(material2)
                .setInfinite(false)
                .setInventory(10)
                .setEquilibrium(100)
                .setBasePrice(20.0)
                .setValueAddedTax(30)
                .build();

        plugin.getListingDao().insertItemListing(listing);
        assertEquals("Verify listing was inserted",
                listing,
                plugin.getListingDao().findItemListing(material2));
    }

    @Test
    public void updateItemListing() throws Exception {
        ItemListing updatedListing = new ItemListingBuilder(material)
                .setInfinite(false)
                .setInventory(10)
                .setEquilibrium(100)
                .setBasePrice(20.0)
                .setValueAddedTax(30)
                .build();

        plugin.getListingDao().updateItemListing(updatedListing);
        assertEquals("Verify listing is updated",
                updatedListing,
                plugin.getListingDao().findItemListing(material));
    }

    @Test
    public void deleteItemListing() throws Exception {
        findItemListing();
        plugin.getListingDao().deleteItemListing(listing);
        assertNull("Verify listing is deleted", plugin.getListingDao().findItemListing(material));
    }

    @Test
    public void listingExists() {
        assertTrue("Verify item found", plugin.getListingDao().listingExists(material));
    }
}