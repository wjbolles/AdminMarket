package com.wjbolles.command.actions;

import com.wjbolles.AdminMarketTest;
import com.wjbolles.adminmarket.utils.Consts;
import com.wjbolles.eco.model.ItemListing;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.Assert.*;

public class ItemListingActionsTest extends AdminMarketTest {

    private ItemStack stack = new ItemStack(Material.STONE, 1);
    private ItemListingActions itemListingActions = new ItemListingActions(this.plugin);

    File conf = new File(Consts.PLUGIN_ITEMS_DIR + File.separatorChar + "STONE.yml");
    YamlConfiguration yamlConf = new YamlConfiguration();

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addItemsTest() throws Exception {
        itemListingActions.addItems(stack, 10.0, true);
        ItemListing listing = plugin.getListingDao().findItemListing(stack);
        assertEquals(stack, listing.getStack());
        assertEquals(10.0, listing.getBasePrice(), 0.1);
        assertEquals(true, listing.isInfinite());
    }

    @Test
    public void addItemsTest2() throws Exception {
        itemListingActions.addItems(stack, 10.0, true);
        assertTrue(conf.exists());
        yamlConf.load(conf);

        assertEquals(stack.getType().toString(), yamlConf.get("material"));
        // assertEquals(stack.getDurability(), (short) yamlConf.getInt("durability"));
        assertEquals(true, yamlConf.get("isInfinite"));
        assertEquals(0, yamlConf.get("inventory"));
        assertEquals(1000, yamlConf.get("equilibrium"));
        assertEquals(10.0, yamlConf.get("basePrice"));
    }

    @Test
    public void removeItems() throws Exception {

    }

}