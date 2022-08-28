package me.zxoir.smp.tabcompleters;

import me.zxoir.smp.utilities.GlobalCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/18/2022
 */
public class LuckyBlockCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();

        if (!(sender instanceof Player player))
            return completer;

        if (!player.hasPermission(GlobalCache.STAFFPERMISSION))
            return completer;

        if (args.length == 1) {
            completer.add("getLuckyBlock");
            completer.add("setLuckyBlock");
            completer.add("addItem");
            completer.add("Items");

            return completer;
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("addItem")) {
                completer.add("<Chance>");
                return completer;
            }

        }

        return completer;
    }
}
