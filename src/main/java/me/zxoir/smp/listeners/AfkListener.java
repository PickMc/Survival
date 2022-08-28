package me.zxoir.smp.listeners;

import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import static me.zxoir.smp.utilities.BasicUtilities.runTaskAsync;
import static me.zxoir.smp.utilities.BasicUtilities.runTaskSync;
import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/6/2022
 */
public class AfkListener implements Listener {
    final long AFK_BLOCK_TIMER = 120;
    final long AFK_YAW_PITCH_TIMER = 160;
    ConcurrentHashMap<Player, BukkitTask> checkTask = new ConcurrentHashMap<>();
    ConcurrentHashMap<Player, BukkitTask> check2Task = new ConcurrentHashMap<>();

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        user.getCache().setIsAfk(false);
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        user.getCache().setIsAfk(false);
    }

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());
        user.getCache().setIsAfk(true);
    }

    @EventHandler
    public void onRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());

        user.getCache().setIsAfk(false);
        player.resetTitle();
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());

        runTaskAsync(() -> {
            final int fromX = (int) event.getFrom().getX();
            final int fromZ = (int) event.getFrom().getZ();
            final int toX = (int) event.getTo().getX();
            final int toZ = (int) event.getTo().getZ();

            final int fromPitch = (int) event.getFrom().getPitch();
            final int fromYaw = (int) event.getFrom().getYaw();
            final int toPitch = (int) event.getTo().getPitch();
            final int toYaw = (int) event.getTo().getYaw();

            if (fromX == toX && fromZ == toZ) {

                if (user.getCache().isAfk())
                    return;

                if (checkTask.containsKey(player))
                    checkTask.get(player).cancel();

                BukkitTask task = Bukkit.getScheduler().runTaskLater(SMP.getPlugin(SMP.class), () -> {

                    int currentX = (int) player.getLocation().getX();
                    int currentZ = (int) player.getLocation().getZ();

                    if (user.getCache().isAfk())
                        return;

                    if (toX == currentX && toZ == currentZ) {
                        Title.Times times = Title.Times.times(Duration.ofSeconds(2), Duration.ofDays(1), Duration.ofSeconds(1));
                        Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(colorize("&e&lYou are AFK")), LegacyComponentSerializer.legacySection().deserialize(""), times);
                        runTaskSync(() -> player.showTitle(title));
                        user.getCache().setIsAfk(true);
                    }

                }, 20L * AFK_BLOCK_TIMER);

                checkTask.put(player, task);

            } else if (fromPitch == toPitch && fromYaw == toYaw) {

                if (user.getCache().isAfk())
                    return;

                if (check2Task.containsKey(player))
                    return;

                BukkitTask task = Bukkit.getScheduler().runTaskLater(SMP.getPlugin(SMP.class), () -> {

                    if (user.getCache().isAfk()) {
                        check2Task.remove(player);
                        return;
                    }

                    int currentPitch = (int) player.getLocation().getPitch();
                    int currentYaw = (int) player.getLocation().getYaw();

                    if (toPitch == currentPitch && toYaw == currentYaw) {
                        Title.Times times = Title.Times.times(Duration.ofSeconds(2), Duration.ofDays(1), Duration.ofSeconds(1));
                        Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(colorize("&e&lYou are AFK")), LegacyComponentSerializer.legacySection().deserialize(""), times);
                        runTaskSync(() -> player.showTitle(title));
                        user.getCache().setIsAfk(true);
                    }

                    check2Task.remove(player);

                }, 20L * AFK_YAW_PITCH_TIMER);

                check2Task.put(player, task);
            } else if (user.getCache().isAfk()) {
                Title.Times times = Title.Times.times(Duration.ZERO, Duration.ZERO, Duration.ofSeconds(2));
                Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(colorize("&e&lYou are AFK")), LegacyComponentSerializer.legacySection().deserialize(""), times);
                runTaskSync(() -> player.showTitle(title));
                user.getCache().setIsAfk(false);

                if (checkTask.containsKey(player)) {
                    checkTask.get(player).cancel();
                    checkTask.remove(player);
                }

                if (check2Task.containsKey(player)) {
                    check2Task.get(player).cancel();
                    check2Task.remove(player);
                }
            }
        });
    }

}
