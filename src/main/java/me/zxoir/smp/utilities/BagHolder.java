package me.zxoir.smp.utilities;

import me.zxoir.smp.customclasses.User;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/20/2023
 */
public class BagHolder implements InventoryHolder {
    private final User user;

    public BagHolder(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
