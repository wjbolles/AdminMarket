/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles;

import com.wjbolles.command.QueryCommands;
import com.wjbolles.command.TransactionCommands;
import com.wjbolles.eco.dao.ItemListingDao;
import com.wjbolles.eco.dao.ItemListingYamlDao;
import com.wjbolles.eco.economy.BasicEconomyWrapperImpl;
import com.wjbolles.eco.economy.EconomyWrapper;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.testng.annotations.AfterMethod;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

public class AdminMarketTest {
    
    protected AdminMarket plugin;

    // Plugin mock fields
    protected Config config;
    protected EconomyWrapper economy;
    protected TransactionCommands transactionCommands;
    protected QueryCommands queryCommands;
    protected Logger logger;
    protected ItemListingDao listingDao;

    // Needed to test some commands
    protected HashMap<String, Double> accounts = new HashMap<String, Double>();
    protected Player player;
    protected PlayerInventory inventory;

    protected File workingDir;

    private void createDirectoryStructure(){
        // Create directories
        workingDir = new File(System.getProperty("user.dir") + File.separator + "plugins");
        if (!workingDir.exists()) {
            workingDir.mkdir();
        }
        doCallRealMethod().when(plugin).createDirectory();
        plugin.createDirectory();
    }

    private void mockJavaPlugin(){
        this.plugin = mock(AdminMarket.class);

        Server mockedServer = mock(Server.class);
        ItemFactory mockedFactory = mock(ItemFactory.class);
        ItemMeta mockedMeta = mock(ItemMeta.class);

        doReturn(true).when(mockedServer).isPrimaryThread();

        /*
        ItemListing.equals() fails without this stub
        because the factory for getting an ItemMeta is only
        in the real (proprietary) server, which isn't available during testing.
        */
        doReturn(mockedFactory).when(mockedServer).getItemFactory();
        doReturn(mockedMeta).when(mockedFactory).getItemMeta(any(Material.class));
    }

    private void mockPlayer(){
        this.player = mock(Player.class);
        this.inventory = mock(PlayerInventory.class);

        doReturn("ANY_PLAYER").when(player).getName();
        doReturn(inventory).when(player).getInventory();
    }

    private void stubAdminMarketMethods(){
        this.logger = Logger.getAnonymousLogger();
        doReturn(logger).when(plugin).getLog();

        this.config = mock(Config.class);
        doReturn(config).when(plugin).getPluginConfig();

        this.economy = new BasicEconomyWrapperImpl(accounts);
        doReturn(economy).when(plugin).getEconomyWrapper();

        this.listingDao = new ItemListingYamlDao(plugin);
        doReturn(listingDao).when(plugin).getListingDao();

        this.queryCommands = new QueryCommands(plugin);
        doReturn(queryCommands).when(plugin).getQueryCommands();

        this.transactionCommands = new TransactionCommands(plugin);
        doReturn(transactionCommands).when(plugin).getTransactionCommands();
    }

    protected void preparePluginMock() {
        // For plugin
        mockJavaPlugin();
        createDirectoryStructure();
        stubAdminMarketMethods();

        // For some tests
        mockPlayer();
    }
    
    protected boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return(directory.delete());
    }

    @AfterMethod
    public void tearDown() {
        // Delete the ~/plugins directory for the next test
        deleteDirectory(workingDir);
    }
}
