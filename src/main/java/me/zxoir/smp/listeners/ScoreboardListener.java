package me.zxoir.smp.listeners;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.zxoir.smp.SMP;
import me.zxoir.smp.managers.ConfigManager;
import me.zxoir.smp.utilities.BasicUtilities;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */
public class ScoreboardListener implements Listener {
    @Getter
    private static final HashMap<Integer, String> lineIds = new HashMap<>();

    public static void init() {
        if (!lineIds.isEmpty())
            return;

        int lineNumber = ConfigManager.getScoreboard().size();

        for (String ignored : ConfigManager.getScoreboard()) {
            lineIds.put(lineNumber--, getRandomWord());
        }

    }

    private static @NotNull String getRandomWord() {
        String alphabet = "123xyzabcXYZABC";
        StringBuilder word = new StringBuilder();

        for (int i = 1; i < 5; i++) {
            word.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
        }

        if (lineIds.containsValue(word.toString()))
            return getRandomWord();

        return word.toString();
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        setScoreboard(player);
        BasicUtilities.runTaskAsync(() -> Bukkit.getOnlinePlayers().forEach(ScoreboardListener::updateScoreboard));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BasicUtilities.runTaskAsync(() -> Bukkit.getOnlinePlayers().forEach(ScoreboardListener::updateScoreboard));
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        BasicUtilities.runTaskAsync(() -> Bukkit.getOnlinePlayers().forEach(ScoreboardListener::updateScoreboard));
    }

    public static void updateScoreboard(@NotNull Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        int lineNumber = ConfigManager.getScoreboard().size();

        for (String line : ConfigManager.getScoreboard()) {
            if (!line.contains("%")) {
                lineNumber--;
                continue;
            }

            line = PlaceholderAPI.setPlaceholders(player, line);

            String teamName = lineIds.get(lineNumber--);

            Objects.requireNonNull(scoreboard.getTeam(teamName)).prefix(LegacyComponentSerializer.legacySection().deserialize(line));
        }
    }

    public static void setScoreboard(@NotNull Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = scoreboard.registerNewObjective("PickMC", Criteria.DUMMY, LegacyComponentSerializer.legacySection().deserialize(ConfigManager.getScoreboardName()));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        int lineNumber = ConfigManager.getScoreboard().size();

        for (String line : ConfigManager.getScoreboard()) {
            line = PlaceholderAPI.setPlaceholders(player, line);

            if (line.contains("%"))
                addLine(scoreboard, obj, line, lineNumber--);
            else
                addStaticLine(scoreboard, obj, line, lineNumber--);
        }

        player.setScoreboard(scoreboard);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                updateScoreboard(player);
            }

        }.runTaskTimerAsynchronously(SMP.getPlugin(SMP.class), 0, 20L * ConfigManager.getScoreboardRefresh());
    }

    private static void addLine(@NotNull Scoreboard scoreboard, @NotNull Objective obj, @NotNull String text, int line) {
        Team team = scoreboard.registerNewTeam(lineIds.get(line));
        String lineID = (ChatColor.WHITE + "" + ChatColor.WHITE).repeat(Math.max(1, line));
        team.addEntry(lineID);
        team.prefix(LegacyComponentSerializer.legacySection().deserialize(text));
        obj.getScore(lineID).setScore(line);
    }

    private static void addStaticLine(@NotNull Scoreboard scoreboard, @NotNull Objective obj, @NotNull String text, int line) {
        Team team = scoreboard.registerNewTeam(lineIds.get(line));
        String lineID = (ChatColor.WHITE + "" + ChatColor.WHITE).repeat(Math.max(1, line));
        team.addEntry(lineID);
        team.prefix(LegacyComponentSerializer.legacySection().deserialize(text));
        obj.getScore(lineID).setScore(line);
    }
}
