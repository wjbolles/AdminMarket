package com.wjbolles.eco.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.wjbolles.AdminMarket;
import com.wjbolles.adminmarket.utils.Constants;
import com.wjbolles.command.CommandUtil;
import com.wjbolles.eco.model.ItemListing;

import com.wjbolles.eco.model.ItemListingBuilder;
import org.bukkit.Material;


@SuppressWarnings({"ThrowFromFinallyBlock", "CaughtExceptionImmediatelyRethrown"})
public class SqliteItemListingDao implements ItemListingDao {

    private final AdminMarket plugin;
    private static final String URL = "jdbc:sqlite:" + Constants.PLUGIN_CONF_DIR + File.separatorChar + "items.db";

    public SqliteItemListingDao(AdminMarket plugin) {
        this.plugin = plugin;
        loadItems();
    }

    public static void connect() {
        Connection conn = null;
        Statement statement;

        try {
            // create a connection to the database
            conn = DriverManager.getConnection(SqliteItemListingDao.URL);
            statement = conn.createStatement();

            statement.setQueryTimeout(30);
            statement.executeUpdate(
                    "create table if not exists items (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "material TEXT NOT NULL UNIQUE, " +
                            "is_infinite INTEGER NOT NULL, " +
                            "inventory INTEGER NOT NULL, " +
                            "vat REAL NOT NULL," +
                            "base_price REAL NOT NULL, " +
                            "equilibrium INTEGER NOT NULL" +
                            ")"
            );

            System.out.println("Connection to items DB has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public void loadItems() {
        connect();
    }

    @Override
    public HashMap<String, ItemListing> getAllListings() {
        HashMap<String, ItemListing> listings = new HashMap<>();

        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(SqliteItemListingDao.URL);
            statement = conn.createStatement();
            rs = statement.executeQuery("select * from items");
            while (rs.next()) {
                Material material = CommandUtil.materialFactory(rs.getString("material"));

                ItemListing listing = new ItemListingBuilder(material)
                        .setInfinite(rs.getInt("is_infinite") != 0)
                        .setInventory(rs.getInt("inventory"))
                        .setValueAddedTax(rs.getFloat("vat"))
                        .setBasePrice(rs.getFloat("base_price"))
                        .setEquilibrium(rs.getInt("equilibrium"))
                        .setConfig(plugin.getPluginConfig())
                        .build();

                listings.put(material.toString(), listing);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) { statement.close(); }
                if (conn != null) { conn.close(); }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return listings;
    }

    @Override
    public ItemListing findItemListing(Material material) {

        ItemListing listing = null;

        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(SqliteItemListingDao.URL);
            statement = conn.createStatement();
            rs = statement.executeQuery("select * from items where material =\"" + material.toString() +"\"");

            listing = new ItemListingBuilder(material)
                .setInfinite(rs.getInt("is_infinite") != 0)
                .setInventory(rs.getInt("inventory"))
                .setValueAddedTax(rs.getFloat("vat"))
                .setBasePrice(rs.getFloat("base_price"))
                .setEquilibrium(rs.getInt("equilibrium"))
                .setConfig(plugin.getPluginConfig())
                .build();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) { statement.close(); }
                if (conn != null) { conn.close(); }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return listing;
    }

    @Override
    public void insertItemListing(ItemListing listing) throws Exception {

        String sql = "INSERT INTO items ( material, is_infinite, inventory, vat, base_price, equilibrium) VALUES(?,?,?,?,?,?)";

        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = DriverManager.getConnection(SqliteItemListingDao.URL);
            statement = conn.prepareStatement(sql);

            statement.setString(1, listing.getMaterial().toString() );
            statement.setInt(2, listing.isInfinite() ? 1 : 0);
            statement.setInt(3, listing.getInventory());
            statement.setDouble(4, listing.getValueAddedTax());
            statement.setDouble(5, listing.getBasePrice());
            statement.setInt(6, listing.getEquilibrium());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    @Override
    public void updateItemListing(ItemListing listing) throws Exception {

        String sql = "update items set is_infinite = ?, " +
                "inventory = ?," +
                "vat = ?," +
                "base_price = ?," +
                "equilibrium = ? " +
                "where material = ?";

        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = DriverManager.getConnection(SqliteItemListingDao.URL);
            statement = conn.prepareStatement(sql);

            statement.setInt(1, listing.isInfinite() ? 1 : 0);
            statement.setInt(2, listing.getInventory());
            statement.setDouble(3, listing.getValueAddedTax());
            statement.setDouble(4, listing.getBasePrice());
            statement.setInt(5, listing.getEquilibrium());
            statement.setString(6, listing.getMaterial().toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    @Override
    public void deleteItemListing(ItemListing listing) throws Exception {
        String sql = "delete from items where material = ?";

        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = DriverManager.getConnection(SqliteItemListingDao.URL);
            statement = conn.prepareStatement(sql);

            statement.setString(1, listing.getMaterial().toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    @Override
    public boolean listingExists(Material material) {
        return findItemListing(material) != null;
    }
}
