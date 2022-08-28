package me.zxoir.smp.utilities;

import com.google.gson.*;
import me.zxoir.smp.customclasses.Cache;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.UUID;

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

        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        Location backLocation = GlobalCache.getAdapter().fromJson(object.get("backLocation").getAsString(), Location.class);
        int playtimeDaysRewarded = object.get("playtimeDaysRewarded").getAsInt();

        return new Cache(uuid, backLocation, playtimeDaysRewarded);
    }

    @Override
    public JsonElement serialize(@NotNull Cache src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("uuid", src.getUuid().toString());
        object.addProperty("backLocation", GlobalCache.getAdapter().toJson(src.getBackLocation(), Location.class));
        object.addProperty("playtimeDaysRewarded", src.getPlaytimeDaysRewarded());

        return object;
    }

}
