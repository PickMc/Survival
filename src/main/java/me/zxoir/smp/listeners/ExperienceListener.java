package me.zxoir.smp.listeners;

import io.papermc.paper.advancement.AdvancementDisplay;
import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static me.zxoir.smp.utilities.BasicUtilities.runTaskAsync;
import static me.zxoir.smp.utilities.BasicUtilities.runTaskSync;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */
public class ExperienceListener implements Listener {
    private final ConcurrentHashMap<Block, BlockData> formedBlocks = new ConcurrentHashMap<>();

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = UserManager.getUser(player.getUniqueId());

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (UserManager.getTotalPlaytime(user).toDays() > user.getCache().getPlaytimeDaysRewarded()) {
                    user.getCache().setPlaytimeDaysRewarded(user.getCache().getPlaytimeDaysRewarded() + 1);
                    user.getStats().addXp(300, 1000);
                }

            }

        }.runTaskTimerAsynchronously(SMP.getPlugin(SMP.class), 0, 20L * 60);
    }

    @EventHandler
    public void onExplosion(@NotNull BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (isFormed(block))
                formedBlocks.remove(event.getBlock());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();
        boolean isRareBlock = SMP.getRareItems().contains(event.getBlock().getType());

        if (!event.getBlock().isBuildable())
            return;

        User user = UserManager.getUser(player.getUniqueId());

        if (user.getCache().isAfk())
            return;

        if (isFormed(event.getBlock())) {
            formedBlocks.remove(event.getBlock());
            return;
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(SMP.getPlugin(SMP.class), () -> {
            if (isPlacedByPlayer(event.getBlock()))
                return;

            if (user.getCache().getCanBreakBlock() != null && Instant.now().isBefore(user.getCache().getCanBreakBlock()) && !isRareBlock)
                return;

            BigDecimal xpGain = isRareBlock ? user.getStats().addXp(10, 20) : user.getStats().addXp(0, 6);

            int randomDelay = ThreadLocalRandom.current().nextInt(0, 6);
            user.getCache().setCanBreakBlock(Instant.now().plusSeconds(randomDelay));

            runTaskSync(() -> player.sendMessage("Can break again in " + randomDelay + " seconds"));
        }, 20);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAdvancement(@NotNull PlayerAdvancementDoneEvent event) {
        AdvancementDisplay advancementDisplay = event.getAdvancement().getDisplay();
        if (advancementDisplay != null && advancementDisplay.doesAnnounceToChat()) {
            User user = UserManager.getUser(event.getPlayer().getUniqueId());

            if (user.getCache().getCanAdvancement() != null && Instant.now().isBefore(user.getCache().getCanAdvancement()))
                return;

            user.getStats().addXp(200, 500);

            int randomDelay = ThreadLocalRandom.current().nextInt(30, 60);
            user.getCache().setCanAdvancement(Instant.now().plusSeconds(randomDelay));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityKill(@NotNull EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null || !killer.getType().equals(EntityType.PLAYER) || entity.getType().equals(EntityType.PLAYER))
            return;

        User user = UserManager.getUser(killer.getUniqueId());
        if (user.getCache().getCanKillEntity() != null && Instant.now().isBefore(user.getCache().getCanKillEntity())) {
            runTaskSync(() -> killer.sendMessage("Can kill again in " + Duration.between(Instant.now(), user.getCache().getCanKillEntity()).getSeconds() + " seconds"));
            return;
        }

        BigDecimal xpGain = user.getStats().addXp(0, 11);

        int randomDelay = ThreadLocalRandom.current().nextInt(0, 6);
        user.getCache().setCanKillEntity(Instant.now().plusSeconds(randomDelay));
        runTaskSync(() -> killer.sendMessage("Can kill again in " + randomDelay + " seconds"));
    }

    /* Called when a block is formed */
    @EventHandler(ignoreCancelled = true)
    public void onFormedBlock(@NotNull BlockFormEvent event) {
        runTaskAsync(() -> {
            if ((event.getNewState().isPlaced() && !event.getNewState().getBlock().isBuildable()) || event.getNewState().getType().equals(Material.OBSIDIAN))
                return;

            formedBlocks.put(event.getBlock(), event.getNewState().getBlockData());
        });
    }

    /* Called when a block is formed by an entity */
    @EventHandler(ignoreCancelled = true)
    public void onFormedBlock(@NotNull EntityBlockFormEvent event) {
        runTaskAsync(() -> {
            if ((event.getNewState().isPlaced() && !event.getNewState().getBlock().isBuildable()) || event.getNewState().getType().equals(Material.OBSIDIAN))
                return;

            formedBlocks.put(event.getBlock(), event.getNewState().getBlockData());
        });
    }

    /* Called when a block is spawned from a Dispenser */
    @EventHandler(ignoreCancelled = true)
    public void onBlockSpawn(@NotNull BlockDispenseEvent event) {
        BlockFace blockFace = ((Directional) event.getBlock().getBlockData()).getFacing();
        Block blockInFront = getBlockInFront(event.getBlock(), blockFace);

        runTaskSync(() -> {
            if (blockInFront.getBlockData().matches(event.getItem().getType().createBlockData()) && blockInFront.isBuildable()) {
                formedBlocks.put(blockInFront, blockInFront.getBlockData());
            }
        });
    }

    /* Called when a block is moved by a piston */
    @EventHandler(ignoreCancelled = true)
    public void onBlockMove(@NotNull BlockPistonExtendEvent event) {
        BlockFace blockFace = ((Directional) event.getBlock().getBlockData()).getFacing();

        if (event.getBlocks().isEmpty())
            return;

        for (Block block : event.getBlocks()) {
            if (formedBlocks.containsKey(block) && formedBlocks.get(block).matches(block.getBlockData())) {
                Block blockAgainst = getBlockInFront(block, blockFace);
                formedBlocks.remove(block);
                formedBlocks.put(blockAgainst, block.getBlockData());
            }
        }
    }

    /* Called when a block is moved by a piston */
    @EventHandler(ignoreCancelled = true)
    public void onBlockMove(@NotNull BlockPistonRetractEvent event) {
        BlockFace blockFace = ((Directional) event.getBlock().getBlockData()).getFacing().getOppositeFace();

        if (event.getBlocks().isEmpty())
            return;

        for (Block block : event.getBlocks()) {
            if (formedBlocks.containsKey(block) && formedBlocks.get(block).matches(block.getBlockData())) {
                Block blockAgainst = getBlockInFront(block, blockFace);
                formedBlocks.remove(block);
                formedBlocks.put(blockAgainst, block.getBlockData());
            }
        }
    }

    private boolean isFormed(Block block) {

        if (formedBlocks.containsKey(block)) {

            if (formedBlocks.get(block).matches(block.getBlockData())) {
                formedBlocks.remove(block);
                return true;
            }

            formedBlocks.remove(block);
        }

        return false;
    }

    private boolean isPlacedByPlayer(Block block) {
        List<String[]> blockLookup = SMP.getCoreProtectAPI().blockLookup(block, (int) (System.currentTimeMillis() / 1000L));

        if (blockLookup == null)
            return false;

        for (String[] result : blockLookup) {
            if (result == null)
                continue;

            CoreProtectAPI.ParseResult parseResult = SMP.getCoreProtectAPI().parseResult(result);
            if (parseResult == null)
                return false;

            if (parseResult.getActionId() == 1)
                return true;
        }

        return false;
    }

    private @NotNull Block getBlockInFront(@NotNull Block block, @NotNull BlockFace face) {
        Location spawnedBlock = block.getLocation();

        switch (face) {
            case NORTH -> spawnedBlock.setZ(spawnedBlock.getZ() - 1);
            case EAST -> spawnedBlock.setX(spawnedBlock.getX() + 1);
            case SOUTH -> spawnedBlock.setZ(spawnedBlock.getZ() + 1);
            case WEST -> spawnedBlock.setX(spawnedBlock.getX() - 1);
            case UP -> spawnedBlock.setY(spawnedBlock.getY() + 1);
            case DOWN -> spawnedBlock.setY(spawnedBlock.getY() - 1);
        }

        return spawnedBlock.getBlock();
    }
}
