package me.zxoir.smp.customclasses;

import me.zxoir.smp.SMP;
import me.zxoir.smp.managers.UserManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/5/2022
 */
public class Cache {
    private final UUID uuid;
    private final AtomicBoolean isAfk = new AtomicBoolean(false);
    private Instant sessionStart;
    private Instant canKillEntity;
    private Instant canBreakBlock;
    private Instant canAdvancement;
    private Location backLocation;
    private int playtimeDaysRewarded = 1;
    private String teamName;
    private List<UUID> friends = new ArrayList<>();
    private Location trackedCheckpoint;
    private Player trackedFriend;
    private BukkitTask trackingTask;
    private ConcurrentHashMap<String, Location> checkpoints = new ConcurrentHashMap<>();
    private final List<UUID> friendRequests = new ArrayList<>();
    private boolean tracking = false;

    public Cache(UUID uuid, Location backLocation, int playtimeDaysRewarded, List<UUID> friends, ConcurrentHashMap<String, Location> checkpoints) {
        this.uuid = uuid;
        this.backLocation = backLocation;
        this.playtimeDaysRewarded = playtimeDaysRewarded;
        this.friends = friends;
        this.checkpoints = checkpoints;
    }

    public Cache(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Nullable
    public Instant getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(@Nullable Instant sessionStart) {
        this.sessionStart = sessionStart;
    }

    public Instant getCanBreakBlock() {
        return canBreakBlock;
    }

    public void setCanBreakBlock(Instant canBreakBlock) {
        this.canBreakBlock = canBreakBlock;
    }

    public Instant getCanKillEntity() {
        return canKillEntity;
    }

    public void setCanKillEntity(Instant canKillEntity) {
        this.canKillEntity = canKillEntity;
    }

    public Instant getCanAdvancement() {
        return canAdvancement;
    }

    public void setCanAdvancement(Instant canAdvancement) {
        this.canAdvancement = canAdvancement;
    }

    public void setIsAfk(boolean isAfk) {
        this.isAfk.set(isAfk);

        UserManager.updateUserPlaytime(UserManager.getUser(uuid));

        if (isAfk)
            sessionStart = null;
        else
            sessionStart = Instant.now();
    }

    public boolean isAfk() {
        return isAfk.get();
    }

    public Location getBackLocation() {
        return backLocation;
    }

    public void setBackLocation(Location backLocation) {
        this.backLocation = backLocation;
        UserManager.getUser(uuid).save();
    }

    public List<UUID> getFriends() {
        return friends;
    }

    public List<UUID> getFriendRequests() {
        return friendRequests;
    }

    public int getPlaytimeDaysRewarded() {
        return playtimeDaysRewarded;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public User getUser() {
        return UserManager.getUser(uuid);
    }

    public ConcurrentHashMap<String, Location> getCheckpoints() {
        return checkpoints;
    }

    public void setPlaytimeDaysRewarded(int playtimeDaysRewarded) {
        this.playtimeDaysRewarded = playtimeDaysRewarded;
    }

    public void createCheckpoint(@NotNull String checkpoint, Location location) {
        checkpoints.put(checkpoint.toLowerCase(), location);
        getUser().save();
    }

    public Location getTrackedCheckpoint() {
        return trackedCheckpoint;
    }

    public Player getTrackedFriend() {
        return trackedFriend;
    }

    public BukkitTask getTrackingTask() {
        return trackingTask;
    }

    public void deleteCheckpoint(@NotNull String checkpoint) {
        if (isTracking()) {
            for (String checkpointName : checkpoints.keySet()) {
                if (checkpoints.get(checkpointName).equals(trackedCheckpoint)) {
                    untrackCheckpoint();
                }
            }
        }

        checkpoints.remove(checkpoint.toLowerCase());
        getUser().save();
    }

    public void trackCheckpoint(Location location) {
        if (tracking) return;
        Player player = Bukkit.getPlayer(uuid);
        tracking = true;
        trackedCheckpoint = location;


        trackingTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (player == null) {
                    this.cancel();
                    tracking = false;
                    return;
                }

                if (!player.isOnline()) {
                    this.cancel();
                    tracking = false;
                    return;
                }

                if (!player.getLocation().getWorld().getName().equals(location.getWorld().getName())) {
                    this.cancel();
                    tracking = false;
                    return;
                }

                double distance = player.getLocation().distance(location);
                String direction = "";

                Vector playerToEntity = location.clone().subtract(player.getLocation()).toVector();
                Vector playerLooking = player.getLocation().getDirection();
                double x1 = playerToEntity.getX();
                double z1 = playerToEntity.getZ();
                double x2 = playerLooking.getX();
                double z2 = playerLooking.getZ();
                int x1Int = (int) x1;
                int z1Int = (int) z1;
                int x2Int = (int) x2;
                int z2Int = (int) z2;
                double angle = Math.atan2(x1 * z2 - z1 * x2, x1 * x2 + z1 * z2) * 180 / Math.PI;
                if (angle >= -45 && angle < 45) {
                    direction = "↑";
                } else if (angle >= 45 && angle < 135) {
                    direction = "←";
                } else if (angle >= 135 && angle <= 180 || angle >= -180 && angle < -135) {
                    direction = "↓";
                } else if (angle >= -135 && angle < -45) {
                    direction = "→";
                }

                if (x1Int == x2Int && z1Int == z2Int) {
                    if (player.getLocation().getY() > location.getY()) {
                        direction = "DOWN";
                    } else if (player.getLocation().getY() < location.getY()) {
                        direction = "UP";
                    }
                }

                Component message = distance >= 1 ? Component.text(colorize("&aYou are " + String.format("%.2f", distance) + " blocks away! &8(&b" + direction + "&8)")).asComponent() : Component.text(colorize("&aYou have arrived!")).asComponent();
                player.sendActionBar(message);

                if (distance < 1) {
                    untrackCheckpoint();
                }
            }

        }.runTaskTimerAsynchronously(SMP.getInstance(), 0, 5);
    }

