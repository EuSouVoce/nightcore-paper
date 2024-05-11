package su.nightexpress.nightcore.database.serialize;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.nightcore.util.ItemNbt;

import java.lang.reflect.Type;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(final ItemStack item, final Type type, final JsonSerializationContext context) {
        final JsonObject object = new JsonObject();
        object.addProperty("data64", ItemNbt.compress(item));
        return object;
    }

    @Override
    public ItemStack deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        return ItemNbt.decompress(object.get("data64").getAsString());
    }

}
