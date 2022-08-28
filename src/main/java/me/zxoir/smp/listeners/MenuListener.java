package me.zxoir.smp.listeners;

import me.zxoir.smp.menusystem.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */
public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(@NotNull InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (event.getCurrentItem() == null || event.getClickedInventory() == null || !(holder instanceof Menu menu))
            return;

        menu.handleMenu(event);
    }
}
