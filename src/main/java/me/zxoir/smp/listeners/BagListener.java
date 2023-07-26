package me.zxoir.smp.listeners;

import me.zxoir.smp.managers.DatabaseManager;
import me.zxoir.smp.utilities.BagHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/20/2023
 */
public class BagListener implements Listener {

    @EventHandler
    public void onBagClose(@NotNull InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() == null || inventory.getHolder().getClass() != BagHolder.class)
            return;

        BagHolder bagHolder = (BagHolder) inventory.getHolder();
        bagHolder.getUser().getBag().setBagContent(inventory.getContents());
        DatabaseManager.updateBagToDatabase(bagHolder.getUser());
    }

}
