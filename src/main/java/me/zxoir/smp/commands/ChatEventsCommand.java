package me.zxoir.smp.commands;

import me.zxoir.smp.customclasses.ChatEvents;
import me.zxoir.smp.utilities.Colors;
import me.zxoir.smp.utilities.GlobalCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/16/2022
 */
public class ChatEventsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player))
            return true;

        if (!player.hasPermission(GlobalCache.STAFFPERMISSION)) {
            player.sendMessage(GlobalCache.NOPERMISSION);
            return true;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("stop")) {
                if (!ChatEvents.isEventActive()) {
                    player.sendMessage(Colors.error + "There is no Event running.");
                    return true;
                }

                ChatEvents.endEvent(null);
                return true;
            }

            if (args[0].equalsIgnoreCase("status")) {
                if (!ChatEvents.isEventActive()) {
                    player.sendMessage(Colors.error + "There is no Event running.");
                    return true;
                }

                String status = Colors.primary + "Event Type: " + Colors.secondary + ChatEvents.getEventType().name() + "\n" +
                        (ChatEvents.getCurrentWord() != null ? "\n" + Colors.primary + "Word: " + Colors.secondary + ChatEvents.getCurrentWord() : "") +
                        (ChatEvents.getCurrentQuestion() != null ? "\n" + Colors.primary + "Question: " + Colors.secondary + ChatEvents.getCurrentQuestion().question() : "") +
                        (ChatEvents.getCurrentQuestion() != null ? "\n" + Colors.primary + "Answer: " + Colors.secondary + ChatEvents.getCurrentQuestion().answer() : "") +
                        (ChatEvents.getCurrentMaterial() != null ? "\n" + Colors.primary + "Item: " + Colors.secondary + ChatEvents.getCurrentMaterial().name() : "");

                player.sendMessage(status);
                return true;
            }

        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("forcewin")) {
                if (!ChatEvents.isEventActive()) {
                    player.sendMessage(Colors.error + "There is no Event running.");
                    return true;
                }

                Player winner = Bukkit.getPlayer(args[1]);
                if (winner == null || !winner.isOnline()) {
                    player.sendMessage(Colors.error + "That player is not Online.");
                    return true;
                }

                ChatEvents.endEvent(winner);
                return true;
            }

        }

        if (args.length >= 1) {

            if (args[0].equalsIgnoreCase("start")) {
                if (ChatEvents.isEventActive()) {
                    player.sendMessage(Colors.error + "There is an Event running.");
                    return true;
                }

                if (args.length == 1) {
                    ChatEvents.startEvent(null);
                    return true;
                }

                if (args.length == 2) {
                    ChatEvents.EventType eventType;

                    try {
                        eventType = ChatEvents.EventType.valueOf(args[1].toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                        player.sendMessage(Colors.error + "There is no Event Type with that name.");
                        return true;
                    }

                    ChatEvents.startEvent(eventType);
                    return true;
                }

            }

        }

        player.sendMessage("\n" + Colors.colorize("#5E91BEChat Events " + "&lList of Commands\n" +
                Colors.primary + "/ChatEvents Start [EventType] " + Colors.secondary + "Force a event to start\n" +
                Colors.primary + "/ChatEvents ForceWin <Player> " + Colors.secondary + "Forces the event to end and give the rewards to a specific Player\n" +
                Colors.primary + "/ChatEvents Status " + Colors.secondary + "Check the current Event Status\n" +
                Colors.primary + "/ChatEvents Stop " + Colors.secondary + "Forces a event to stop\n&a"));

        return true;
    }
}
