package me.zxoir.smp.listeners;

import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/8/2022
 */
public class PlaytimeListener implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        user.getCache().setSessionStart(Instant.now());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        UserManager.updateUserPlaytime(user);
        user.getCache().setSessionStart(null);
        Bukkit.getLogger().info("UPDATED USER PLAYTIME");
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        UserManager.updateUserPlaytime(user);
        user.getCache().setSessionStart(null);
    }
}