    public boolean isTracking() {
        return tracking;
    }

    public void setTracking(boolean tracking) {
        this.tracking = tracking;
    }

    public void untrackCheckpoint() {
        setTracking(false);
        if (trackingTask == null || trackingTask.isCancelled())
            return;
        trackedCheckpoint = null;
        trackingTask.cancel();
    }

    public void trackFriend(Player trackedFriend) {
        if (tracking) return;
        Player player = Bukkit.getPlayer(uuid);
        tracking = true;
        this.trackedFriend = trackedFriend;

        trackingTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (player == null || trackedFriend == null) {
                    this.cancel();
                    tracking = false;
                    return;
                }

                if (!player.isOnline() || !trackedFriend.isOnline()) {
                    this.cancel();
                    tracking = false;
                    return;
                }

                if (!player.getLocation().getWorld().getName().equals(trackedFriend.getLocation().getWorld().getName())) {
                    this.cancel();
                    tracking = false;
                    return;
                }


                double distance = player.getLocation().distance(trackedFriend.getLocation());
                String direction = "";

                Vector playerToEntity = trackedFriend.getLocation().clone().subtract(player.getLocation()).toVector();
                Vector playerLooking = player.getLocation().getDirection();
                double x1 = playerToEntity.getX();
                double z1 = playerToEntity.getZ();
                double x2 = playerLooking.getX();
                double z2 = playerLooking.getZ();
                int x1Int = (int) x1;
                int z1Int = (int) z1;
                int x2Int = (int) x2;
                int z2Int = (int) z2;
                double angle = Math.atan2(x1 * z2 - z1 * x2, x1 * x2 + z1 * z2) * 180 / Math.PI;
                if (angle >= -45 && angle < 45) {
                    direction = "↑";
                } else if (angle >= 45 && angle < 135) {
                    direction = "←";
                } else if (angle >= 135 && angle <= 180 || angle >= -180 && angle < -135) {
                    direction = "↓";
                } else if (angle >= -135 && angle < -45) {
                    direction = "→";
                }

                if (x1Int == x2Int && z1Int == z2Int) {
                    if (player.getLocation().getY() > trackedFriend.getLocation().getY()) {
                        direction = "DOWN";
                    } else if (player.getLocation().getY() < trackedFriend.getLocation().getY()) {
                        direction = "UP";
                    }
                }

                Component message = distance >= 1 ? Component.text(colorize("&aYou are " + String.format("%.2f", distance) + " blocks away! &8(&b" + direction + "&8)")).asComponent() : Component.text(colorize("&aYou have arrived!")).asComponent();
                player.sendActionBar(message);

                if (distance < 1)
                    untrackFriend();
            }

        }.runTaskTimerAsynchronously(SMP.getInstance(), 0, 5);
    }

    public void untrackFriend() {
        setTracking(false);
        if (trackingTask == null || trackingTask.isCancelled())
            return;
        trackedFriend = null;
        trackingTask.cancel();
    }
}
