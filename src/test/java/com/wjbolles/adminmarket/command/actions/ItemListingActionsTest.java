package com.wjbolles.adminmarket.command.actions;

import com.wjbolles.AdminMarketTest;
import com.wjbolles.adminmarket.eco.dao.ItemListingDao;
import com.wjbolles.adminmarket.eco.model.ItemListing;
import org.bukkit.Material;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ItemListingActionsTest extends AdminMarketTest {

    private Material material = Material.GRANITE;
    private ItemListingActions itemListingAction;
    private ItemListingDao itemListingDao;

    @Before
    public void setUp() throws Exception {
        this.itemListingAction = plugin.getItemListingActions();
        this.itemListingDao = plugin.getListingDao();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testAddItem() throws Exception {
        itemListingAction.addItem(material, 10.0, false);
        ItemListing itemListing = itemListingDao.findItemListing(material);

        assertNotNull("Listing should have been created.", itemListing);
        assertEquals("Item should cost $10.00.", 10.0, itemListing.getBasePrice(), 0.001);
        assertEquals("Item should not have infinite inventory.", false, itemListing.isInfinite());
    }

    @Test
    public void testRemoveItem() throws Exception {
        itemListingAction.addItem(material, 10.0, false);
        ItemListing itemListing = itemListingDao.findItemListing(material);

        assertNotNull("Listing should have been created.", itemListing);
        itemListingAction.removeItem(material);
        assertNull("Listing should have been removed.", itemListingDao.findItemListing(material));
    }

    @Test
    public void testUpdateItemInventory() throws Exception {
        itemListingAction.addItem(material, 10.0, false);
        itemListingAction.updateItemInventory(material, 20);
        assertEquals("Inventory should have been updated.", itemListingDao.findItemListing(material).getInventory(), 20);
    }

    public void testUpdateItemBasePrice() {
    }
}