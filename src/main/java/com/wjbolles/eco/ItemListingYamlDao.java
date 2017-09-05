package com.wjbolles.eco;

import com.wjbolles.AdminMarket;
import com.wjbolles.adminmarket.utils.Consts;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.json.simple.ItemList;

import java.io.File;
import java.io.IOException;
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

    private void updateYamlConf(ItemListing listing) throws IOException, InvalidConfigurationException {

        YamlConfiguration yamlConf = listing.getYamlConf();

        yamlConf.load(listing.getItemConf());
        yamlConf.set("material", listing.getStack().getType().toString());
        yamlConf.set("durability", listing.getStack().getDurability());
        yamlConf.set("isInfinite", listing.isInfinite());
        yamlConf.set("inventory", listing.getInventory());
        yamlConf.set("equilibrium", listing.getEquilibrium());
        yamlConf.set("basePrice",  listing.getBasePrice());
        yamlConf.save(listing.getItemConf());
    }

    public boolean insertItemListing(ItemListing listing) {
        // TODO Implement this
        return false;
    }

    public boolean updateItemListing(ItemListing listing) {
        try {
            updateYamlConf(listing);
        } catch (IOException e) {
            return false;
        } catch (InvalidConfigurationException e) {
            return false;
        }
        return true;
    }

    public boolean deleteItemListing(ItemListing listing) {
        listing.getItemConf().delete();
        return true;
    }
}
