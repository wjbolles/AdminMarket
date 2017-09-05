package com.wjbolles.eco;

import com.wjbolles.AdminMarket;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Logger;

public interface ItemListingDao {
    ItemListing findItemListing(ItemStack stack);
    boolean insertItemListing(ItemListing listing);
    boolean updateItemListing(ItemListing listing);
    boolean deleteItemListing(ItemListing listing);
}
