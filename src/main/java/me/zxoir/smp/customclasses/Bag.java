package me.zxoir.smp.customclasses;

import lombok.Getter;
import me.zxoir.smp.managers.UserManager;
import me.zxoir.smp.utilities.BagHolder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/19/2023
 */

@Getter
public class Bag {
    UUID uuid;
    ItemStack[] bagContent;
    int bagSize;

    public Bag(UUID uuid) {
        this.uuid = uuid;
        bagContent = null;
        bagSize = 0;
    }

    public Bag(UUID uuid, ItemStack[] bagContent, int bagSize) {
        this.uuid = uuid;
        this.bagContent = bagContent;
        this.bagSize = bagSize;
    }

    public Inventory getInventory() {
        if (bagSize == 0)
            updateBagSize();

        if (bagContent == null)
            bagContent = new ItemStack[0];

        Inventory inventory = Bukkit.createInventory(new BagHolder(UserManager.getUser(uuid)), bagSize, LegacyComponentSerializer.legacySection().deserialize("Bag"));
        inventory.setContents(bagContent);
        return inventory;
    }

    public void setBagContent(ItemStack[] bagContent) {
        this.bagContent = bagContent;
    }

    public boolean updateBagSize() {
        int newInventorySize = checkInventorySize();
        if (newInventorySize == bagSize)
            return false;

        this.bagSize = newInventorySize;
        return true;
    }

    private int checkInventorySize() {
        User user = UserManager.getUser(uuid);
        int level = user.getStats().getLevel().get();
        int slots = (level / 5 + 1) * 9;
        if (level >= 20) {
            slots += 36; // add the maximum number of slots for level 20 and above
        } else if (level % 5 != 0) {
            slots += (level % 5 - 1) * 9; // add any remaining slots for levels not divisible by 5
        }

        return slots;
    }
}
