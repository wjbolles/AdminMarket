package com.wjbolles.adminmarket.eco.dao;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class YamlItemListingDaoTest extends AbstractItemListingDaoTest {
    YamlConfiguration yamlConf = new YamlConfiguration();;
    String correctFileName = material.toString()+".yml";;

    public YamlItemListingDaoTest() {
        plugin.setListingDao(new YamlItemListingDao(plugin));
    }

    @Test
    public void insertItemListingTest_yamlName() throws Exception {
        File generatedConf = ((YamlItemListingDao)plugin.getListingDao()).getListingConfFile(listing);
        assertEquals("Verify conf filename is correct", generatedConf.getName(), correctFileName);
    }

    @Test
    public void insertItemListingTest_yaml() throws Exception {
        File generatedConf = ((YamlItemListingDao)plugin.getListingDao()).getListingConfFile(listing);

        yamlConf.load(generatedConf);
        assertEquals("Material in YAML should match what was stored",
                listing.getMaterial().toString(), yamlConf.get("material"));
        assertEquals("Infinite in YAML should match what was stored",
                true, yamlConf.get("isInfinite"));
        assertEquals("Inventory in YAML should match what was stored",
                0, yamlConf.get("inventory"));
        assertEquals("Equilibrium in YAML should match what was stored",
                1000, yamlConf.get("equilibrium"));
        assertEquals("Base Price in YAML should match what was stored",
                0.0, yamlConf.get("basePrice"));
        assertEquals("Value Added Tax in YAML should match what was stored",
                0.0, yamlConf.get("valueAddedTax"));
    }

    @Test
    public void updateItemListingTest_yaml() throws Exception {
        updateItemListing();

        // Assert
        // Verify new parameters were stored correctly
        File generatedConf = ((YamlItemListingDao)plugin.getListingDao()).getListingConfFile(listing);

        yamlConf.load(generatedConf);
        assertEquals("Material in YAML should match what was stored",
                listing.getMaterial().toString(), yamlConf.get("material"));
        assertEquals("Infinite in YAML should match what was stored",
                false, yamlConf.get("isInfinite"));
        assertEquals("Inventory in YAML should match what was stored",
                10, yamlConf.get("inventory"));
        assertEquals("Equilibrium in YAML should match what was stored",
                100, yamlConf.get("equilibrium"));
        assertEquals("Base Price in YAML should match what was stored",
                20.0, yamlConf.get("basePrice"));
        assertEquals("Value Added Tax in YAML should match what was stored",
                30.0, yamlConf.get("valueAddedTax"));
    }

    @Test
    public void deleteItemTest_yaml() throws Exception {
        assertEquals("Item should have been added to listings", material, listing.getMaterial());
        File conf = ((YamlItemListingDao) plugin.getListingDao()).getListingConfFile(listing);
        plugin.getListingDao().deleteItemListing(listing);
        assertNull("Item should no longer be present", plugin.getListingDao().findItemListing(material));
        assertFalse("Verify the file was removed", conf.exists());
    }
}
