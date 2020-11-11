package com.wjbolles.command.actions;

import com.wjbolles.AdminMarket;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemListingActions {

    private ItemListingDao listingDao;
    private AdminMarket plugin;

    public ItemListingActions(AdminMarket plugin) {
        this.listingDao = plugin.getListingDao();
        this.plugin = plugin;
    }

    public void addItems(Material material, double basePrice, boolean isInfinite) throws Exception {
        ItemListing listing = new ItemListing(material, isInfinite, plugin.getPluginConfig());
        listing.setBasePrice(basePrice);
        listingDao.insertItemListing(listing);
    }

    public void removeItems(Material material) throws Exception {
        ItemListing listing = listingDao.findItemListing(material);
        if(listing != null) {
            listingDao.deleteItemListing(listing);
        }
    }
}
