package me.zxoir.smp.commands;

import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import me.zxoir.smp.utilities.DefaultFontInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/23/2023
 */
public class FriendCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        Player player = (Player) sender;
        User user = UserManager.getUser(player.getUniqueId());

        if (user.getStats().getLevel().get() < 2) {
            player.sendMessage(colorize("&cYou must be level 2 or above to use this command!"));

            return true;
        }

        // add, accept, delete, requests, deny, track, untrack, list
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                User targetUser = UserManager.getUser(target.getUniqueId());

                if (targetUser == null) {
                    player.sendMessage(colorize("&cThat player has never played before!"));
                    return true;
                }

                if (user.getCache().getFriends().contains(target.getUniqueId())) {
                    player.sendMessage(colorize("&cYou are already friends with this player."));
                    return true;
                }

                if (targetUser.getStats().getLevel().get() < 2) {
                    player.sendMessage(colorize("&c&l" + target.getName() + " &cmust be level 2 or more!"));
                    return true;
                }

                if (target.isOnline() && target.getPlayer() != null) {
                    targetUser.getCache().getFriendRequests().add(player.getUniqueId());
                    Component component = Component.text(colorize("&eFriend request received from &9" + player.getName() + " "));
                    Component accept = Component.text(colorize("&a&l[ACCEPT] ")).clickEvent(ClickEvent.runCommand("/friend accept " + player.getName())).hoverEvent(HoverEvent.showText(Component.text(colorize("&aClick to accept"))));
                    Component deny = Component.text(colorize("&c&l[DENY] ")).clickEvent(ClickEvent.runCommand("/friend deny " + player.getName())).hoverEvent(HoverEvent.showText(Component.text(colorize("&cClick to decline"))));
                    component = component.append(accept).append(deny);
                    target.getPlayer().sendMessage(component);
                    player.sendMessage(colorize("&aA friend request has been sent to &b" + target.getName()));
                } else
                    player.sendMessage("That player is not online!");

                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                User targetUser = UserManager.getUser(target.getUniqueId());

                if (targetUser == null) {
                    player.sendMessage(colorize("&cThat player has never played before!"));
                    return true;
                }

                if (!user.getCache().getFriends().contains(target.getUniqueId())) {
                    player.sendMessage(colorize("&cThat player isn't your friend."));
                    return true;
                }

                if (user.getCache().isTracking()) {
                    for (UUID trackedFriend : user.getCache().getFriends()) {
                        if (user.getCache().getTrackedFriend() != null && user.getCache().getTrackedFriend().getUniqueId().equals(trackedFriend)) {
                            user.getCache().untrackFriend();
                        }
                    }

                    if (targetUser.getCache().getTrackedFriend() != null && targetUser.getCache().getTrackedFriend().getUniqueId().equals(player.getUniqueId())) {
                        targetUser.getCache().untrackFriend();
                    }
                }

                user.getCache().getFriends().remove(target.getUniqueId());
                targetUser.getCache().getFriends().remove(player.getUniqueId());
                player.sendMessage(colorize("&cYou are no longer friends with " + target.getName()));

                return true;
            }

            if (args[0].equalsIgnoreCase("accept")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                User targetUser = UserManager.getUser(target.getUniqueId());

                if (targetUser == null) {
                    player.sendMessage(colorize("&cThat player has never played before!"));
                    return true;
                }

                if (user.getCache().getFriends().contains(target.getUniqueId())) {
                    player.sendMessage(colorize("&cYou are already friends with this player."));
                    return true;
                }

                if (!user.getCache().getFriendRequests().contains(target.getUniqueId())) {
                    player.sendMessage(colorize("&cThat player didn't send you a friend request."));
                    return true;
                }

                user.getCache().getFriendRequests().remove(target.getUniqueId());
                user.getCache().getFriends().add(target.getUniqueId());
                targetUser.getCache().getFriends().add(player.getUniqueId());
                player.sendMessage(colorize("&aYou have accepted " + target.getName() + "'s friend request."));

                if (target.isOnline() && target.getPlayer() != null)
                    target.getPlayer().sendMessage(colorize("&9" + player.getName() + " &ehas accepted your friend request."));

                return true;
            }

            if (args[0].equalsIgnoreCase("deny")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                User targetUser = UserManager.getUser(target.getUniqueId());

                if (targetUser == null) {
                    player.sendMessage(colorize("&cThat player has never played before!"));
                    return true;
                }

                if (!user.getCache().getFriendRequests().contains(target.getUniqueId())) {
                    player.sendMessage(colorize("&cThat player didn't send you a friend request."));
                    return true;
                }

                user.getCache().getFriendRequests().remove(target.getUniqueId());
                player.sendMessage(colorize("&cYou have denied " + target.getName() + "'s friend request."));

                if (target.isOnline() && target.getPlayer() != null)
                    target.getPlayer().sendMessage(colorize("&8" + player.getName() + " &7has denied your friend request."));

                return true;
            }

            if (args[0].equalsIgnoreCase("track")) {
                if (user.getCache().isTracking()) {
                    player.sendMessage(colorize("&cYou are already tracking something."));
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                User targetUser = UserManager.getUser(target.getUniqueId());

                if (targetUser == null) {
                    player.sendMessage(colorize("&cThat player has never played before!"));
                    return true;
                }

                if (!user.getCache().getFriends().contains(target.getUniqueId())) {
                    player.sendMessage(colorize("&cThat player isn't your friend."));
                    return true;
                }

                if (!target.isOnline() || target.getPlayer() == null) {
                    player.sendMessage(colorize("&cThat player is not online."));
                    return true;
                }

                if (!player.getWorld().getName().equals(target.getPlayer().getWorld().getName())) {
                    player.sendMessage(colorize("&cYou must be in the same world as the " + target.getName()));
                    return true;
                }

                user.getCache().trackFriend(target.getPlayer());

                return true;
            }
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("requests")) {

                if (user.getCache().getFriendRequests().isEmpty()) {
                    player.sendMessage(colorize("&cYou have no friend requests."));
                    return true;
                }

                Component message = Component.text(colorize("\n&e&lHere are a list of your friend requests:\n")).asComponent();

                for (UUID request : user.getCache().getFriendRequests()) {
                    OfflinePlayer requestPlayer = Bukkit.getOfflinePlayer(request);

                    Component component = Component.text(colorize("&8• &e" + requestPlayer.getName() + " &7- "));
                    Component accept = Component.text(colorize("&a&l[ACCEPT] "))
                            .clickEvent(ClickEvent.runCommand("/friend accept " + requestPlayer.getName()))
                            .hoverEvent(HoverEvent.showText(Component.text(colorize("&aClick to accept"))));
                    Component deny = Component.text(colorize("&c&l[DENY] \n"))
                            .clickEvent(ClickEvent.runCommand("/friend deny " + requestPlayer.getName()))
                            .hoverEvent(HoverEvent.showText(Component.text(colorize("&cClick to decline"))));
                    component = component.append(accept).append(deny);
                    message = message.append(component);
                }

                player.sendMessage(message);
                return true;
            }

            if (args[0].equalsIgnoreCase("untrack")) {
                if (!user.getCache().isTracking()) {
                    player.sendMessage(Component.text(colorize("&cYou're not tracking a friend.")));
                    return true;
                }

                user.getCache().untrackFriend();
                player.sendMessage(Component.text(colorize("&aYou are no longer tracking a friend")));
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (user.getCache().getFriends().isEmpty()) {
                    player.sendMessage(colorize("&cYou have no friends =c"));
                    return true;
                }

                List<String> friends = new ArrayList<>();
                user.getCache().getFriends().forEach(uuid -> friends.add(Bukkit.getOfflinePlayer(uuid).isOnline() ? colorize("&a" + Bukkit.getOfflinePlayer(uuid).getName()) : colorize("&c" + Bukkit.getOfflinePlayer(uuid).getName())));

                player.sendMessage(colorize("&eYour friends are: &9" + StringUtils.join(friends, ",")));
                return true;
            }
        }

        player.sendMessage(colorize("&a\n" +
                centerMessage("&e&lFriends Commands") +
                "\n&a\n" +
                "&8• &e/Friend Add &c(Name) &7- &b Add a friend\n" +
                "&8• &e/Friend Remove &c(Name) &7- &b Remove a friend\n" +
                "&8• &e/Friend Accept &c(Name) &7- &b Accept a friend request\n" +
                "&8• &e/Friend Deny &c(Name) &7- &b Deny a friend request\n" +
                "&8• &e/Friend Track &c(Name) &7- &b Track a friend\n" +
                "&8• &e/Friend Untrack &7- &b Untrack a friend\n" +
                "&8• &e/Friend List &7- &b List of all your friends\n" +
                "&8• &e/Friend Requests &7- &b List of all your friend requests\n"));

        return true;
    }

    private final static int CENTER_PX = 154;

    public static @NotNull String centerMessage(String message) {
        if (message == null || message.equals("")) return "";
        else
            message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }
}
