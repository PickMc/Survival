package me.zxoir.smp.listeners;

import lombok.Getter;
import lombok.Setter;
import me.zxoir.smp.managers.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.smp.utilities.BasicUtilities.runTaskSync;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/8/2022
 */
public class FirstJoinKitListener implements Listener {
    @Getter
    @Setter
    private static ItemStack[] kitItems;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFirstJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (UserManager.getUsers().containsKey(player.getUniqueId()) || kitItems == null)
            return;

        runTaskSync(() -> player.getInventory().setContents(kitItems));
    }

}
