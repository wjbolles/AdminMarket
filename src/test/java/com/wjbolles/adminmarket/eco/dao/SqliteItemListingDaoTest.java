package com.wjbolles.adminmarket.eco.dao;

public class SqliteItemListingDaoTest extends AbstractItemListingDaoTest {
    public SqliteItemListingDaoTest() {
        plugin.setListingDao(new SqliteItemListingDao(plugin));
    }
}
