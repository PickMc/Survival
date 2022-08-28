package me.zxoir.smp.customclasses;

import lombok.Getter;
import me.zxoir.smp.SMP;
import me.zxoir.smp.managers.ConfigManager;
import me.zxoir.smp.runnables.ChatEventEndRunnable;
import me.zxoir.smp.runnables.ChatEventStartRunnable;
import me.zxoir.smp.utilities.Colors;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/14/2022
 */
public class ChatEvents {
    @Getter
    private static boolean isEventActive = false;
    @Getter
    private static EventType eventType;
    @Getter
    private static Question currentQuestion;
    @Getter
    private static String currentWord;
    @Getter
    private static Material currentMaterial;
    private static BukkitTask startTask;
    private static BukkitTask endTask;
    private static final List<Question> questions = new ArrayList<>();

    public static void init() {
        FileConfiguration config = SMP.getPlugin(SMP.class).getConfig();
        ConfigurationSection section = config.getConfigurationSection("ChatEvents");

        if (section != null) {

            for (String key : section.getKeys(false)) {
                String question = config.getString("ChatEvents." + key + ".Question");
                String answer = config.getString("ChatEvents." + key + ".Answer");

                questions.add(new Question(question, answer));
            }

        }
        startTask = new ChatEventStartRunnable().runTaskLater(SMP.getPlugin(SMP.class), ConfigManager.getChatEventInterval() * 20L);
    }

    public static void startEvent(EventType eventType) {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            endEvent(null);
            return;
        }

        if (isEventActive)
            endEvent(null);

        isEventActive = true;
        eventType = eventType == null ? getRandomEvent() : eventType;
        ChatEvents.eventType = eventType;

        switch (eventType) {

            case QUESTION -> {
                currentQuestion = getRandomQuestion();
                Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.primary + "First player to answer this Question wins a &lLucky Block")));
                Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.secondary + currentQuestion.question())));
            }

            case RANDOM_WORD -> {
                currentWord = getRandomWord();
                Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.primary + "First to type " + Colors.secondary + currentWord + Colors.primary + " wins a &lLucky Block")));
            }

            case JUMP ->
                    Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.primary + "First player to jump wins a &lLucky Block")));

            case SNEAK ->
                    Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.primary + "First player to sneak wins a &lLucky Block")));

            case SUICIDE ->
                    Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.primary + "First player to DIE wins TWO &lLucky Block's")));

            case CRAFT_ITEM -> {
                currentMaterial = getRandomMaterial();
                Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.primary + "First player to craft a " + Colors.secondary + currentMaterial.name() + Colors.primary + " wins a &lLucky Block")));
            }

            case PLACE_BLOCK ->
                    Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.primary + "First player to place a block wins a &lLucky Block")));

            case BREAK_BLOCK ->
                    Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.primary + "First player to break a block wins a &lLucky Block")));

            case RIDE_HORSE ->
                    Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.primary + "First player to ride a Horse wins a &lLucky Block")));

            case KILL_ENTITY ->
                    Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.primary + "First player to kill any entity wins a &lLucky Block")));
        }

        endTask = new ChatEventEndRunnable().runTaskLater(SMP.getPlugin(SMP.class), ConfigManager.getChatEventDuration() * 20L);
    }

    public static void endEvent(@Nullable Player player) {
        if (!isEventActive)
            return;

        isEventActive = false;

        if (startTask != null && !startTask.isCancelled())
            startTask.cancel();

        if (endTask != null && !endTask.isCancelled())
            endTask.cancel();

        if (player == null)
            Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.error + "No one has won the event")));
        else {
            Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(Colors.colorize("#5E91BE&lChat Events &r#414438» " + Colors.secondary + player.getName() + " " + Colors.primary + "has won the event")));

            Title title = Title.title(LegacyComponentSerializer.legacySection().deserialize(Colors.primary + "You have won the Event"), LegacyComponentSerializer.legacySection().deserialize(Colors.secondary + "You have received a " + ChatColor.BOLD + "Lucky Block"));
            player.showTitle(title);

            if (!eventType.equals(EventType.SUICIDE) && LuckyBlock.getLuckyBlock() != null) {
                if (!player.getInventory().addItem(LuckyBlock.getLuckyBlock()).isEmpty())
                    player.getWorld().dropItemNaturally(player.getLocation(), LuckyBlock.getLuckyBlock());
            }
        }

        startTask = new ChatEventStartRunnable().runTaskLater(SMP.getPlugin(SMP.class), ConfigManager.getChatEventInterval() * 20L);
    }

    private static Material getRandomMaterial() {
        return SMP.getCraftableItems().get(ThreadLocalRandom.current().nextInt(SMP.getCraftableItems().size()));
    }

    private static @NotNull String getRandomWord() {
        String alphabet = "123456abcxyzABCXYZ";
        StringBuilder word = new StringBuilder();
        for (int i = 1; i < ThreadLocalRandom.current().nextInt(10) + 1; i++) {
            word.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
        }

        return word.toString();
    }

    private static Question getRandomQuestion() {
        return questions.get(ThreadLocalRandom.current().nextInt(questions.size()));
    }

    private static EventType getRandomEvent() {
        return EventType.values()[ThreadLocalRandom.current().nextInt(EventType.values().length)];
    }

    public enum EventType {
        QUESTION,
        RANDOM_WORD,
        JUMP,
        SNEAK,
        SUICIDE,
        CRAFT_ITEM,
        PLACE_BLOCK,
        BREAK_BLOCK,
        RIDE_HORSE,
        KILL_ENTITY
    }
}
