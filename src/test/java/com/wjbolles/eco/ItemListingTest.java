/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.eco;

import static org.junit.Assert.assertTrue;

import java.io.File;

import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;

import org.bukkit.Material;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ItemListingTest {
    ItemListing listing;
    File scratch;
    
    @Before
    public void setup() throws Exception {
        
    }
    
    @Test
    public void test() throws Exception {
        ItemInfo info = Items.itemByType(Material.STAINED_GLASS, (short) 5);
        System.out.println(info.toString().replaceAll(" " , ""));
        assertTrue(true);
    }
    
    @After
    public void tearDown() {
        //scratch.delete();
    }
}
