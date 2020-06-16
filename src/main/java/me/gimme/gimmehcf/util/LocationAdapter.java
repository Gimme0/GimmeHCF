package me.gimme.gimmehcf.util;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;

public class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("not a JSON object");
        }

        final JsonObject o = json.getAsJsonObject();
        final JsonElement world = o.get("world");
        final JsonElement x = o.get("x");
        final JsonElement y = o.get("y");
        final JsonElement z = o.get("z");
        final JsonElement yaw = o.get("yaw");
        final JsonElement pitch = o.get("pitch");

        World worldObject = Bukkit.getWorld(UUID.fromString(world.getAsString()));
        if (worldObject == null) throw new IllegalArgumentException("World not found");

        return new Location(worldObject, x.getAsDouble(), y.getAsDouble(), z.getAsDouble(), yaw.getAsFloat(), pitch.getAsFloat());
    }

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject o = new JsonObject();

        o.addProperty("world", Objects.requireNonNull(src.getWorld()).getUID().toString());
        o.addProperty("x", src.getX());
        o.addProperty("y", src.getY());
        o.addProperty("z", src.getZ());
        o.addProperty("yaw", src.getYaw());
        o.addProperty("pitch", src.getPitch());

        return o;
    }

}
