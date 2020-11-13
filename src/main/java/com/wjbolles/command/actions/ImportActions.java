package com.wjbolles.command.actions;

import com.wjbolles.AdminMarket;
import com.wjbolles.adminmarket.utils.Constants;
import com.wjbolles.command.CommandUtil;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.model.ItemListing;
import com.wjbolles.eco.model.ItemListingBuilder;
import org.bukkit.Material;

import java.io.BufferedReader;
import java.io.FileReader;

public class ImportActions {
    private final ItemListingDao listingDao;
    private final AdminMarket plugin;

    public ImportActions(AdminMarket plugin) {
        this.listingDao = plugin.getListingDao();
        this.plugin = plugin;
    }

    public boolean importItemListings() {
        String row;
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(Constants.PLUGIN_IMPORT));
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                Material material = CommandUtil.materialFactory(data[0]);
                if (material == null) {
                    continue;
                }
                if (!CommandUtil.isValidStoreItem(material)) {
                    continue;
                }
                ItemListing listing = new ItemListingBuilder(material)
                        .setInfinite(Boolean.parseBoolean(data[1]))
                        .setInventory(Integer.parseInt(data[2]))
                        .setValueAddedTax(Double.parseDouble(data[3]))
                        .setBasePrice(Double.parseDouble(data[4]))
                        .setEquilibrium(Integer.parseInt(data[5]))
                        .setConfig(plugin.getPluginConfig())
                        .build();
                try {
                    listingDao.insertItemListing(listing);
                } catch (Exception ignored) {
                    // TODO: Add log warnings for invalid rows
                }
            }
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
