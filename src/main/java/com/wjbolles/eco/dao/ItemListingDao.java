package com.wjbolles.eco.dao;

import com.wjbolles.eco.model.ItemListing;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface ItemListingDao {
    void loadItems();
    HashMap<String, ItemListing> getAllListings();
    ItemListing findItemListing(ItemStack stack);
    void insertItemListing(ItemListing listing) throws Exception;
    void updateItemListing(ItemListing listing) throws Exception;
    void deleteItemListing(ItemListing listing) throws Exception;
}
