package com.wjbolles.eco.dao;

import org.junit.Test;

public class SqliteItemListingDaoTest extends AbstractItemListingDaoTest {
    public SqliteItemListingDaoTest() {
        plugin.setListingDao(new SqliteItemListingDao(plugin));
    }
}
