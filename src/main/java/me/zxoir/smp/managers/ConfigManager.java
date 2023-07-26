package me.zxoir.smp.managers;

import lombok.Getter;
import me.zxoir.smp.SMP;
import me.zxoir.smp.utilities.Colors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final SMP main = SMP.getPlugin(SMP.class);

    @Getter
    private static String username;

    @Getter
    private static String database;

    @Getter
    private static String password;

    @Getter
    private static String ip;

    @Getter
    private static String port;

    @Getter
    private static boolean allowEndPortal;

    private static String joinMessage;

    private static String quitMessage;

    @Getter
    private static String primaryColor;

    @Getter
    private static String secondaryColor;

    @Getter
    private static String errorColor;

    private static String voteBroadcast;

    @Getter
    private static String voteReceivedMessage;

    @Getter
    private static String voteInventoryFullMessage;

    @Getter
    private static String voteReceivedOfflineMessage;

    @Getter
    private static boolean EnableScoreboard;

    private static String scoreboardName;

    @Getter
    private static int scoreboardRefresh;

    private static List<String> scoreboard;

    @Getter
    private static int chatEventInterval;

    @Getter
    private static int chatEventDuration;


    private static void getConfigData() {
        username = main.getConfig().getString("username");
        database = main.getConfig().getString("database");
        password = main.getConfig().getString("password");
        ip = main.getConfig().getString("ip");
        port = main.getConfig().getString("port");
        allowEndPortal = main.getConfig().getBoolean("AllowEndPortal");
        primaryColor = main.getConfig().getString("PrimaryColor");
        secondaryColor = main.getConfig().getString("SecondaryColor");
        errorColor = main.getConfig().getString("ErrorColor");
        joinMessage = replaceCommonPlaceholders(main.getConfig().getString("JoinMessage"));
        quitMessage = replaceCommonPlaceholders(main.getConfig().getString("QuitMessage"));
        voteBroadcast = replaceCommonPlaceholders(main.getConfig().getString("VoteBroadcast"));
        voteReceivedMessage = replaceCommonPlaceholders(main.getConfig().getString("VoteReceivedMessage"));
        voteInventoryFullMessage = replaceCommonPlaceholders(main.getConfig().getString("VoteInventoryFullMessage"));
        voteReceivedOfflineMessage = replaceCommonPlaceholders(main.getConfig().getString("VoteReceivedOfflineMessage"));
        EnableScoreboard = main.getConfig().getBoolean("Enable-Scoreboard");
        scoreboardName = Colors.colorize(main.getConfig().getString("ScoreboardName"));
        scoreboardRefresh = main.getConfig().getInt("ScoreboardRefresh");
        scoreboard = main.getConfig().getStringList("Scoreboard");
        chatEventInterval = main.getConfig().getInt("ChatEventInterval");
        chatEventDuration = main.getConfig().getInt("ChatEventDuration");
    }

    public static void setup() {
        main.saveDefaultConfig();
        getConfigData();
    }

    public static void reloadConfig() {
        main.reloadConfig();
        SMP.getDataFile().reloadConfig();
        getConfigData();
    }

    public static @NotNull String getJoinMessage(String playerName) {
        return joinMessage.replace("%player_name%", playerName);
    }

    public static @NotNull String getQuitMessage(String playerName) {
        return quitMessage.replace("%player_name%", playerName);
    }

    public static @NotNull String getScoreboardName() {
        return replaceCommonPlaceholders(scoreboardName);
    }

    @Contract(pure = true)
    public static @NotNull String getVoteBroadcast(String playerName) {
        return voteBroadcast.replace("%player_name%", playerName);
    }

    public static @NotNull List<String> getScoreboard() {
        List<String> finalScoreboard = new ArrayList<>();
        scoreboard.forEach(line -> finalScoreboard.add(replaceCommonPlaceholders(line)));
        return finalScoreboard;
    }

    private static @NotNull String replaceCommonPlaceholders(String message) {
        if (message == null)
            return "";

        return Colors.colorize(message).replace("%primary_color%", Colors.primary).replace("%secondary_color%", Colors.secondary).replace("%error_color%", Colors.error);
    }
}
