package me.zxoir.smp.menusystem.menus;

import me.zxoir.smp.customclasses.LuckyBlock;
import me.zxoir.smp.menusystem.MenuUtility;
import me.zxoir.smp.menusystem.PaginatedMenu;
import me.zxoir.smp.utilities.Colors;
import me.zxoir.smp.utilities.ItemStackBuilder;
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
 * @since 8/17/2022
 */
public class LuckyBlockItemsMenu extends PaginatedMenu {

    public LuckyBlockItemsMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return "&eEdit LuckyBlock Items";
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

            event.getWhoClicked().closeInventory();

        } else if (clickedItem.getType().equals(Material.DARK_OAK_BUTTON)) {
            String displayName = LegacyComponentSerializer.legacySection().serialize(clickedItem.getItemMeta().displayName());

            if (ChatColor.stripColor(displayName).equalsIgnoreCase("Left")) {
                if (page != 0) {
                    page = page - 1;
                    super.open();
                }
            } else if (ChatColor.stripColor(displayName).equalsIgnoreCase("Right")) {

                if (!((index + 1) >= LuckyBlock.getRandomItems().size())) {
                    page = page + 1;
                    super.open();
                }

            }
        } else if (!clickedItem.getType().equals(Material.AIR) && !clickedItem.equals(FILLER_GLASS) && event.isRightClick() && menuUtility.getCachedItems().containsKey(event.getSlot())) {
            ItemStack itemStack = menuUtility.getCachedItems().get(event.getSlot());
            LuckyBlock.getRandomItems().remove(itemStack);
            inventory.removeItem(clickedItem);

            event.getWhoClicked().sendMessage(Colors.primary + "Item Removed");
        }

    }

    @Override
    public void setMenuItems() {

        addMenuBorder();

        if (!LuckyBlock.getRandomItems().isEmpty()) {

            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= LuckyBlock.getRandomItems().size()) break;

                ItemStack itemStack = (ItemStack) LuckyBlock.getRandomItems().getLogged().keySet().toArray()[index];

                if (itemStack != null) {
                    ItemStack displayedItem = new ItemStackBuilder(itemStack.clone()).withName(Colors.primary + "Chance: " + Colors.secondary + LuckyBlock.getRandomItems().getLogged().get(itemStack)).withLore(Colors.secondary + "Right click to remove item").build();
                    inventory.addItem(displayedItem);
                    menuUtility.getCachedItems().put(inventory.first(displayedItem), itemStack);
                }
            }
        }
    }
}
