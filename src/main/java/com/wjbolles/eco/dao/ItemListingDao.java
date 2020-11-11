package com.wjbolles.eco.dao;

import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface ItemListingDao {
    void loadItems();
    HashMap<String, ItemListing> getAllListings();
    ItemListing findItemListing(Material material);
    void insertItemListing(ItemListing listing) throws Exception;
    void updateItemListing(ItemListing listing) throws Exception;
    void deleteItemListing(ItemListing listing) throws Exception;
}
