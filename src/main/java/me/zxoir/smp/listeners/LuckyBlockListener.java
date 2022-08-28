package me.zxoir.smp.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.LuckyBlock;
import me.zxoir.smp.utilities.BasicUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/16/2022
 */
public class LuckyBlockListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockRemove(@NotNull BlockBurnEvent event) {
        if (LuckyBlock.getPlacedLuckyBlocks().containsKey(event.getBlock()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockRemove(@NotNull BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (LuckyBlock.getPlacedLuckyBlocks().containsKey(block)) {
                BlockData blockData = block.getState().getBlockData().clone();
                BasicUtilities.runTaskSync(() -> {
                    block.setType(LuckyBlock.getLuckyBlock().getType());
                    block.setBlockData(blockData);
                });
            }
        }
    }

    @EventHandler
    public void onBlockRemove(@NotNull EntityExplodeEvent event) {
        if (event.isCancelled())
            return;

        List<Block> unchecked = new ArrayList<>();

        for (Block block : event.blockList()) {
            if (LuckyBlock.getPlacedLuckyBlocks().containsKey(block))
                event.setCancelled(true);
            else {
                if (event.isCancelled())
                    block.breakNaturally();
                else
                    unchecked.add(block);
            }
        }

        if (event.isCancelled()) {
            for (Block block : unchecked) {
                block.breakNaturally();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockRemove(@NotNull BlockDestroyEvent event) {
        if (LuckyBlock.getPlacedLuckyBlocks().containsKey(event.getBlock()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockRemove(@NotNull BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (LuckyBlock.getPlacedLuckyBlocks().containsKey(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAnvilChange(@NotNull PrepareAnvilEvent event) {
        if ((event.getInventory().getFirstItem() != null && event.getInventory().getFirstItem().isSimilar(LuckyBlock.getLuckyBlock())) || (event.getInventory().getSecondItem() != null && event.getInventory().getSecondItem().isSimilar(LuckyBlock.getLuckyBlock()))) {
            event.setResult(null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockRemove(@NotNull BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (LuckyBlock.getPlacedLuckyBlocks().containsKey(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockRemove(@NotNull BlockFromToEvent event) {
        if (LuckyBlock.getPlacedLuckyBlocks().containsKey(event.getToBlock()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(@NotNull BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!event.getItemInHand().isSimilar(LuckyBlock.getLuckyBlock()))
            return;

        Location blockCenter = event.getBlockPlaced().getLocation().add(0.5, 0, 0.5).clone();

        if (event.getBlockPlaced().getType().equals(Material.PLAYER_WALL_HEAD))
            blockCenter.add(0, 0.5, 0);

        SMP mainInstance = SMP.getPlugin(SMP.class);

        BukkitTask task = new BukkitRunnable() {

            final BukkitTask cancel = Bukkit.getScheduler().runTaskLater(mainInstance, () -> {
                LuckyBlock.getPlacedLuckyBlocks().remove(event.getBlockPlaced());
                event.getBlockPlaced().setType(Material.AIR);

                LuckyBlock.activateLuckyBlock(null, LuckyBlock.getRandomLuckyBlockType(), event.getBlock().getLocation());
            }, 20 * 10L);

            @Override
            public void run() {
                if (!LuckyBlock.getPlacedLuckyBlocks().containsKey(event.getBlockPlaced())) {
                    cancel();
                    cancel.cancel();
                    return;
                }

                for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
                    double radius = Math.sin(i);
                    double y = Math.cos(i);
                    for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
                        double x = Math.cos(a) * radius;
                        double z = Math.sin(a) * radius;
                        blockCenter.add(x, y, z);
                        player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, blockCenter, 1);
                        blockCenter.subtract(x, y, z);
                    }
                }
            }

        }.runTaskTimerAsynchronously(mainInstance, 0, 25);

        LuckyBlock.getPlacedLuckyBlocks().put(event.getBlock(), task);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!LuckyBlock.getPlacedLuckyBlocks().containsKey(event.getBlock()))
            return;

        LuckyBlock.getPlacedLuckyBlocks().remove(event.getBlock());
        event.setDropItems(false);
        LuckyBlock.LuckyBlockType luckyBlockType = LuckyBlock.getRandomLuckyBlockType();

        if (player.isSneaking())
            luckyBlockType = LuckyBlock.LuckyBlockType.ANVIL;

        LuckyBlock.activateLuckyBlock(player, luckyBlockType, event.getBlock().getLocation());
    }

}
