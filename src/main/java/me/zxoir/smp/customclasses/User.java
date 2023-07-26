package me.zxoir.smp.customclasses;

import lombok.Getter;
import me.zxoir.smp.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/5/2022
 */

@Getter
public class User {
    private final UUID uuid;
    private final Date dateJoined;
    private final Bag bag;
    private final Stats stats;
    private final Cache cache;
    private Duration playtime;

    public User(UUID uuid, Date dateJoined, Bag bag, Stats stats, Cache cache, Duration playtime) {
        this.uuid = uuid;
        this.dateJoined = dateJoined;
        this.bag = bag;
        this.stats = stats;
        this.cache = cache;
        this.playtime = playtime;
    }

    public User(UUID uuid) {
        this.uuid = uuid;
        dateJoined = new Date();
        bag = new Bag(uuid);
        stats = new Stats(uuid);
        cache = new Cache(uuid);
        playtime = Duration.ZERO;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public void save() {
        DatabaseManager.updateDatabase(this);
    }

    public void setPlaytime(Duration playtime) {
        this.playtime = playtime;
    }
}
