package com.wjbolles.eco.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Logger;

import com.wjbolles.AdminMarket;
import com.wjbolles.adminmarket.utils.Consts;
import com.wjbolles.command.CommandUtil;
import com.wjbolles.eco.model.ItemListing;

import com.wjbolles.eco.model.ItemListingBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class ItemListingSqliteDao implements ItemListingDao {

    private AdminMarket plugin;
    private Logger log;
    private static final String URL = "jdbc:sqlite:" + Consts.PLUGIN_CONF_DIR + File.separatorChar + "items.db";

    public ItemListingSqliteDao(AdminMarket plugin) {
        this.plugin = plugin;
        this.log = plugin.getLog();
        loadItems();
    }

    public static void connect() {
        Connection conn = null;
        Statement statement = null;

        try {
            // create a connection to the database
            conn = DriverManager.getConnection(ItemListingSqliteDao.URL);
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
        HashMap<String, ItemListing> listings = new HashMap<String, ItemListing>();

        Connection conn = null;
        Statement statement = null;

        try {
            conn = DriverManager.getConnection(ItemListingSqliteDao.URL);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select * from items");
            while (rs.next()) {
                Material material = CommandUtil.materialFactory(rs.getString("material"));

                ItemListing listing = new ItemListingBuilder(material)
                        .setInfinite(rs.getInt("is_infinite") == 0 ? false : true)
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
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
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

        try {
            conn = DriverManager.getConnection(ItemListingSqliteDao.URL);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select * from items where material =\"" + material.toString() +"\"");

            listing = new ItemListingBuilder(material)
                .setInfinite(rs.getInt("is_infinite") == 0 ? false : true)
                .setInventory(rs.getInt("inventory"))
                .setValueAddedTax(rs.getFloat("vat"))
                .setBasePrice(rs.getFloat("base_price"))
                .setEquilibrium(rs.getInt("equilibrium"))
                .setConfig(plugin.getPluginConfig())
                .build();

            return listing;

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
                System.err.println(e.getMessage());
            }
        }
        return listing;
    }

    @Override
    public void insertItemListing(ItemListing listing) throws Exception {

        String sql = "INSERT INTO items ( material, is_infinite, inventory, vat, base_price, equilibrium) VALUES(?,?,?,?,?,?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(ItemListingSqliteDao.URL);
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, listing.getMaterial().toString() );
            pstmt.setInt(2, listing.isInfinite() ? 1 : 0);
            pstmt.setInt(3, listing.getInventory());
            pstmt.setDouble(4, listing.getValueAddedTax());
            pstmt.setDouble(5, listing.getBasePrice());
            pstmt.setInt(6, listing.getEquilibrium());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
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
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(ItemListingSqliteDao.URL);
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, listing.isInfinite() ? 1 : 0);
            pstmt.setInt(2, listing.getInventory());
            pstmt.setDouble(3, listing.getValueAddedTax());
            pstmt.setDouble(4, listing.getBasePrice());
            pstmt.setInt(5, listing.getEquilibrium());
            pstmt.setString(6, listing.getMaterial().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
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
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(ItemListingSqliteDao.URL);
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, listing.getMaterial().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
    }
}
