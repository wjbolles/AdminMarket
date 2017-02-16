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
import java.util.HashMap;
import java.util.logging.Logger;

import com.wjbolles.eco.EconomyWrapper;
import com.wjbolles.eco.ListingManager;
import com.wjbolles.eco.TransactionManager;

import com.wjbolles.fakes.EconomyWrapperImpl;

public class AdminMarketTest {
    
    // Plugin mock fields
    protected AdminMarket plugin;
    protected EconomyWrapper economy;
    protected TransactionManager tm;
    protected ListingManager lm;
    protected Logger logger;
    protected File workingDir;
    protected HashMap<String, Double> accounts;
    
    protected void preparePluginMock() {      
        // Build plugin
        plugin = mock(AdminMarket.class);
        accounts = new HashMap<String, Double>();
        economy = new EconomyWrapperImpl(accounts);
        
        // Create directories
        workingDir = new File(System.getProperty("user.dir") + File.separator + "plugins");
        if (!workingDir.exists()) {
            workingDir.mkdir();
        }
        AdminMarket.createDirectory();
        
        // Stubs
        when(plugin.getEconomy()).thenReturn(economy);
        when(plugin.getLog()).thenReturn(logger);
        
        lm = new ListingManager(plugin);
        when(plugin.getListingManager()).thenReturn(lm);
        
        tm = new TransactionManager(plugin);
        when(plugin.getTransactionManager()).thenReturn(tm);
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
}
