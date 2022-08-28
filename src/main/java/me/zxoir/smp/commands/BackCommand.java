package me.zxoir.smp.commands;

import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.customclasses.Warp;
import me.zxoir.smp.managers.UserManager;
import me.zxoir.smp.utilities.Colors;
import me.zxoir.smp.utilities.GlobalCache;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */
public class BackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        User user = UserManager.getUser(player.getUniqueId());

        if (args.length == 3 && args[0].equalsIgnoreCase("whitelist") && args[1].equalsIgnoreCase("add")) {
            if (!player.hasPermission(GlobalCache.STAFFPERMISSION)) {
                player.sendMessage(GlobalCache.NOPERMISSION);
                return true;
            }

            World world = Bukkit.getWorld(args[2]);
            if (world == null) {
                player.sendMessage(Colors.error + "No such world with that name!");
                return true;
            }

            if (GlobalCache.getWhitelistedBackWorlds().contains(world)) {
                player.sendMessage(Colors.error + "That World is already whitelisted");
                return true;
            }

            GlobalCache.getWhitelistedBackWorlds().add(world);
            player.sendMessage(Colors.primary + "Successfully whitelisted " + Colors.secondary + world.getName());
            return true;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("whitelist") && args[1].equalsIgnoreCase("remove")) {
            if (!player.hasPermission(GlobalCache.STAFFPERMISSION)) {
                player.sendMessage(GlobalCache.NOPERMISSION);
                return true;
            }

            World world = Bukkit.getWorld(args[2]);
            if (world == null) {
                player.sendMessage(Colors.error + "No such world with that name!");
                return true;
            }

            if (!GlobalCache.getWhitelistedBackWorlds().contains(world)) {
                player.sendMessage(Colors.error + "That World is not whitelisted");
                return true;
            }

            GlobalCache.getWhitelistedBackWorlds().remove(player.getWorld());
            player.sendMessage(Colors.secondary + player.getWorld().getName() + Colors.primary + " is no longer whitelisted");
            return true;
        }

        if (!GlobalCache.getWhitelistedBackWorlds().contains(player.getWorld())) {
            player.sendMessage(Colors.error + "You can't use that here!");
            return true;
        }

        if (user.getCache().getBackLocation() == null) {
            player.sendMessage(Colors.error + "Location not found");
            return true;
        }

        Warp warp = new Warp(user.getCache().getBackLocation());

        if (player.hasPermission(GlobalCache.STAFFPERMISSION))
            warp.teleport(player);
        else
            warp.teleport(player, 3);

        return true;
    }
}