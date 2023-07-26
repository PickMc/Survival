package me.zxoir.smp.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.ConfigManager;
import me.zxoir.smp.managers.UserManager;
import me.zxoir.smp.utilities.BasicUtilities;
import me.zxoir.smp.utilities.Colors;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/17/2022
 */
public class GeneralListener implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.joinMessage(LegacyComponentSerializer.legacySection().deserialize(ConfigManager.getJoinMessage(player.getName())));
    }

    @EventHandler
    public void onJoin(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.quitMessage(LegacyComponentSerializer.legacySection().deserialize(ConfigManager.getQuitMessage(player.getName())));
    }

    /* Make Skeleton/Zombie Horses ridable */
    @EventHandler(ignoreCancelled = true)
    public void onInteract(@NotNull PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType().equals(EntityType.SKELETON_HORSE) || event.getRightClicked().getType().equals(EntityType.ZOMBIE_HORSE)) {
            Entity entity = event.getRightClicked();

            entity.addPassenger(event.getPlayer());
        }
    }

    /* Player Chat Mention */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMention(@NotNull AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        HashSet<Player> mentionedPlayers = new HashSet<>();
        for (String messageSplit : message.split(" ")) {
            Player player = Bukkit.getPlayer(messageSplit);
            if (player != null) {
                mentionedPlayers.add(player);
                message = message.replace(messageSplit, player.getName());
            }
        }

        if (mentionedPlayers.isEmpty())
            return;

        for (Player mentioned : mentionedPlayers) {
            mentioned.playSound(mentioned.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            String lastColor = ChatColor.getLastColors(StringUtils.substringBefore(message, mentioned.getName()));

            message = message.replace(mentioned.getName(), colorize("&a" + mentioned.getName() + (lastColor.isEmpty() ? "&f" : lastColor)));

            User user = UserManager.getUser(mentioned.getUniqueId());
            if (user.getCache().isAfk())
                BasicUtilities.runTaskSync(() -> event.getPlayer().sendMessage(Colors.secondary + mentioned.getName() + ChatColor.GRAY + " is AFK and might not respond"));
        }

        event.message(LegacyComponentSerializer.legacySection().deserialize(message));
    }

}
