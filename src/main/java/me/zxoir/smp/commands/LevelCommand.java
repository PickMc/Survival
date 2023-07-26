package me.zxoir.smp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/25/2023
 */
public class LevelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        sender.sendMessage(colorize("""
                #7b634f♦ &6Levels:
                   &eLevel 1 &7— &bStarter Kit.
                   &eLevel 3 &7— &bAccess to #c7e8e8&l/friend&b.
                   &eLevel 4 &7— &bAccess to #c7e8e8&l/checkpoint&b.
                   &eLevel 5 &7—  &bAccess to #c7e8e8&l/bag &c[9 Slots]&b.
                   &eLevel 6 &7—  &bAccess to #c7e8e8&l/noweed&b.
                   &eLevel 7 &7—  &bAccess to #c7e8e8&l/arena &4[SOON]&b.
                   &eLevel 8 &7—  &bMax teammates up to 5&b.
                   &eLevel 15 &7—  &bLocate ONE structure &c[Once a month]&b.
                   &eLevel 25 &7—  &bFly in claim&b."""));

        return true;
    }
}
