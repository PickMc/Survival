package me.zxoir.smp.commands;

import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/19/2023
 */
public class BagCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player))
            return true;

        User user = UserManager.getUser(player.getUniqueId());

        if (args.length == 1 && player.isOp()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            User targetUser = UserManager.getUser(offlinePlayer.getUniqueId());

            if (targetUser == null) {
                player.sendMessage("Can't find " + args[0]);
                return true;
            }

            player.openInventory(targetUser.getBag().getInventory());
            return true;
        }

        if (user.getStats().getLevel().get() < 6) {
            player.sendMessage(colorize("&cYou must be level 6 or above to use this command!"));

            return true;
        }

        player.openInventory(user.getBag().getInventory());
        return true;
    }
}
