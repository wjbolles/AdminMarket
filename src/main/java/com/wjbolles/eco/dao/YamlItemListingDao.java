package com.wjbolles.eco.dao;

import com.wjbolles.AdminMarket;
import com.wjbolles.AdminMarketConfig;
import com.wjbolles.adminmarket.utils.Constants;
import com.wjbolles.command.CommandUtil;
import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YamlItemListingDao implements ItemListingDao {

    private final AdminMarket plugin;
    private final Logger log;
    private final HashMap<String, ItemListing> listings = new HashMap<>();

    public YamlItemListingDao(AdminMarket plugin) {
        this.plugin = plugin;
        this.log = plugin.getLog();

        loadItems();
    }

    public void loadItems() {
        log.info("Loading items...");

        File itemsDir = new File(Constants.PLUGIN_ITEMS_DIR);
        
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
            listings.put(listing.getMaterialAsString(), listing);
        }
    }

    private ItemListing itemListingFactory(File itemConf, AdminMarketConfig config) throws Exception {
        YamlConfiguration yamlConf = new YamlConfiguration();
        yamlConf.load(itemConf);

        Material material = CommandUtil.materialFactory(yamlConf.getString("material"));
        if (material == null) {
            throw new Exception("Invalid material in YAML");
        }
        boolean isInfinite = yamlConf.getBoolean("isInfinite");

        ItemListing listing = new ItemListing(material, isInfinite, config);

        listing.setBasePrice(yamlConf.getDouble("basePrice"));
        listing.setInventory(yamlConf.getInt("inventory"));
        listing.setEquilibrium(yamlConf.getInt("equilibrium"));
        listing.setValueAddedTax(yamlConf.getDouble("valueAddedTax"));

        return listing;

    }

    public HashMap<String, ItemListing> getAllListings() {
        return listings;
    }

    public ItemListing findItemListing(Material material) {
        if (material == null) {
            throw new IllegalArgumentException();
        }
        return listings.get(material.toString());
    }

    private void updateYamlConf(ItemListing listing) throws IOException, InvalidConfigurationException {
        File listingConf = getListingConfFile(listing);

        saveAllParameters(listing, listingConf);
        listings.put(listing.getMaterial().toString(), listing);

    }

    public void insertItemListing(ItemListing listing) throws Exception {
        String key = listing.getMaterial().toString();

        if(listings.containsKey(key)) {
            throw new Exception("Cannot insert listing, already exists.");
        }

        listings.put(key, listing);
        initNewConf(listing);
    }

    public void updateItemListing(ItemListing listing) throws IOException, InvalidConfigurationException {
        updateYamlConf(listing);
    }

    public void deleteItemListing(ItemListing listing) throws Exception {
        String key = listing.getMaterial().toString();
        if(!listings.containsKey(key)) {
            throw new Exception("The specified listing was not found.");
        }
        Files.delete(Paths.get(getListingConfFile(listing).getPath()));
        listings.remove(key);
    }

    @Override
    public boolean listingExists(Material material) {
        return findItemListing(material) != null;
    }

    File getListingConfFile(ItemListing listing){
        return new File(Constants.PLUGIN_ITEMS_DIR + File.separatorChar +
                listing.getMaterial()+".yml");
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
        yamlConf.set("material", listing.getMaterial().toString());
        yamlConf.set("isInfinite", listing.isInfinite());
        yamlConf.set("inventory", listing.getInventory());
        yamlConf.set("equilibrium", listing.getEquilibrium());
        yamlConf.set("basePrice", listing.getBasePrice());
        yamlConf.set("valueAddedTax", listing.getValueAddedTax());
        yamlConf.save(listingConf);
    }
}
