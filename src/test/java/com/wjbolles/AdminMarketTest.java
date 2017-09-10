/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles;

import static org.mockito.Mockito.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Logger;

import com.wjbolles.command.QueryCommands;
import com.wjbolles.command.TransactionCommands;
import com.wjbolles.eco.economy.EconomyWrapper;

import com.wjbolles.eco.economy.BasicEconomyWrapperImpl;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.testng.annotations.AfterMethod;

public class AdminMarketTest {
    
    // Plugin mock fields
    protected AdminMarket plugin;
    protected EconomyWrapper economy;
    protected TransactionCommands tm;
    protected QueryCommands lm;
    protected Logger logger;
    protected File workingDir;
    protected HashMap<String, Double> accounts;
    protected Config config;
    protected Player player;
    protected PlayerInventory inventory;
    
    protected void preparePluginMock() {      

        accounts = new HashMap<String, Double>();
        economy = new BasicEconomyWrapperImpl(accounts);
        config = new Config();
        
        // Create directories
        workingDir = new File(System.getProperty("user.dir") + File.separator + "plugins");
        if (!workingDir.exists()) {
            workingDir.mkdir();
        }
        AdminMarket.createDirectory();

        // Mocks
        plugin = mock(AdminMarket.class);
        Server mockedServer = mock(Server.class);
        ItemFactory mockedFactory = mock(ItemFactory.class);
        ItemMeta mockedMeta = mock(ItemMeta.class);
        player = mock(Player.class);
        inventory = mock(PlayerInventory.class);
        
        // Stubs
        when(plugin.getEconomyWrapper()).thenReturn(economy);
        when(plugin.getLog()).thenReturn(logger);
        
        lm = new QueryCommands(plugin);
        when(plugin.getListingManager()).thenReturn(lm);

        when(plugin.getPluginConfig()).thenReturn(config);
        tm = new TransactionCommands(plugin);

        when(plugin.getTransactionCommands()).thenReturn(tm);
        when(mockedServer.getItemFactory()).thenReturn(mockedFactory);
        when(mockedServer.isPrimaryThread()).thenReturn(true);
        when(mockedFactory.getItemMeta(any(Material.class))).thenReturn(mockedMeta);

        when(player.getName()).thenReturn("ANY_PLAYER");
        when(player.getInventory()).thenReturn(inventory);

       // setStaticField(Bukkit.class, "server", mockedServer);
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

    /*
        ItemListing.equals() fails without this spy
        because the factory for getting an ItemMeta is only
        in the server, which isn't available during testing.
        Item stacks should be built with this method when
        equals() is going to be used later.
    */
    public static ItemStack getItemStackSpy(Material type, int amount, short damage){
        ItemStack stackSpy = spy(new ItemStack(type, amount, damage));
        doReturn(false).when(stackSpy).hasItemMeta();
        return stackSpy;
    }

    private static void setStaticField(Class<?> parent, String name, Object value) {
        try {
            Field field = parent.getDeclaredField(name);
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot set static field " + name + ".", e);
        }
    }
}
