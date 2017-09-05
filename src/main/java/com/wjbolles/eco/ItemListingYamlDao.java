package com.wjbolles.eco;

import com.wjbolles.AdminMarket;
import com.wjbolles.adminmarket.utils.Consts;
import org.bukkit.inventory.ItemStack;
import org.json.simple.ItemList;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Logger;

public class ItemListingYamlDao implements ItemListingDao {

    private AdminMarket plugin;
    private Logger log;
    private DecimalFormat df = new DecimalFormat("#.00");
    private HashMap<String, ItemListing> listings = new HashMap<String, ItemListing>();

    public ItemListingYamlDao(AdminMarket plugin) {
        this.plugin = plugin;
        this.log = plugin.getLog();
        log.info("Loading items...");
        loadItems();
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
    }

    private void loadItems() {
        File itemsDir = new File(Consts.PLUGIN_ITEMS_DIR);
        File[] items = itemsDir.listFiles();

        for(File item : items) {
            ItemListing listing = null;
            try {
                listing = new ItemListing(item, plugin.getPluginConfig());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ItemStack stack = new ItemStack(listing.getStack().getType(), 1, listing.getStack().getDurability());
            listings.put(generateStackKey(stack), listing);
        }
    }

    public String generateStackKey(ItemStack stack) {
        return stack.getType()+":"+stack.getDurability();
    }

    public ItemListing findItemListing(ItemStack stack) {
        if (stack.getAmount() != 1) {
            throw new IllegalArgumentException();
        }
        return listings.get(generateStackKey(stack));
    }

    public boolean insertItemListing(ItemListing listing) {
        return false;
    }

    public boolean updateItemListing(ItemListing listing) {
        return false;
    }

    public boolean deleteItemListing(ItemListing listing) {
        return false;
    }
}
