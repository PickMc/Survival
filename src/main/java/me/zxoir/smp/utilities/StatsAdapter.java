package me.zxoir.smp.utilities;

import com.google.gson.*;
import me.zxoir.smp.customclasses.Stats;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MIT License Copyright (c) 2020/2021 Zxoir
 *
 * @author Zxoir
 * @since 10/20/2020
 */
public class StatsAdapter implements JsonSerializer<Stats>, JsonDeserializer<Stats> {
    @Override
    public Stats deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        int level = object.get("level").getAsInt();
        int deaths = object.get("deaths").getAsInt();
        String experience = object.get("experience").getAsString();


        return new Stats(uuid, new AtomicInteger(level), new AtomicInteger(deaths), new AtomicReference<>(new BigDecimal(experience)));
    }

    @Override
    public JsonElement serialize(@NotNull Stats src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        UUID uuid = src.getUuid();
        int level = src.getLevel().get();
        int deaths = src.getDeaths();
        String experience = src.getExperience().get().toString();

        object.addProperty("uuid", uuid.toString());
        object.addProperty("level", level);
        object.addProperty("deaths", deaths);
        object.addProperty("experience", experience);

        return object;
    }
}
