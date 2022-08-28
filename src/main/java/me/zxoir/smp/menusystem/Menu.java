package me.zxoir.smp.menusystem;

import me.zxoir.smp.utilities.ItemStackBuilder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */
public abstract class Menu implements InventoryHolder {

    protected MenuUtility menuUtility;
    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = new ItemStackBuilder(Material.GRAY_STAINED_GLASS_PANE).withName("&a").build();

    public Menu(MenuUtility menuUtility) {
        this.menuUtility = menuUtility;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent event);

    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), LegacyComponentSerializer.legacySection().deserialize(colorize(getMenuName())));

        this.setMenuItems();

        menuUtility.getOwner().openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void setFillerGlass() {
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }
}
