package com.wjbolles;

import com.wjbolles.adminmarket.utils.Consts;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ConfigTest extends AdminMarketTest {
    Config config = new Config(plugin);
    File conf = new File(Consts.PLUGIN_CONF_DIR + File.separatorChar + "config.yml");
    YamlConfiguration yamlConf = new YamlConfiguration();

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getTreasuryAccountTest() throws Exception {
        assertEquals("towny-server", config.getTreasuryAccount());
    }

    @Test
    public void getSalesTaxTest() throws Exception {
        assertEquals(0.03, config.getSalesTax(), 0.01);
    }

    @Test
    public void getMaxPercentBasePriceTest() throws Exception {
        assertEquals(1.33, config.getMaxPercentBasePrice(), 0.01);
    }

    @Test
    public void getUseFloatingPricesTest() throws Exception {
        assertEquals(false, config.getUseFloatingPrices());
    }

    @Test
    public void setTreasuryAccountTest() throws Exception {
        // Test in memory
        config.setTreasuryAccount("test");
        assertEquals("test", config.getTreasuryAccount());

        // Test written to file
        yamlConf.load(conf);
        assertEquals("test", yamlConf.getString("treasuryAccount"));
    }

    @Test
    public void setSalesTaxTest() throws Exception {
        // Test in memory
        config.setSalesTax(0.0);
        assertEquals(0.0, config.getSalesTax(), 0.1);

        // Test written to file
        yamlConf.load(conf);
        assertEquals(0.0, yamlConf.getDouble("salesTax"), 0.1);
    }

    @Test
    public void setMaxPercentBasePriceTest() throws Exception {
        // Test in memory
        config.setMaxPercentBasePrice(2.0);
        assertEquals(2.0, config.getMaxPercentBasePrice(), 0.1);

        // Test written to file
        yamlConf.load(conf);
        assertEquals(2.0, yamlConf.getDouble("maxPercentBasePrice"), 0.1);
    }

    @Test
    public void setUseFloatingPricesTest() throws Exception {
        // Test in memory
        config.setUseFloatingPrices(true);
        assertEquals(true, config.getUseFloatingPrices());

        // Test written to file
        yamlConf.load(conf);
        assertEquals(true, yamlConf.getBoolean("useFloatingPrices"));
    }
}
