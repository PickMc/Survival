package me.zxoir.smp.managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.zxoir.smp.customclasses.User;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/5/2022
 */
public class UserManager {
    private static final Cache<UUID, User> cachedUsers = CacheBuilder.newBuilder().build();

    public static void cacheUsers() {
        DatabaseManager.getUsers().forEach(user -> cachedUsers.put(user.getUuid(), user));
    }

    public static void cacheUser(User user) {
        if (cachedUsers.asMap().containsKey(user.getUuid()))
            return;

        Bukkit.getLogger().info("CACHE 1");
        DatabaseManager.saveToDB(user);
        Bukkit.getLogger().info("CACHE 2");
        cachedUsers.put(user.getUuid(), user);
        Bukkit.getLogger().info("CACHE 3");
    }

    public static void updateUserPlaytime(@NotNull User user) {
        user.setPlaytime(getTotalPlaytime(user));
    }

    public static Duration getTotalPlaytime(@NotNull User user) {
        Instant sessionStart = user.getCache().getSessionStart();

        if (sessionStart == null)
            return user.getPlaytime();

        Duration getPlayedTime = Duration.between(sessionStart, Instant.now());
        return user.getPlaytime().plus(getPlayedTime);
    }

    public static User getUser(@NotNull UUID uuid) {
        return cachedUsers.getIfPresent(uuid);
    }

    @NotNull
    public static ConcurrentMap<UUID, User> getUsers() {
        return cachedUsers.asMap();
    }
}
