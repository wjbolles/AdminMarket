package com.wjbolles;

import com.wjbolles.fakes.InventoryFake;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import static org.mockito.Mockito.doReturn;

public class PlayerSpyFactory {
    @Mock
    private Player player;
    //@Mock
    private PlayerInventory inventory = new InventoryFake();

    public PlayerSpyFactory(){
        MockitoAnnotations.initMocks(this);

        doReturn("ANY_PLAYER").when(player).getName();
        doReturn(inventory).when(player).getInventory();
    }

    public Player getPlayer(){
        return this.player;
    }
}
