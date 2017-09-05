package com.wjbolles.eco.dao;

import com.wjbolles.AdminMarket;
import com.wjbolles.eco.model.ItemListing;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Logger;

public interface ItemListingDao {
    void loadItems();
    HashMap<String, ItemListing> getAllListings();
    ItemListing findItemListing(ItemStack stack);
    boolean insertItemListing(ItemListing listing);
    boolean updateItemListing(ItemListing listing);
    boolean deleteItemListing(ItemListing listing);
}
