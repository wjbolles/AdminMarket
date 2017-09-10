package com.wjbolles.eco.dao;

import com.wjbolles.AdminMarket;
import com.wjbolles.Config;
import com.wjbolles.adminmarket.utils.Consts;
import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemListingYamlDao implements ItemListingDao {

    private AdminMarket plugin;
    private Logger log;
    private HashMap<String, ItemListing> listings = new HashMap<>();

    public ItemListingYamlDao(AdminMarket plugin) {
        this.plugin = plugin;
        this.log = plugin.getLog();
        log.info("Loading items...");
        loadItems();
    }

    public void loadItems() {
        File itemsDir = new File(Consts.PLUGIN_ITEMS_DIR);
        File[] items = itemsDir.listFiles();

        for (File itemFile : items != null ? items : new File[0]) {
            ItemListing listing = null;
            try {
                listing = itemListingFactory(itemFile, plugin.getPluginConfig());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            assert listing != null;
            ItemStack stack = new ItemStack(listing.getStack().getType(), 1, listing.getStack().getDurability());
            listings.put(generateStackKey(stack), listing);
        }
    }

    private ItemListing itemListingFactory(File itemConf, Config config) throws Exception {
        YamlConfiguration yamlConf = new YamlConfiguration();
        yamlConf.load(itemConf);

        ItemStack stack = new ItemStack(
                Material.getMaterial(yamlConf.getString("material")),
                1,
                (short) yamlConf.getInt("durability")
        );
        boolean isInfinite = yamlConf.getBoolean("isInfinite");

        ItemListing listing = new ItemListing(stack, isInfinite, config);

        listing.setBasePrice(yamlConf.getDouble("basePrice"));
        listing.setInventory(yamlConf.getInt("inventory"));
        listing.setEquilibrium(yamlConf.getInt("equilibrium"));
        listing.setValueAddedTax(yamlConf.getDouble("valueAddedTax"));

        return listing;

    }

    private String generateStackKey(ItemStack stack) {
        return stack.getType() + ":" + stack.getDurability();
    }

    public HashMap<String, ItemListing> getAllListings() {
        return listings;
    }

    public ItemListing findItemListing(ItemStack stack) {
        if (stack.getAmount() != 1) {
            throw new IllegalArgumentException();
        }
        return listings.get(generateStackKey(stack));
    }

    private void updateYamlConf(ItemListing listing) throws IOException, InvalidConfigurationException {
        File listingConf = getListingConfFile(listing);

        saveAllParameters(listing, listingConf);
    }

    public void insertItemListing(ItemListing listing) throws Exception {
        String key = generateStackKey(listing.getStack());
        listings.put(key, listing);
        initNewConf(listing);
    }

    public void updateItemListing(ItemListing listing) throws IOException, InvalidConfigurationException {
        updateYamlConf(listing);
    }

    public void deleteItemListing(ItemListing listing) throws Exception {
        String key = generateStackKey(listing.getStack());
        if(!listings.containsKey(key)) {
            throw new Exception("The specified listing was not found.");
        }
        Files.delete(Paths.get(getListingConfFile(listing).getPath()));
        listings.remove(key);
    }

    File getListingConfFile(ItemListing listing){
        return new File(Consts.PLUGIN_ITEMS_DIR + File.separatorChar +
                listing.getStack().getType()+"-"+
                listing.getStack().getDurability()+".yml");
    }

    private void initNewConf(ItemListing listing) throws Exception {
        File listingConf = getListingConfFile(listing);

        if(listingConf.exists()) {
            log.log(Level.SEVERE, "ItemListingYamlDao: Listing conf file already exists, cannot create a new one!");
            throw new IllegalStateException();
        }
        if(!listingConf.createNewFile()) {
            log.log(Level.SEVERE, "ItemListingYamlDao: - Failed to create listing file!");
            throw new IOException("Failed to create listing file.");
        }
        saveAllParameters(listing, listingConf);
    }

    private void saveAllParameters(ItemListing listing, File listingConf) throws IOException, InvalidConfigurationException {
        YamlConfiguration yamlConf = new YamlConfiguration();

        yamlConf.load(listingConf);
        yamlConf.set("material", listing.getStack().getType().toString());
        yamlConf.set("durability", listing.getStack().getDurability());
        yamlConf.set("isInfinite", listing.isInfinite());
        yamlConf.set("inventory", listing.getInventory());
        yamlConf.set("equilibrium", listing.getEquilibrium());
        yamlConf.set("basePrice", listing.getBasePrice());
        yamlConf.save(listingConf);
    }
}
