package com.wjbolles.command.actions;

import com.wjbolles.AdminMarket;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Material;

public class ItemListingActions {

    private final ItemListingDao listingDao;
    private final AdminMarket plugin;

    public ItemListingActions(AdminMarket plugin) {
        this.listingDao = plugin.getListingDao();
        this.plugin = plugin;
    }

    public void addItem(Material material, double basePrice, boolean isInfinite) throws Exception {
        ItemListing listing = new ItemListing(material, isInfinite, plugin.getPluginConfig());
        listing.setBasePrice(basePrice);
        listingDao.insertItemListing(listing);
    }

    public void removeItem(Material material) throws Exception {
        ItemListing listing = listingDao.findItemListing(material);
        if(listing != null) {
            listingDao.deleteItemListing(listing);
        }
    }

    public void updateItemEquilibrium(Material material, int equilibrium) throws Exception {
        ItemListing listing = listingDao.findItemListing(material);
        if(listing != null) {
            listing.setEquilibrium(equilibrium);
            listingDao.updateItemListing(listing);
        }
    }

    public void updateItemInventory(Material material, int inventory) throws Exception {
        ItemListing listing = listingDao.findItemListing(material);
        listing.setInventory(inventory);
        listingDao.updateItemListing(listing);
    }

    public void updateItemBasePrice(Material material, double basePrice) throws Exception {
        ItemListing listing = listingDao.findItemListing(material);
        listing.setBasePrice(basePrice);
        listingDao.updateItemListing(listing);
    }
}
