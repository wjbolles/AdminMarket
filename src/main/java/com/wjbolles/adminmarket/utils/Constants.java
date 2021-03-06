/*
 * AdminMarket
 *
 * Copyright 2020 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.adminmarket.utils;

import java.io.File;

public final class Constants {
    public static final String PLUGIN_CONF_DIR = "plugins" + File.separatorChar + "AdminMarket";
    public static final String PLUGIN_ITEMS_DIR = PLUGIN_CONF_DIR + File.separatorChar + "items";
    // public static final String PLUGIN_LOG_DIR = PLUGIN_CONF_DIR + File.separatorChar + "log";
    public static final String PLUGIN_IMPORT = PLUGIN_CONF_DIR + File.separatorChar + "import.csv";
}