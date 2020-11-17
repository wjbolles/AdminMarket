package com.wjbolles.adminmarket.eco.model;

import com.wjbolles.adminmarket.AdminMarketConfig;
import org.bukkit.Material;

public class ItemListingBuilder {

    private final Material material;
    private boolean isInfinite;
    private int inventory;
    private double basePrice;
    private double valueAddedTax;
    private int equilibrium;
    private AdminMarketConfig config;

    public ItemListingBuilder(Material material) {
        this.material = material;
    }

    public ItemListingBuilder setInfinite(boolean infinite) {
        isInfinite = infinite;
        return this;
    }

    public ItemListingBuilder setInventory(int inventory) {
        this.inventory = inventory;
        return this;
    }

    public ItemListingBuilder setBasePrice(double basePrice) {
        this.basePrice = basePrice;
        return this;
    }

    public ItemListingBuilder setValueAddedTax(double valueAddedTax) {
        this.valueAddedTax = valueAddedTax;
        return this;
    }

    public ItemListingBuilder setEquilibrium(int equilibrium) {
        this.equilibrium = equilibrium;
        return this;
    }

    public ItemListingBuilder setConfig(AdminMarketConfig config) {
        this.config = config;
        return this;
    }

    public ItemListing build() {
        return new ItemListing(material, isInfinite, basePrice, inventory, valueAddedTax, equilibrium, config);
    }
}
