package me.zxoir.smp.commands;

import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import me.zxoir.smp.utilities.Colors;
import me.zxoir.smp.utilities.GlobalCache;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/17/2022
 */
public class AfkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player))
            return true;

        User user = UserManager.getUser(player.getUniqueId());

        if (args.length == 1) {
            if (!player.hasPermission(GlobalCache.STAFFPERMISSION)) {
                player.sendMessage(GlobalCache.NOPERMISSION);
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(Colors.error + "That player is not Online");
                return true;
            }

            User targetUser = UserManager.getUser(target.getUniqueId());

            if (targetUser.getCache().isAfk()) {
                targetUser.getCache().setIsAfk(false);
                Title.Times times = Title.Times.times(Duration.ZERO, Duration.ZERO, Duration.ofSeconds(2));
                Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(colorize("&e&lYou are AFK")), LegacyComponentSerializer.legacySection().deserialize(""), times);
                target.showTitle(title);

                player.sendMessage(Colors.secondary + target.getName() + Colors.primary + " is no longer Afk");
            } else {
                targetUser.getCache().setIsAfk(true);
                Title.Times times = Title.Times.times(Duration.ofSeconds(2), Duration.ofDays(1), Duration.ofSeconds(1));
                Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(colorize("&e&lYou are AFK")), LegacyComponentSerializer.legacySection().deserialize(""), times);
                target.showTitle(title);

                player.sendMessage(Colors.secondary + target.getName() + Colors.primary + " is now Afk");
            }

            return true;
        }

        if (user.getCache().isAfk()) {
            user.getCache().setIsAfk(false);
            Title.Times times = Title.Times.times(Duration.ZERO, Duration.ZERO, Duration.ofSeconds(2));
            Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(colorize("&e&lYou are AFK")), LegacyComponentSerializer.legacySection().deserialize(""), times);
            player.showTitle(title);
        } else {
            user.getCache().setIsAfk(true);
            Title.Times times = Title.Times.times(Duration.ofSeconds(2), Duration.ofDays(1), Duration.ofSeconds(1));
            Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(colorize("&e&lYou are AFK")), LegacyComponentSerializer.legacySection().deserialize(""), times);
            player.showTitle(title);
        }

        return true;
    }

}