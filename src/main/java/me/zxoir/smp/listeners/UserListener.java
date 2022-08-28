package me.zxoir.smp.listeners;

import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */
public class UserListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (UserManager.getUsers().containsKey(player.getUniqueId()))
            return;

        UserManager.cacheUser(new User(player.getUniqueId()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        user.save();
    }
}
