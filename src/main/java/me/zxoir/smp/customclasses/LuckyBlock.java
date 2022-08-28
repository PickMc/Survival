package me.zxoir.smp.customclasses;

import lombok.Getter;
import lombok.Setter;
import me.zxoir.smp.SMP;
import me.zxoir.smp.managers.UserManager;
import me.zxoir.smp.utilities.BasicUtilities;
import me.zxoir.smp.utilities.Colors;
import me.zxoir.smp.utilities.RandomCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/16/2022
 */

public class LuckyBlock {
    @Setter
    private static ItemStack luckyBlock;
    @Getter
    private static final ConcurrentHashMap<Block, BukkitTask> placedLuckyBlocks = new ConcurrentHashMap<>();
    @Getter
    private static final RandomCollection<ItemStack> randomItems = new RandomCollection<>();
    @Getter
    private static final RandomCollection<EntityType> randomEntities = new RandomCollection<>();

    public static void init() {
        randomEntities.add(30, EntityType.HUSK);
        randomEntities.add(30, EntityType.SKELETON);
        randomEntities.add(20, EntityType.CAVE_SPIDER);
        randomEntities.add(10, EntityType.CREEPER);
        randomEntities.add(10, EntityType.WITCH);
        randomEntities.add(5, EntityType.BLAZE);
        randomEntities.add(5, EntityType.SLIME);
        randomEntities.add(1, EntityType.GHAST);
        randomEntities.add(0.1, EntityType.WARDEN);

        randomEntities.add(30, EntityType.COW);
        randomEntities.add(30, EntityType.SHEEP);
        randomEntities.add(30, EntityType.PIG);
        randomEntities.add(30, EntityType.CHICKEN);
        randomEntities.add(20, EntityType.HORSE);
        randomEntities.add(20, EntityType.GOAT);
        randomEntities.add(20, EntityType.DONKEY);
        randomEntities.add(1, EntityType.SKELETON_HORSE);
        randomEntities.add(1, EntityType.ZOMBIE_HORSE);
    }

    private static boolean needPlayer(@NotNull LuckyBlockType luckyBlockType) {
        return luckyBlockType.equals(LuckyBlockType.CAGE) || luckyBlockType.equals(LuckyBlockType.EXPERIENCE) || luckyBlockType.equals(LuckyBlockType.POTION_EFFECT) || luckyBlockType.equals(LuckyBlockType.ANVIL) || luckyBlockType.equals(LuckyBlockType.EXPLOSION);
    }


    public static void activateLuckyBlock(@Nullable Player player, @NotNull LuckyBlockType luckyBlockType, @NotNull Location location) {
        if (player == null && needPlayer(luckyBlockType)) {
            boolean requirePlayer = true;
            while (requirePlayer) {
                luckyBlockType = getRandomLuckyBlockType();
                requirePlayer = needPlayer(luckyBlockType);
            }
        }

        switch (luckyBlockType) {
            case ITEM -> {
                if (!LuckyBlock.getRandomItems().isEmpty()) {
                    ItemStack itemStack = LuckyBlock.getRandomItems().next();
                    location.getWorld().dropItemNaturally(location, itemStack);
                } else activateLuckyBlock(player, getRandomLuckyBlockType(), location);
            }

            case SPAWN_ENTITY -> {
                EntityType entityType = LuckyBlock.getRandomEntities().next();
                boolean spawnAround = entityType.equals(EntityType.HUSK) || entityType.equals(EntityType.SKELETON) || entityType.equals(EntityType.COW) || entityType.equals(EntityType.SHEEP) || entityType.equals(EntityType.PIG) || entityType.equals(EntityType.CHICKEN);
                boolean spawnTwo = entityType.equals(EntityType.CAVE_SPIDER) || entityType.equals(EntityType.BLAZE) || entityType.equals(EntityType.WITCH);
                Location loc = player == null ? location : player.getLocation();

                if (spawnAround) {
                    List<Block> blocks = getBlocks(loc.getBlock(), 1);
                    boolean skip = false;
                    for (Block block : blocks) {

                        if (skip) {
                            skip = false;
                            continue;
                        }

                        if (!block.getType().equals(Material.AIR) && block.getLocation().add(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
                            block.getWorld().spawnEntity(block.getLocation().add(0, 1, 0), entityType);
                        }

                        skip = true;
                    }
                } else if (spawnTwo) {
                    loc.getWorld().spawnEntity(loc.add(1, 0, 0), entityType);
                    loc.getWorld().spawnEntity(loc.add(0, 0, 1), entityType);
                } else
                    loc.getWorld().spawnEntity(loc, entityType);
            }

            case TREE -> spawnTree(location, 1);

            case CAGE -> {
                buildIronCageAround(player);
                player.getLocation().add(0, 1, 0).getBlock().setType(Material.WATER);
            }

            case EXPERIENCE -> {
                User user = UserManager.getUser(player.getUniqueId());
                BigDecimal xp = user.getStats().addXp(1, 200);
                player.sendMessage(Colors.colorize("#E5FF65&lLuckyBlock &r#414438» " + Colors.primary + "You have received " + Colors.secondary + xp.intValue() + Colors.primary + " XP"));
            }

            case EXPLOSION -> {
                Entity primedTnt = location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                ((TNTPrimed) primedTnt).setFuseTicks(20);
                ArmorStand hologram = createTntTimerStand(primedTnt.getLocation());
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        double timer = Double.parseDouble(PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(hologram.customName())));
                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(1);

                        if (timer <= 0) {
                            BasicUtilities.runTaskSync(hologram::remove);
                            this.cancel();
                            return;
                        }

                        hologram.customName(Component.text(df.format(timer - 0.1) + ""));
                    }

                }.runTaskTimerAsynchronously(SMP.getPlugin(SMP.class), 0, 2);
            }

