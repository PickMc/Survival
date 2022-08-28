package me.zxoir.smp.menusystem.menus;

import me.zxoir.smp.menusystem.Menu;
import me.zxoir.smp.menusystem.MenuUtility;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */
public class TestMenu extends Menu {

    public TestMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return "&a&lTest Menu";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(@NotNull InventoryClickEvent event) {
        if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
            return;


        event.getWhoClicked().sendMessage("Clicked on something...");
        event.setCancelled(true);
    }

    @Override
    public void setMenuItems() {
        setFillerGlass();
    }
}
