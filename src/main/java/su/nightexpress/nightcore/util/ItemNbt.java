package su.nightexpress.nightcore.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.NightCore;

public class ItemNbt {

    private static final Class<?> ITEM_STACK_CLASS = Reflex.getClass("net.minecraft.world.item", "ItemStack");
    private static final Class<?> COMPOUND_TAG_CLASS = Reflex.getClass("net.minecraft.nbt", "NBTTagCompound");
    private static final Class<?> NBT_IO_CLASS = Reflex.getClass("net.minecraft.nbt", "NBTCompressedStreamTools");

    private static final Class<?> CRAFT_ITEM_STACK_CLASS = Reflex.getClass(Version.CRAFTBUKKIT_PACKAGE + ".inventory", "CraftItemStack");

    private static final Method CRAFT_ITEM_STACK_AS_NMS_COPY = Reflex.getMethod(ItemNbt.CRAFT_ITEM_STACK_CLASS, "asNMSCopy",
            ItemStack.class);
    private static final Method CRAFT_ITEM_STACK_AS_BUKKIT_COPY = Reflex.getMethod(ItemNbt.CRAFT_ITEM_STACK_CLASS, "asBukkitCopy",
            ItemNbt.ITEM_STACK_CLASS);

    private static final Method NBT_IO_WRITE = Reflex.getMethod(ItemNbt.NBT_IO_CLASS, "a", ItemNbt.COMPOUND_TAG_CLASS, DataOutput.class);
    private static final Method NBT_IO_READ = Reflex.getMethod(ItemNbt.NBT_IO_CLASS, "a", DataInput.class);

    // For 1.20.6+
    private static Method MINECRAFT_SERVER_REGISTRY_ACCESS;
    private static Method ITEM_STACK_PARSE_OPTIONAL;
    private static Method ITEM_STACK_SAVE_OPTIONAL;

    // For 1.20.4 and below.
    private static Constructor<?> NBT_TAG_COMPOUND_NEW;
    private static Method NMS_ITEM_OF;
    private static Method NMS_SAVE;

    static {
        if (Version.isAtLeast(Version.MC_1_20_6)) {
            final Class<?> minecraftServerClass = Reflex.getClass("net.minecraft.server", "MinecraftServer");
            final Class<?> holderLookupProviderClass = Reflex.getInnerClass("net.minecraft.core.HolderLookup", "a"); // Provider

            ItemNbt.MINECRAFT_SERVER_REGISTRY_ACCESS = Reflex.getMethod(minecraftServerClass, "bc");
            ItemNbt.ITEM_STACK_PARSE_OPTIONAL = Reflex.getMethod(ItemNbt.ITEM_STACK_CLASS, "a", holderLookupProviderClass,
                    ItemNbt.COMPOUND_TAG_CLASS);
            ItemNbt.ITEM_STACK_SAVE_OPTIONAL = Reflex.getMethod(ItemNbt.ITEM_STACK_CLASS, "b", holderLookupProviderClass);
        } else {
            ItemNbt.NBT_TAG_COMPOUND_NEW = Reflex.getConstructor(ItemNbt.COMPOUND_TAG_CLASS);
            ItemNbt.NMS_ITEM_OF = Reflex.getMethod(ItemNbt.ITEM_STACK_CLASS, "a", ItemNbt.COMPOUND_TAG_CLASS);
            ItemNbt.NMS_SAVE = Reflex.getMethod(ItemNbt.ITEM_STACK_CLASS, "b", ItemNbt.COMPOUND_TAG_CLASS);
        }
    }

    private static boolean useRegistry;
    private static Object registryAccess;