            case ANVIL -> {
                Location drop = player.getLocation().add(0, 15, 0);

                if (!drop.getBlock().getType().equals(Material.AIR)) {
                    player.sendMessage(Colors.colorize("#E5FF65&lLuckyBlock &r#414438» " + Colors.primary + "A block above you saved you from a falling Anvil"));
                    return;
                }

                drop.getBlock().setType(Material.DAMAGED_ANVIL);

                //player.getLocation().add(0, 15, 0).getBlock().setType(Material.ANVIL);
                player.sendMessage(Colors.colorize("#E5FF65&lLuckyBlock &r#414438» " + Colors.primary + "Look up..."));
            }
        }
    }

    @NotNull
    private static ArmorStand createTntTimerStand(@NotNull Location location) {
        location.setY(location.getY() + 1);

        return location.getWorld().spawn(location, ArmorStand.class, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.customName(Component.text("1.0"));
            armorStand.setCustomNameVisible(true);
            armorStand.setBasePlate(false);
            armorStand.setGravity(false);
        });
    }

    public static LuckyBlockType getRandomLuckyBlockType() {
        return LuckyBlockType.values()[ThreadLocalRandom.current().nextInt(LuckyBlockType.values().length)];
    }

    private static void buildIronCageAround(@NotNull Entity ent) {
        Material fence = Material.STONE;
        Material roof = Material.STONE;
        Location entLoc = ent.getLocation();

        int delta = (3 / 2);
        Location corner1 = new Location(entLoc.getWorld(), entLoc.getBlockX() + delta, entLoc.getBlockY() + 1, entLoc.getBlockZ() - delta);
        Location corner2 = new Location(entLoc.getWorld(), entLoc.getBlockX() - delta, entLoc.getBlockY() + 1, entLoc.getBlockZ() + delta);
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if ((x == minX || x == maxX) || (z == minZ || z == maxZ)) {
                        Block b = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() + y, z);
                        b.setType(fence);
                    }

                    if (y == 2 - 1) {
                        Block b = corner1.getWorld().getBlockAt(x, entLoc.getBlockY() + y + 1, z);
                        b.setType(roof);
                    }
                }
            }
        }
    }

    public static @Nullable ItemStack getLuckyBlock() {
        return luckyBlock == null ? null : luckyBlock.clone();
    }

    @NotNull
    public static List<Block> getBlocks(@NotNull Block start, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++) {
            for (double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++) {
                for (double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++) {
                    Location loc = new Location(start.getWorld(), x, y, z);
                    blocks.add(loc.getBlock());
                }
            }
        }
        return blocks;
    }

    private static void spawnTree(@NotNull Location location, int tries) {
        TreeType treeType = getRandomTree();
        boolean spawned = location.getWorld().generateTree(location, treeType);

        if (tries >= 5) {
            activateLuckyBlock(null, getRandomLuckyBlockType(), location);
            return;
        }

        if (!spawned)
            spawnTree(location, tries + 1);
    }

    private static TreeType getRandomTree() {
        return TreeType.values()[ThreadLocalRandom.current().nextInt(TreeType.values().length)];
    }

    public enum LuckyBlockType {
        ITEM,
        POTION_EFFECT,
        SPAWN_ENTITY,
        TREE,
        CAGE,
        EXPERIENCE,
        EXPLOSION,
        ANVIL
    }
}
