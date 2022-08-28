package me.zxoir.smp.listeners;


import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.zxoir.smp.customclasses.ChatEvents;
import me.zxoir.smp.customclasses.LuckyBlock;
import me.zxoir.smp.customclasses.Question;
import me.zxoir.smp.utilities.BasicUtilities;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityMountEvent;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/14/2022
 */
public class ChatEventsListener implements Listener {

    @EventHandler
    public void onChat(@NotNull AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = LegacyComponentSerializer.legacySection().serialize(event.message());

        if (ChatEvents.isEventActive()) {

            if (ChatEvents.getEventType().equals(ChatEvents.EventType.QUESTION)) {
                Question question = ChatEvents.getCurrentQuestion();

                if (message.toUpperCase().matches(".*\\b" + question.answer().toUpperCase() + "\\b.*"))
                    BasicUtilities.runTaskSync(() -> ChatEvents.endEvent(player));
            }

            if (ChatEvents.getEventType().equals(ChatEvents.EventType.RANDOM_WORD)) {
                if (message.equals(ChatEvents.getCurrentWord()))
                    BasicUtilities.runTaskSync(() -> ChatEvents.endEvent(player));
            }

        }
    }

    @EventHandler
    public void onJump(@NotNull PlayerJumpEvent event) {
        Player player = event.getPlayer();

        if (ChatEvents.isEventActive() && ChatEvents.getEventType().equals(ChatEvents.EventType.JUMP))
            ChatEvents.endEvent(player);
    }

    @EventHandler
    public void onSneak(@NotNull PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (ChatEvents.isEventActive() && ChatEvents.getEventType().equals(ChatEvents.EventType.SNEAK))
            ChatEvents.endEvent(player);
    }

    @EventHandler
    public void onDeath(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (ChatEvents.isEventActive() && ChatEvents.getEventType().equals(ChatEvents.EventType.SUICIDE)) {
            ChatEvents.endEvent(player);
            if (LuckyBlock.getLuckyBlock() == null)
                return;
            BasicUtilities.runTaskSync(() -> {
                if (!player.getInventory().addItem(LuckyBlock.getLuckyBlock()).isEmpty()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), LuckyBlock.getLuckyBlock());
                }

                if (!player.getInventory().addItem(LuckyBlock.getLuckyBlock()).isEmpty()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), LuckyBlock.getLuckyBlock());
                }
            });
        }
    }

    @EventHandler
    public void onCraft(@NotNull CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (ChatEvents.isEventActive() && ChatEvents.getEventType().equals(ChatEvents.EventType.CRAFT_ITEM) && event.getRecipe().getResult().getType().equals(ChatEvents.getCurrentMaterial()))
            ChatEvents.endEvent(player);
    }

    @EventHandler
    public void onPlace(@NotNull BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (ChatEvents.isEventActive() && ChatEvents.getEventType().equals(ChatEvents.EventType.PLACE_BLOCK))
            ChatEvents.endEvent(player);
    }

    @EventHandler
    public void onBreak(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (ChatEvents.isEventActive() && ChatEvents.getEventType().equals(ChatEvents.EventType.BREAK_BLOCK))
            ChatEvents.endEvent(player);
    }

    @EventHandler
    public void onRideHorse(@NotNull EntityMountEvent event) {
        if (ChatEvents.isEventActive() && ChatEvents.getEventType().equals(ChatEvents.EventType.RIDE_HORSE) && event.getEntityType().equals(EntityType.PLAYER) && (event.getMount().getType().equals(EntityType.HORSE) || event.getMount().getType().equals(EntityType.SKELETON_HORSE) || event.getMount().getType().equals(EntityType.ZOMBIE_HORSE)))
            ChatEvents.endEvent((Player) event.getEntity());
    }

    @EventHandler
    public void onKillEntity(@NotNull EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null || !killer.getType().equals(EntityType.PLAYER) || entity.getType().equals(EntityType.PLAYER))
            return;

        if (ChatEvents.isEventActive() && ChatEvents.getEventType().equals(ChatEvents.EventType.KILL_ENTITY))
            ChatEvents.endEvent(killer);
    }

}
