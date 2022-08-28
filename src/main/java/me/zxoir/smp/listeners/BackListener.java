package me.zxoir.smp.listeners;

import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import me.zxoir.smp.utilities.GlobalCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */
public class BackListener implements Listener {

    @EventHandler
    public void onTeleport(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());

        if (!GlobalCache.getWhitelistedBackWorlds().contains(event.getFrom().getWorld()))
            return;

        if (user.getCache().getBackLocation() != null) {
            user.getCache().setBackLocation(null);
            return;
        }

        user.getCache().setBackLocation(event.getFrom());
    }

}
