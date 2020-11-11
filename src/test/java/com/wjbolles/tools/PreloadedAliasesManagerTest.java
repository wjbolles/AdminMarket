/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.tools;

import java.util.ArrayList;
import java.util.List;

import com.wjbolles.AdminMarketTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PreloadedAliasesManagerTest extends AdminMarketTest {
    @Before
    public void setup() {
        PreloadedAliasesManager.initialize(plugin);
    }
    
    @SuppressWarnings("deprecation")
    @Test
    @Ignore
    public void test() {
        System.out.println(PreloadedAliasesManager.aliasMap.size());

        List<String> keys = new ArrayList<String>(PreloadedAliasesManager.aliasMap.keySet());

        for (String key : keys) {
            System.out.println(key + ": " + PreloadedAliasesManager.aliasMap.get(key).getType()+"-"+PreloadedAliasesManager.aliasMap.get(key).getDurability());
        }
    }
}
