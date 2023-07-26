package me.zxoir.smp.listeners;

import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.Cache;
import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/20/2023
 */
public class VoidTP implements Listener {
    List<Player> inTp = new ArrayList<>();

    @EventHandler
    public void voidRandom(@NotNull EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER || event.getCause() != EntityDamageEvent.DamageCause.VOID)
            return;

        Player player = (Player) event.getEntity();

        User user = UserManager.getUser(player.getUniqueId());
        Cache cache = user.getCache();

        if (!player.getWorld().getName().equals("Spawn") || player.getLocation().getY() > 0)
            return;

        event.setDamage(0);
        player.setFallDistance(0);
        event.setCancelled(true);

        if (inTp.contains(player))
            return;

        if (cache.getBackLocation() != null) {
            player.teleport(cache.getBackLocation());
            cache.setBackLocation(null);
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rtp player " + player.getName());
        inTp.add(player);

        Bukkit.getScheduler().runTaskLater(SMP.getInstance(), () -> inTp.remove(player), 40);
    }
}
