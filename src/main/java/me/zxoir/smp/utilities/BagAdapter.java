package me.zxoir.smp.utilities;

import com.google.gson.*;
import lombok.SneakyThrows;
import me.zxoir.smp.customclasses.Bag;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/20/2023
 */
public class BagAdapter implements JsonSerializer<Bag>, JsonDeserializer<Bag> {
    @SneakyThrows
    @Override
    public Bag deserialize(@NotNull JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        Bukkit.getLogger().info("q");

        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        Bukkit.getLogger().info("w");
        Bukkit.getLogger().info(object.get("bagContent").getAsString());
        ItemStack[] bagContent = object.get("bagContent").getAsString().equals("null") ? null : ItemDeserializer.itemStackArrayFromBase64(object.get("bagContent").getAsString());
        Bukkit.getLogger().info("e");
        int bagSize = object.get("bagSize").getAsInt();
        Bukkit.getLogger().info("r");
        return new Bag(uuid, bagContent, bagSize);
    }

    @Override
    public JsonElement serialize(@NotNull Bag bag, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        Bukkit.getLogger().info("t");
        object.addProperty("uuid", bag.getUuid().toString());
        Bukkit.getLogger().info("y");
        object.addProperty("bagContent", bag.getBagContent() == null ? "null" : ItemDeserializer.itemStackArrayToBase64(bag.getBagContent()));
        Bukkit.getLogger().info("u");
        object.addProperty("bagSize", bag.getBagSize());
        Bukkit.getLogger().info("i");

        return object;
    }
}
