package me.zxoir.smp.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 1/14/2022
 */
public class NoWeedCommand implements CommandExecutor {
    private final HashMap<UUID, BukkitTask> NoWeedList = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        Player player = (Player) sender;
        User user = UserManager.getUser(player.getUniqueId());

        if (user.getStats().getLevel().get() < 6) {
            player.sendMessage(colorize("&cYou must be level 6 or above to use this command!"));

            return true;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("on")) {

                if (NoWeedList.containsKey(player.getUniqueId())) {
                    player.sendMessage(colorize("&cNo Weed is already on!"));
                    return true;
                }

                player.sendMessage(colorize("&aNo Weed is on!"));
                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            this.cancel();
                            NoWeedList.remove(player.getUniqueId());
                            return;
                        }

                        if (!NoWeedList.containsKey(player.getUniqueId())) {
                            this.cancel();
                            NoWeedList.remove(player.getUniqueId());
                            return;
                        }

                        if (!user.getCache().isTracking()) {
                            player.sendActionBar(Component.text(colorize("&e&lNo Weed Enabled")));
                        }

                        RegionContainer container = SMP.getWorldGuard().getPlatform().getRegionContainer();
                        RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));

                        List<Block> blocks = getNearbyBlocks(player.getLocation());
                        if (blocks.isEmpty()) return;
                        for (Block block : blocks) {
                            if (block == null || block.getType().equals(Material.AIR) || !isWeed(block.getType()))
                                continue;

                            ApplicableRegionSet applicableRegionSet = null;
                            if (regions != null)
                                applicableRegionSet = regions.getApplicableRegions(BukkitAdapter.asBlockVector(block.getLocation()));
                            if (regions != null) {
                                if (applicableRegionSet.getRegions() != null && !applicableRegionSet.getRegions().isEmpty()) {
                                    boolean cont = false;
                                    for (ProtectedRegion region : applicableRegionSet.getRegions()) {
                                        if (region.getId().equalsIgnoreCase("protect"))
                                            cont = true;
                                    }

                                    if (cont) {
                                        Bukkit.getLogger().info("Detected PROTECTED REGION");
                                        continue;
                                    }
                                }
                            }

                            Bukkit.getScheduler().runTask(SMP.getInstance(), () -> block.breakNaturally());
                        }
                    }
                }.runTaskTimerAsynchronously(SMP.getInstance(), 0, 10);
                NoWeedList.put(player.getUniqueId(), task);
                return true;
            }

            if (args[0].equalsIgnoreCase("off")) {

                if (!NoWeedList.containsKey(player.getUniqueId())) {
                    player.sendMessage(colorize("&cNo Weed is not enabled!"));
                    return true;
                }

                NoWeedList.get(player.getUniqueId()).cancel();
                NoWeedList.remove(player.getUniqueId());
                player.sendMessage(colorize("&cNo Weed is off!"));
                return true;
            }

        }

        player.sendMessage(colorize("""

                &eYou have to make it &aON &eOr &cOFF&e.

                &7Ex: &e/NoWeed &aON
                &a"""));

        return true;
    }

    private boolean isWeed(Material material) {
        return material == Material.GRASS || material == Material.TALL_GRASS || material == Material.SEAGRASS || material == Material.TALL_SEAGRASS || material == Material.DEAD_BUSH || material == Material.FERN || material == Material.LARGE_FERN;
    }

    private @NotNull List<Block> getNearbyBlocks(@NotNull Location location) {
        List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - 5; x <= location.getBlockX() + 5; x++) {
            for (int y = location.getBlockY() - 5; y <= location.getBlockY() + 5; y++) {
                for (int z = location.getBlockZ() - 5; z <= location.getBlockZ() + 5; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
}