package me.zxoir.smp.commands;

import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/23/2023
 */
public class CheckpointCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;
        Player player = (Player) sender;
        User user = UserManager.getUser(player.getUniqueId());

        if (user.getStats().getLevel().get() < 3) {
            player.sendMessage(colorize("&cYou must be level 3 or above to use this command!"));

            return true;
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("create")) {

                if (user.getCache().getCheckpoints().containsKey(args[1].toLowerCase())) {
                    player.sendMessage(Component.text(colorize("&cA checkpoint with this name already exists!")));
                    return true;
                }

                user.getCache().createCheckpoint(args[1], player.getLocation());
                player.sendMessage(Component.text(colorize("&aCheckpoint created!")));

                return true;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                if (!user.getCache().getCheckpoints().containsKey(args[1].toLowerCase())) {
                    player.sendMessage(Component.text(colorize("&cThis checkpoint doesn't exist.")));
                    return true;
                }

                user.getCache().deleteCheckpoint(args[1]);
                player.sendMessage(Component.text(colorize("&aCheckpoint removed.")));
                return true;
            }

            if (args[0].equalsIgnoreCase("track")) {

                if (!user.getCache().getCheckpoints().containsKey(args[1].toLowerCase())) {
                    player.sendMessage(Component.text(colorize("&cThat checkpoint doesn't exist!")));
                    return true;
                }

                if (user.getCache().isTracking()) {
                    player.sendMessage(Component.text(colorize("&cYou are already tracking something.")));
                    return true;
                }

                Location checkpoint = user.getCache().getCheckpoints().get(args[1].toLowerCase());

                if (checkpoint == null || checkpoint.getWorld() == null) {
                    player.sendMessage(colorize("&cCheckpoint location has been corrupted. Please try creating another one!"));
                    return true;
                }

                if (!checkpoint.getWorld().getName().equals(player.getWorld().getName())) {
                    player.sendMessage(colorize("&cYou must be in the same world as the Checkpoint"));
                    return true;
                }

                user.getCache().trackCheckpoint(checkpoint);

                return true;
            }

        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("untrack")) {
                if (!user.getCache().isTracking()) {
                    player.sendMessage(Component.text(colorize("&cYou're not tracking a checkpoint.")));
                    return true;
                }

                user.getCache().untrackCheckpoint();
                player.sendMessage(Component.text(colorize("&aYou are no longer tracking a checkpoint")));
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (user.getCache().getCheckpoints().isEmpty()) {
                    player.sendMessage(colorize("&cYou have no checkpoints =c"));
                    return true;
                }

                Component component = Component.text(colorize("\n&e&lHere are a list of your checkpoints:\n")).asComponent();
                for (String checkpoint : user.getCache().getCheckpoints().keySet()) {
                    Location checkpointLocation = user.getCache().getCheckpoints().get(checkpoint);
                    String loc = String.format("%.2f", checkpointLocation.getX()) + ", " + checkpointLocation.getY() + ", " + String.format("%.2f", checkpointLocation.getZ());
                    Component point = Component.text(colorize("&8• &c" + checkpoint + " &7- &b(Click to track)\n"))
                            .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/checkpoint track " + checkpoint))
                            .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text(colorize("&bClick to track " + checkpoint + "\n&8Location: &7" + loc))));
                    component = component.append(point);
                }

                player.sendMessage(component);
                return true;
            }
        }

        player.sendMessage(colorize("\n" +
                FriendCommand.centerMessage("&e&lCheckpoint Commands:") +
                "\n&a\n" +
                "&8• &e/Checkpoint Create &c(Name) &7- &bCreate a new checkpoint\n" +
                "&8• &e/Checkpoint Delete &c(Name) &7- &bDelete a checkpoint\n" +
                "&8• &e/Checkpoint Track &c(Name) &7- &bTrack a checkpoint\n" +
                "&8• &e/Checkpoint Untrack &7- &bUntrack a checkpoint\n" +
                "&8• &e/Checkpoint list &7- &bList of all your checkpoints\n"));

        return true;
    }

}
