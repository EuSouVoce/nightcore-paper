package su.nightexpress.nightcore.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

public class PDCUtil {

    public static final PersistentDataType<byte[], UUID> UUID = new UUIDDataType();

    @NotNull
    public static <Z> Optional<Z> get(@NotNull final ItemStack holder, @NotNull final PersistentDataType<?, Z> type, @NotNull final NamespacedKey key) {
        final ItemMeta meta = holder.getItemMeta();
        if (meta == null)
            return Optional.empty();

        return PDCUtil.get(meta, type, key);
    }

    @NotNull
    public static <Z> Optional<Z> get(@NotNull final PersistentDataHolder holder, @NotNull final PersistentDataType<?, Z> type,
            @NotNull final NamespacedKey key) {
        final PersistentDataContainer container = holder.getPersistentDataContainer();
        if (container.has(key, type)) {
            return Optional.ofNullable(container.get(key, type));
        }
        return Optional.empty();
    }

    public static void set(@NotNull final ItemStack holder, @NotNull final NamespacedKey key, final boolean value) {
        PDCUtil.set(holder, PersistentDataType.INTEGER, key, value ? 1 : 0);
    }

    public static void set(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key, final boolean value) {
        PDCUtil.set(holder, PersistentDataType.INTEGER, key, value ? 1 : 0);
    }

    public static void set(@NotNull final ItemStack holder, @NotNull final NamespacedKey key, final double value) {
        PDCUtil.set(holder, PersistentDataType.DOUBLE, key, value);
    }

    public static void set(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key, final double value) {
        PDCUtil.set(holder, PersistentDataType.DOUBLE, key, value);
    }

    public static void set(@NotNull final ItemStack holder, @NotNull final NamespacedKey key, final int value) {
        PDCUtil.set(holder, PersistentDataType.INTEGER, key, value);
    }

    public static void set(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key, final int value) {
        PDCUtil.set(holder, PersistentDataType.INTEGER, key, value);
    }

    public static void set(@NotNull final ItemStack holder, @NotNull final NamespacedKey key, final long value) {
        PDCUtil.set(holder, PersistentDataType.LONG, key, value);
    }

    public static void set(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key, final long value) {
        PDCUtil.set(holder, PersistentDataType.LONG, key, value);
    }

    public static void set(@NotNull final ItemStack holder, @NotNull final NamespacedKey key, @Nullable final String value) {
        PDCUtil.set(holder, PersistentDataType.STRING, key, value);
    }

    public static void set(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key, @Nullable final String value) {
        PDCUtil.set(holder, PersistentDataType.STRING, key, value);
    }

    public static void set(@NotNull final ItemStack holder, @NotNull final NamespacedKey key, @Nullable final UUID value) { PDCUtil.set(holder, PDCUtil.UUID, key, value); }

    public static void set(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key, @Nullable final UUID value) {
        PDCUtil.set(holder, PDCUtil.UUID, key, value);
    }

    public static <T, Z> void set(@NotNull final ItemStack item, @NotNull final PersistentDataType<T, Z> dataType, @NotNull final NamespacedKey key,
            @Nullable final Z value) {
        ItemUtil.editMeta(item, meta -> PDCUtil.set(meta, dataType, key, value));
    }

    public static <T, Z> void set(@NotNull final PersistentDataHolder holder, @NotNull final PersistentDataType<T, Z> dataType,
            @NotNull final NamespacedKey key, @Nullable final Z value) {
        if (value == null) {
            PDCUtil.remove(holder, key);
            return;
        }

        final PersistentDataContainer container = holder.getPersistentDataContainer();
        container.set(key, dataType, value);
    }

    public static void remove(@NotNull final ItemStack holder, @NotNull final NamespacedKey key) {
        ItemUtil.editMeta(holder, meta -> PDCUtil.remove(meta, key));
    }

    public static void remove(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key) {
        final PersistentDataContainer container = holder.getPersistentDataContainer();
        container.remove(key);
    }

    @NotNull
    public static Optional<String> getString(@NotNull final ItemStack holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PersistentDataType.STRING, key);
    }

    @NotNull
    public static Optional<String> getString(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PersistentDataType.STRING, key);
    }

    @NotNull
    public static Optional<Integer> getInt(@NotNull final ItemStack holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PersistentDataType.INTEGER, key);
    }

    @NotNull
    public static Optional<Integer> getInt(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PersistentDataType.INTEGER, key);
    }

    @NotNull
    public static Optional<Long> getLong(@NotNull final ItemStack holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PersistentDataType.LONG, key);
    }

    @NotNull
    public static Optional<Long> getLong(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PersistentDataType.LONG, key);
    }

    @NotNull
    public static Optional<Double> getDouble(@NotNull final ItemStack holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PersistentDataType.DOUBLE, key);
    }

    @NotNull
    public static Optional<Double> getDouble(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PersistentDataType.DOUBLE, key);
    }

    @NotNull
    public static Optional<Boolean> getBoolean(@NotNull final ItemStack holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PersistentDataType.INTEGER, key).map(i -> i != 0);
    }

    @NotNull
    public static Optional<Boolean> getBoolean(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PersistentDataType.INTEGER, key).map(i -> i != 0);
    }

    @NotNull
    public static Optional<UUID> getUUID(@NotNull final ItemStack holder, @NotNull final NamespacedKey key) { return PDCUtil.get(holder, PDCUtil.UUID, key); }

    @NotNull
    public static Optional<UUID> getUUID(@NotNull final PersistentDataHolder holder, @NotNull final NamespacedKey key) {
        return PDCUtil.get(holder, PDCUtil.UUID, key);
    }

    public static class UUIDDataType implements PersistentDataType<byte[], UUID> {

        @NotNull
        @Override
        public Class<byte[]> getPrimitiveType() { return byte[].class; }

        @NotNull
        @Override
        public Class<UUID> getComplexType() { return UUID.class; }

        @Override
        public byte @NotNull [] toPrimitive(final UUID complex, @NotNull final PersistentDataAdapterContext context) {
            final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(complex.getMostSignificantBits());
            bb.putLong(complex.getLeastSignificantBits());
            return bb.array();
        }

        @Override
        public @NotNull UUID fromPrimitive(final byte @NotNull [] primitive, @NotNull final PersistentDataAdapterContext context) {
            final ByteBuffer bb = ByteBuffer.wrap(primitive);
            final long firstLong = bb.getLong();
            final long secondLong = bb.getLong();
            return new UUID(firstLong, secondLong);
        }
    }
}
