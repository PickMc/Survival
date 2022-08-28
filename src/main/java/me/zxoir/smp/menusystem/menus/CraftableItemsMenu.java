package me.zxoir.smp.menusystem.menus;

import me.zxoir.smp.SMP;
import me.zxoir.smp.menusystem.MenuUtility;
import me.zxoir.smp.menusystem.PaginatedMenu;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/16/2022
 */
public class CraftableItemsMenu extends PaginatedMenu {

    public CraftableItemsMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return "&eEdit Craftable Items";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(@NotNull InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();
        event.setCancelled(true);

        if (clickedInventory.getType().equals(InventoryType.PLAYER))
            return;

        if (clickedItem.getType().equals(Material.BARRIER)) {

            new SettingsMenu(menuUtility).open();

        } else if (event.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)) {
            String displayName = LegacyComponentSerializer.legacySection().serialize(clickedItem.getItemMeta().displayName());

            if (ChatColor.stripColor(displayName).equalsIgnoreCase("Left")) {
                if (page != 0) {
                    page = page - 1;
                    super.open();
                }
            } else if (ChatColor.stripColor(displayName).equalsIgnoreCase("Right")) {

                if (!((index + 1) >= SMP.getCraftableItems().size())) {
                    page = page + 1;
                    super.open();
                }

            }
        }

    }

    @Override
    public void setMenuItems() {

        addMenuBorder();

        if (!SMP.getCraftableItems().isEmpty()) {

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= SMP.getCraftableItems().size()) break;

                Material material = (Material) SMP.getCraftableItems().toArray()[index];

                if (SMP.getCraftableItems().toArray()[index] != null)
                    inventory.addItem(new ItemStack(material));

            }
        }
    }
}
