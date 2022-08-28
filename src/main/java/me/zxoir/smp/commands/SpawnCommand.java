package me.zxoir.smp.commands;

import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.Warp;
import me.zxoir.smp.utilities.Colors;
import me.zxoir.smp.utilities.GlobalCache;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */
public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        Player player = (Player) sender;

        if (args.length == 1 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("setspawn"))) {
            if (!player.hasPermission(GlobalCache.STAFFPERMISSION)) {
                player.sendMessage(GlobalCache.NOPERMISSION);
                return true;
            }

            Location location = player.getLocation();
            GlobalCache.setSpawnWarp(new Warp(location));
            player.getWorld().setSpawnLocation(player.getLocation());
            player.sendMessage(Colors.primary + "Successfully set Spawn point");

            SMP.getDataFile().getConfig().set("Spawn", player.getLocation());
            SMP.getDataFile().saveConfig();
            return true;
        }

        if (GlobalCache.getSpawnWarp() == null) {
            player.sendMessage(Colors.error + "Please contact an Admin to set a Spawn point!");
            return true;
        }

        if (player.hasPermission(GlobalCache.STAFFPERMISSION))
            GlobalCache.getSpawnWarp().teleport(player);
        else
            GlobalCache.getSpawnWarp().teleport(player, 3);

        return true;
    }
}