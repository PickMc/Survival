package me.zxoir.smp.menusystem;

import me.zxoir.smp.utilities.ItemStackBuilder;
import org.bukkit.Material;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */
public abstract class PaginatedMenu extends Menu {
    protected int page = 0;
    protected int maxItemsPerPage = 28;
    protected int index = 0;

    public PaginatedMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    //Set the border and menu buttons for the menu
    public void addMenuBorder() {
        inventory.setItem(48, new ItemStackBuilder(Material.DARK_OAK_BUTTON).withName("&aLeft").build());

        inventory.setItem(49, new ItemStackBuilder(Material.BARRIER).withName("&4Close").build());

        inventory.setItem(50, new ItemStackBuilder(Material.DARK_OAK_BUTTON).withName("&aRight").build());

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
        }

        inventory.setItem(17, super.FILLER_GLASS);
        inventory.setItem(18, super.FILLER_GLASS);
        inventory.setItem(26, super.FILLER_GLASS);
        inventory.setItem(27, super.FILLER_GLASS);
        inventory.setItem(35, super.FILLER_GLASS);
        inventory.setItem(36, super.FILLER_GLASS);

        for (int i = 44; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
        }
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}