    public static boolean setup(@NotNull final NightCore core) {
        if (Version.isBehind(Version.MC_1_20_6))
            return true;

        ItemNbt.useRegistry = true;

        final Class<?> craftServerClass = Reflex.getClass(Version.CRAFTBUKKIT_PACKAGE, "CraftServer");
        if (craftServerClass == null) {
            core.error("Could not find 'CraftServer' class in craftbukkit package: '" + Version.CRAFTBUKKIT_PACKAGE + "'.");
            return false;
        }

        final Method getServer = Reflex.getMethod(craftServerClass, "getServer");
        if (getServer == null || ItemNbt.MINECRAFT_SERVER_REGISTRY_ACCESS == null) {
            core.error("Could not find proper class(es) for ItemStack compression util.");
            return false;
        }

        try {
            final Object craftServer = craftServerClass.cast(Bukkit.getServer());
            final Object minecraftServer = getServer.invoke(craftServer);
            ItemNbt.registryAccess = ItemNbt.MINECRAFT_SERVER_REGISTRY_ACCESS.invoke(minecraftServer);
            return true;
        } catch (final ReflectiveOperationException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Nullable
    public static String compress(@NotNull final ItemStack item) {
        if (ItemNbt.CRAFT_ITEM_STACK_AS_NMS_COPY == null || ItemNbt.NBT_IO_WRITE == null) {
            return null;
        }

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutput = new DataOutputStream(outputStream);
        try {
            Object compoundTag;
            final Object itemStack = ItemNbt.CRAFT_ITEM_STACK_AS_NMS_COPY.invoke(null, item);

            if (ItemNbt.useRegistry) {
                if (ItemNbt.ITEM_STACK_SAVE_OPTIONAL == null)
                    return null;

                compoundTag = ItemNbt.ITEM_STACK_SAVE_OPTIONAL.invoke(itemStack, ItemNbt.registryAccess);
            } else {
                if (ItemNbt.NBT_TAG_COMPOUND_NEW == null || ItemNbt.NMS_SAVE == null)
                    return null;

                compoundTag = ItemNbt.NBT_TAG_COMPOUND_NEW.newInstance();
                ItemNbt.NMS_SAVE.invoke(itemStack, compoundTag);
            }

            ItemNbt.NBT_IO_WRITE.invoke(null, compoundTag, dataOutput);

            return new BigInteger(1, outputStream.toByteArray()).toString(32);
        } catch (final ReflectiveOperationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ItemStack decompress(@NotNull final String compressed) {
        if (ItemNbt.NBT_IO_READ == null || ItemNbt.CRAFT_ITEM_STACK_AS_BUKKIT_COPY == null) {
            throw new UnsupportedOperationException("Unsupported server version!");
        }

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(compressed, 32).toByteArray());
        try {
            final Object compoundTag = ItemNbt.NBT_IO_READ.invoke(null, new DataInputStream(inputStream));
            Object itemStack;

            if (ItemNbt.useRegistry) {
                if (ItemNbt.ITEM_STACK_PARSE_OPTIONAL == null)
                    return null;

                itemStack = ItemNbt.ITEM_STACK_PARSE_OPTIONAL.invoke(null, ItemNbt.registryAccess, compoundTag);
            } else {
                if (ItemNbt.NMS_ITEM_OF == null)
                    return null;

                itemStack = ItemNbt.NMS_ITEM_OF.invoke(null, compoundTag);
            }

            return (ItemStack) ItemNbt.CRAFT_ITEM_STACK_AS_BUKKIT_COPY.invoke(null, itemStack);
        } catch (final ReflectiveOperationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @NotNull
    public static List<String> compress(@NotNull final ItemStack[] items) { return ItemNbt.compress(Arrays.asList(items)); }

    @NotNull
    public static List<String> compress(@NotNull final List<ItemStack> items) {
        return new ArrayList<>(items.stream().map(ItemNbt::compress).filter(Objects::nonNull).toList());
    }

    public static ItemStack[] decompress(@NotNull final List<String> list) {
        final List<ItemStack> items = list.stream().map(ItemNbt::decompress).filter(Objects::nonNull).toList();
        return items.toArray(new ItemStack[list.size()]);
    }
}
