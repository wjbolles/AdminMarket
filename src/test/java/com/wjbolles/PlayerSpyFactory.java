package com.wjbolles;

import com.wjbolles.adminmarket.fakes.InventoryFake;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
