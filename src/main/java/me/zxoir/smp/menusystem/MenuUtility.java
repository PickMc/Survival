package me.zxoir.smp.menusystem;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */

@Getter
public class MenuUtility {
    private final Player owner;
    private final HashMap<Integer, ItemStack> cachedItems = new HashMap<>();

    public MenuUtility(Player player) {
        this.owner = player;
    }
}
