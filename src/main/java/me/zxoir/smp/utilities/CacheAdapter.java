package me.zxoir.smp.utilities;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import me.zxoir.smp.customclasses.Cache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/18/2022
 */
public class CacheAdapter implements JsonSerializer<Cache>, JsonDeserializer<Cache> {

    @Override
    public Cache deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        Bukkit.getLogger().info("DB 1");
        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        Bukkit.getLogger().info("DB 2");
        Location backLocation = GlobalCache.getAdapter().fromJson(object.get("backLocation").getAsString(), Location.class);
        Bukkit.getLogger().info("DB 3");
        int playtimeDaysRewarded = object.get("playtimeDaysRewarded").getAsInt();
        Bukkit.getLogger().info("DB 4");
        List<UUID> friends = GlobalCache.getAdapter().fromJson(object.get("friends").getAsString(), new TypeToken<List<UUID>>() {
        }.getType());
        Bukkit.getLogger().info("DB 5");
        ConcurrentHashMap<String, Location> checkpoints = GlobalCache.getAdapter().fromJson(object.get("checkpoints").getAsString(), new TypeToken<ConcurrentHashMap<String, Location>>() {
        }.getType());
        Bukkit.getLogger().info("DB 6");
        return new Cache(uuid, backLocation, playtimeDaysRewarded, friends, checkpoints);
    }

    @Override
    public JsonElement serialize(@NotNull Cache src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("uuid", src.getUuid().toString());
        object.addProperty("backLocation", GlobalCache.getAdapter().toJson(src.getBackLocation(), Location.class));
        object.addProperty("playtimeDaysRewarded", src.getPlaytimeDaysRewarded());
        object.addProperty("friends", GlobalCache.getAdapter().toJson(src.getFriends()));
        object.addProperty("checkpoints", GlobalCache.getAdapter().toJson(src.getCheckpoints()));

        return object;
    }

}
