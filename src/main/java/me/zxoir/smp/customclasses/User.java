package me.zxoir.smp.customclasses;

import lombok.Getter;
import lombok.Setter;
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
    private final Stats stats;
    private final Cache cache;
    @Setter
    private Duration playtime;

    public User(UUID uuid, Date dateJoined, Stats stats, Cache cache, Duration playtime) {
        this.uuid = uuid;
        this.dateJoined = dateJoined;
        this.stats = stats;
        this.cache = cache;
        this.playtime = playtime;
    }

    public User(UUID uuid) {
        this.uuid = uuid;
        dateJoined = new Date();
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
}
