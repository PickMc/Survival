package me.zxoir.smp.menusystem.menus;

import me.zxoir.smp.SMP;
import me.zxoir.smp.menusystem.Menu;
import me.zxoir.smp.menusystem.MenuUtility;
import me.zxoir.smp.utilities.ItemStackBuilder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */
public class SettingsMenu extends Menu {

    public SettingsMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return "&a&lSMP Settings";
    }

    @Override
    public int getSlots() {
        return 9 * 3;
    }

    @Override
    public void handleMenu(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
            return;

        if (event.getCurrentItem().getType().equals(Material.DIAMOND)) {

            if (SMP.getRareItems().isEmpty()) {
                event.getWhoClicked().sendMessage(LegacyComponentSerializer.legacySection().deserialize(colorize("&c&lThere aren't any Rare Items found")));
                return;
            }

            new RareItemsMenu(menuUtility).open();
        }

        if (event.getCurrentItem().getType().equals(Material.CRAFTING_TABLE)) {

            if (SMP.getCraftableItems().isEmpty()) {
                event.getWhoClicked().sendMessage(LegacyComponentSerializer.legacySection().deserialize(colorize("&c&lThere aren't any Craftable Items found")));
                return;
            }

            new CraftableItemsMenu(menuUtility).open();

        }

    }

    @Override
    public void setMenuItems() {
        ItemStack soonItem = new ItemStackBuilder(Material.BARRIER).withName("&c&lSOON").build();
        ItemStack editRareItems = new ItemStackBuilder(Material.DIAMOND).withName("&eEdit Rare Items").withLore("&7Click to view/edit Rare Items").build();
        ItemStack editCraftableItems = new ItemStackBuilder(Material.CRAFTING_TABLE).withName("&eEdit Craftable Items").withLore("&7Click to view/edit Craftable Items").build();

        inventory.setItem(9, editRareItems);
        inventory.setItem(11, editCraftableItems);
        inventory.setItem(13, soonItem);
        inventory.setItem(15, soonItem);
        inventory.setItem(17, soonItem);

        setFillerGlass();
    }
}
