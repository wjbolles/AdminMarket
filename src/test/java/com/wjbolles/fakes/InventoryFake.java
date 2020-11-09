/*
 * AdminMarket
 *
 * Copyright 2017 by Walter Bolles <mail@wjbolles.com>
 *
 * Licensed under the Apache License, Version 2.0
 */

package com.wjbolles.fakes;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryFake implements PlayerInventory {

    final int INVENTORY_SIZE = 10;
    final int STANDARD_STACK_SIZE = 64;
    ItemStack[] inventoryMock = new ItemStack[INVENTORY_SIZE];
    ItemStack mainHand = null;

    /**
     * This assumes you're only sending in one stack, and that stack has amount 1.
     * For our purposes, that is all we expect.
     */
    public HashMap<Integer, ItemStack> addItem(ItemStack... arg0) throws IllegalArgumentException {

        HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>();
        ItemStack stack = arg0[0];

        // Try to fit the individual stack in each pseudo-inventory slot.
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            // If it's null, it's a free slot
            if (inventoryMock[i] == null) {
                inventoryMock[i] = stack;
                break;
            }

            if (inventoryMock[i].getType().equals(stack.getType())
                    && inventoryMock[i].getAmount() != inventoryMock[i].getMaxStackSize()) {

                inventoryMock[i].setAmount(inventoryMock[i].getAmount() + 1);
                stack = null;
            }

            if (stack == null) {
                break;
            }

            if (i == INVENTORY_SIZE - 1) {
                leftOver.put(0, stack);
            }
        }

        return leftOver;
    }

    public HashMap<Integer, ? extends ItemStack> all(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap<Integer, ? extends ItemStack> all(Material arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap<Integer, ? extends ItemStack> all(ItemStack arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void clear() {
        // TODO Auto-generated method stub

    }

    public void clear(int arg0) {
        // TODO Auto-generated method stub

    }

    public boolean contains(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean contains(Material arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean contains(ItemStack arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean contains(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean contains(Material arg0, int arg1) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean contains(ItemStack arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean containsAtLeast(ItemStack arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    public int first(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int first(Material arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int first(ItemStack arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int firstEmpty() {
        // TODO Auto-generated method stub
        return 0;
    }

    public ItemStack[] getContents() {
        // TODO Auto-generated method stub
        return null;
    }

    public ItemStack getItem(int arg0) {
        return inventoryMock[arg0];
    }

    public int getMaxStackSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getSize() {
        return INVENTORY_SIZE;
    }

    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    public InventoryType getType() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<HumanEntity> getViewers() {
        // TODO Auto-generated method stub
        return null;
    }

    public ListIterator<ItemStack> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    public ListIterator<ItemStack> iterator(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Location getLocation() {
        return null;
    }

    public void remove(int i) {
        inventoryMock[i] = null;
    }

    public void remove(Material arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    public void remove(ItemStack arg0) {
        // TODO Auto-generated method stub

    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    public void setContents(ItemStack[] arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    public ItemStack[] getStorageContents() {
        return new ItemStack[0];
    }

    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {

    }

    public void setMaxStackSize(int arg0) {
        // TODO Auto-generated method stub

    }

    public int clear(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return 0;
    }

    public ItemStack[] getArmorContents() {
        // TODO Auto-generated method stub
        return null;
    }

    public ItemStack[] getExtraContents() {
        return new ItemStack[0];
    }

    public ItemStack getBoots() {
        // TODO Auto-generated method stub
        return null;
    }

    public ItemStack getChestplate() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getHeldItemSlot() {
        // TODO Auto-generated method stub
        return 0;
    }

    public ItemStack getHelmet() {
        // TODO Auto-generated method stub
        return null;
    }

    public HumanEntity getHolder() {
        // TODO Auto-generated method stub
        return null;
    }

    public ItemStack getItemInHand() {
        // TODO Auto-generated method stub
        return null;
    }

    public ItemStack getLeggings() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setArmorContents(ItemStack[] arg0) {
        // TODO Auto-generated method stub

    }

    public void setExtraContents(ItemStack[] itemStacks) {

    }

    public void setBoots(ItemStack arg0) {
        // TODO Auto-generated method stub

    }

    public ItemStack getItemInMainHand() {
        return this.mainHand;
    }

    public void setItemInMainHand(ItemStack itemStack) {
        this.mainHand = itemStack;
    }

    public ItemStack getItemInOffHand() {
        return null;
    }

    public void setItemInOffHand(ItemStack itemStack) {

    }

    public void setChestplate(ItemStack arg0) {
        // TODO Auto-generated method stub

    }

    public void setHeldItemSlot(int arg0) {
        // TODO Auto-generated method stub

    }

    public void setHelmet(ItemStack arg0) {
        // TODO Auto-generated method stub

    }

    public void setItem(int arg0, ItemStack arg1) {
        inventoryMock[arg0] = arg1;
    }

    public void setItemInHand(ItemStack arg0) {
        // TODO Auto-generated method stub

    }

    public void setLeggings(ItemStack arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setItem(EquipmentSlot slot, ItemStack item) {
        // TODO Auto-generated method stub

    }

    @Override
    public ItemStack getItem(EquipmentSlot slot) {
        // TODO Auto-generated method stub
        return null;
    }

}
