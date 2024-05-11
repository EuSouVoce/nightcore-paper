package su.nightexpress.nightcore.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitThing {

    @Nullable
    public static <T extends Keyed> T fromRegistry(@NotNull final Registry<T> registry, @NotNull String key) {
        key = StringUtil.lowerCaseUnderscoreStrict(key);

        final NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
        return registry.get(namespacedKey);
    }

    @NotNull
    public static <T extends Keyed> Set<T> allFromRegistry(@NotNull final Registry<T> registry) {
        if (Version.isBehind(Version.V1_20_R2)) {
            return StreamSupport.stream(registry.spliterator(), false).collect(Collectors.toSet());
        }
        return registry.stream().collect(Collectors.toSet());
    }

    @NotNull
    public static String toString(@NotNull final Keyed keyed) { return keyed.getKey().getKey(); }

    @Nullable
    public static Material getMaterial(@NotNull final String name) { return BukkitThing.fromRegistry(Registry.MATERIAL, name); }

    @NotNull
    public static Set<Material> getMaterials() { return BukkitThing.allFromRegistry(Registry.MATERIAL); }

    @NotNull
    public static Set<Enchantment> getEnchantments() { return BukkitThing.allFromRegistry(Registry.ENCHANTMENT); }

    @SuppressWarnings("deprecation")
    @Nullable
    public static Enchantment getEnchantment(@NotNull final String name) {
        if (Version.isBehind(Version.V1_19_R3)) {
            return Enchantment.getByKey(NamespacedKey.minecraft(StringUtil.lowerCaseUnderscoreStrict(name)));
        }
        return BukkitThing.fromRegistry(Registry.ENCHANTMENT, name);
    }

    @Nullable
    public static EntityType getEntityType(@NotNull final String name) { return BukkitThing.fromRegistry(Registry.ENTITY_TYPE, name); }

    @Nullable
    public static Attribute getAttribute(@NotNull final String name) { return BukkitThing.fromRegistry(Registry.ATTRIBUTE, name); }

    @Nullable
    public static PotionEffectType getPotionEffect(@NotNull final String name) { return BukkitThing.fromRegistry(Registry.EFFECT, name); }

    @Nullable
    public static Sound getSound(@NotNull final String name) { return BukkitThing.fromRegistry(Registry.SOUNDS, name); }
}
