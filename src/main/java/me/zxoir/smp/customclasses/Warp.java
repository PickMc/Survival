package me.zxoir.smp.customclasses;

import lombok.AllArgsConstructor;
import me.zxoir.smp.SMP;
import me.zxoir.smp.runnables.WarpRunnable;
import me.zxoir.smp.utilities.Colors;
import me.zxoir.smp.utilities.GlobalCache;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */

@AllArgsConstructor
public class Warp {
    private Location location;

    public void teleport(@NotNull Player toBeTeleported) {
        if (GlobalCache.getWarpingPlayers().contains(toBeTeleported))
            return;

        toBeTeleported.teleport(location);
        toBeTeleported.sendActionBar(LegacyComponentSerializer.legacySection().deserialize(Colors.primary + "Teleported!"));
    }

    /**
     * @param time Warp time till player is teleported
     */
    public void teleport(Player toBeTeleported, int time) {
        if (GlobalCache.getWarpingPlayers().contains(toBeTeleported))
            return;

        new WarpRunnable(time, toBeTeleported, location).runTaskTimer(SMP.getPlugin(SMP.class), 0, 20);
        GlobalCache.getWarpingPlayers().add(toBeTeleported);
    }
}
