package me.zxoir.smp.customclasses;

import me.zxoir.smp.managers.UserManager;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public Cache(UUID uuid, Location backLocation, int playtimeDaysRewarded) {
        this.uuid = uuid;
        this.backLocation = backLocation;
        this.playtimeDaysRewarded = playtimeDaysRewarded;
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

    public int getPlaytimeDaysRewarded() {
        return playtimeDaysRewarded;
    }

    public void setPlaytimeDaysRewarded(int playtimeDaysRewarded) {
        this.playtimeDaysRewarded = playtimeDaysRewarded;
    }
}
