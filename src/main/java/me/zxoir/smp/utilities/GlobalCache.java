package me.zxoir.smp.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.zxoir.smp.customclasses.Cache;
import me.zxoir.smp.customclasses.Stats;
import me.zxoir.smp.customclasses.Warp;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/12/2022
 */

public class GlobalCache {
    public static final String STAFFPERMISSION = "staff.admin";
    public static final String NOPERMISSION = Colors.error + "You don't have permission to use this command";
    @Getter
    private static final HashSet<Player> warpingPlayers = new HashSet<>();
    @Getter
    private static final HashSet<World> whitelistedBackWorlds = new HashSet<>();
    @Setter
    @Getter
    private static Warp spawnWarp;
    @Getter
    private static final Gson adapter = new GsonBuilder().registerTypeAdapter(Cache.class, new CacheAdapter()).registerTypeAdapter(Stats.class, new StatsAdapter()).registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();
}
