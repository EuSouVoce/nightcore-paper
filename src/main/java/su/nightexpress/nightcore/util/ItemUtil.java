package su.nightexpress.nightcore.util;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import su.nightexpress.nightcore.language.LangAssets;

public class ItemUtil {

    private static final String TEXTURES_HOST = "http://textures.minecraft.net/texture/";

    @NotNull
    public static String getItemName(@NotNull final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        return (meta == null || !meta.hasDisplayName()) ? LangAssets.get(item.getType()) : meta.getDisplayName();
    }

    public static void editMeta(@NotNull final ItemStack item, @NotNull final Consumer<ItemMeta> function) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        function.accept(meta);
        item.setItemMeta(meta);
    }

    @NotNull
    public static List<String> getLore(@NotNull final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        return (meta == null || meta.getLore() == null) ? new ArrayList<>() : meta.getLore();
    }

    @NotNull
    @Deprecated
    public static ItemStack createCustomHead(@NotNull final String texture) {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemUtil.setSkullTexture(item, texture);
        return item;
    }

    @NotNull
    public static ItemStack getSkinHead(@NotNull final String texture) {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemUtil.setHeadSkin(item, texture);
        return item;
    }

    public static void setHeadSkin(@NotNull final ItemStack item, @NotNull String urlData) {
        if (urlData.isBlank())
            return;
        if (item.getType() != Material.PLAYER_HEAD)
            return;
        if (!(item.getItemMeta() instanceof final SkullMeta meta))
            return;

        if (!urlData.startsWith(ItemUtil.TEXTURES_HOST)) {
            urlData = ItemUtil.TEXTURES_HOST + urlData;
        }

        try {
            final UUID uuid = UUID.nameUUIDFromBytes(urlData.getBytes());
            // If no name, then meta#getOwnerProfile will return 'null' (wtf?)
            // sometimes swtiching to "new" spigot api is a pain.
            // why the hell i have to dig into nms to learn that...
            final PlayerProfile profile = Bukkit.createPlayerProfile(uuid, urlData.substring(0, 16));
            final URL url = new URL(urlData);
            final PlayerTextures textures = profile.getTextures();
            textures.setSkin(url);
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
            item.setItemMeta(meta);
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    @Nullable
    public static String getHeadSkin(@NotNull final ItemStack item) {
        if (item.getType() != Material.PLAYER_HEAD)
            return null;
        if (!(item.getItemMeta() instanceof final SkullMeta meta))
            return null;

        final PlayerProfile profile = meta.getOwnerProfile();
        if (profile == null)
            return null;

        final URL skin = profile.getTextures().getSkin();
        if (skin == null)
            return null;

        final String raw = skin.toString();
        return raw.substring(ItemUtil.TEXTURES_HOST.length());
    }

    @Deprecated
    public static void setSkullTexture(@NotNull final ItemStack item, @NotNull final String value) {
        if (item.getType() != Material.PLAYER_HEAD)
            return;
        if (!(item.getItemMeta() instanceof final SkullMeta meta))
            return;

        final UUID uuid = UUID.nameUUIDFromBytes(value.getBytes());
        final GameProfile profile = new GameProfile(uuid, "null");
        profile.getProperties().put("textures", new Property("textures", value));

        final Method method = Reflex.getMethod(meta.getClass(), "setProfile", GameProfile.class);
        if (method != null) {
            Reflex.invokeMethod(method, meta, profile);
        } else {
            Reflex.setFieldValue(meta, "profile", profile);
        }

        item.setItemMeta(meta);
    }

    @Nullable
    @Deprecated
    public static String getSkullTexture(@NotNull final ItemStack item) {
        if (item.getType() != Material.PLAYER_HEAD)
            return null;

        final SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null)
            return null;

        final GameProfile profile = (GameProfile) Reflex.getFieldValue(meta, "profile");
        if (profile == null)
            return null;

        final Collection<Property> properties = profile.getProperties().get("textures");
        final Optional<Property> opt = properties.stream().filter(prop -> {
            String name;
            if (Version.isAtLeast(Version.V1_20_R2)) {
                name = prop.name();
            } else {
                name = (String) Reflex.getFieldValue(profile, "name");
            }
            return name != null && name.equalsIgnoreCase("textures");
        }).findFirst();

        if (opt.isEmpty())
            return null;

        if (Version.isAtLeast(Version.V1_20_R2)) {
            return opt.get().value();
        } else {
            return (String) Reflex.getFieldValue(opt.get(), "value");
        }
    }

    public static boolean isTool(@NotNull final ItemStack item) {
        return ItemUtil.isAxe(item) || ItemUtil.isHoe(item) || ItemUtil.isPickaxe(item) || ItemUtil.isShovel(item);
    }

    public static boolean isArmor(@NotNull final ItemStack item) {
        return ItemUtil.isHelmet(item) || ItemUtil.isChestplate(item) || ItemUtil.isLeggings(item) || ItemUtil.isBoots(item);
    }

    public static boolean isBow(@NotNull final ItemStack item) {
        return item.getType() == Material.BOW || item.getType() == Material.CROSSBOW;
    }

    public static boolean isSword(@NotNull final ItemStack item) {
        if (Version.isAtLeast(Version.V1_19_R3)) {
            return Tag.ITEMS_SWORDS.isTagged(item.getType());
        }

        final Material material = item.getType();
        return material == Material.DIAMOND_SWORD || material == Material.GOLDEN_SWORD || material == Material.IRON_SWORD
                || material == Material.NETHERITE_SWORD || material == Material.STONE_SWORD || material == Material.WOODEN_SWORD;
    }

    public static boolean isAxe(@NotNull final ItemStack item) {
        if (Version.isAtLeast(Version.V1_19_R3)) {
            return Tag.ITEMS_AXES.isTagged(item.getType());
        }

        final Material material = item.getType();
        return material == Material.DIAMOND_AXE || material == Material.GOLDEN_AXE || material == Material.IRON_AXE
                || material == Material.NETHERITE_AXE || material == Material.STONE_AXE || material == Material.WOODEN_AXE;
    }

    public static boolean isTrident(@NotNull final ItemStack item) { return item.getType() == Material.TRIDENT; }

    public static boolean isPickaxe(@NotNull final ItemStack item) {
        if (Version.isAtLeast(Version.V1_19_R3)) {
            return Tag.ITEMS_PICKAXES.isTagged(item.getType());
        }

        final Material material = item.getType();
        return material == Material.DIAMOND_PICKAXE || material == Material.GOLDEN_PICKAXE || material == Material.IRON_PICKAXE
                || material == Material.NETHERITE_PICKAXE || material == Material.STONE_PICKAXE || material == Material.WOODEN_PICKAXE;
    }

    public static boolean isShovel(@NotNull final ItemStack item) {
        if (Version.isAtLeast(Version.V1_19_R3)) {
            return Tag.ITEMS_SHOVELS.isTagged(item.getType());
        }

        final Material material = item.getType();
        return material == Material.DIAMOND_SHOVEL || material == Material.GOLDEN_SHOVEL || material == Material.IRON_SHOVEL
                || material == Material.NETHERITE_SHOVEL || material == Material.STONE_SHOVEL || material == Material.WOODEN_SHOVEL;
    }

    public static boolean isHoe(@NotNull final ItemStack item) {
        if (Version.isAtLeast(Version.V1_19_R3)) {
            return Tag.ITEMS_HOES.isTagged(item.getType());
        }

        final Material material = item.getType();
        return material == Material.DIAMOND_HOE || material == Material.GOLDEN_HOE || material == Material.IRON_HOE
                || material == Material.NETHERITE_HOE || material == Material.STONE_HOE || material == Material.WOODEN_HOE;
    }

    public static boolean isElytra(@NotNull final ItemStack item) { return item.getType() == Material.ELYTRA; }

    public static boolean isFishingRod(@NotNull final ItemStack item) { return item.getType() == Material.FISHING_ROD; }

    public static boolean isHelmet(@NotNull final ItemStack item) { return ItemUtil.getEquipmentSlot(item) == EquipmentSlot.HEAD; }

    public static boolean isChestplate(@NotNull final ItemStack item) { return ItemUtil.getEquipmentSlot(item) == EquipmentSlot.CHEST; }

    public static boolean isLeggings(@NotNull final ItemStack item) { return ItemUtil.getEquipmentSlot(item) == EquipmentSlot.LEGS; }

    public static boolean isBoots(@NotNull final ItemStack item) { return ItemUtil.getEquipmentSlot(item) == EquipmentSlot.FEET; }

    @NotNull
    public static EquipmentSlot getEquipmentSlot(@NotNull final ItemStack item) {
        final Material material = item.getType();
        return material.isItem() ? material.getEquipmentSlot() : EquipmentSlot.HAND;
    }

    @Nullable
    @Deprecated
    public static String compress(@NotNull final ItemStack item) { return ItemNbt.compress(item); }

    @Nullable
    @Deprecated
    public static ItemStack decompress(@NotNull final String compressed) { return ItemNbt.decompress(compressed); }

    @NotNull
    @Deprecated
    public static List<String> compress(@NotNull final ItemStack[] items) { return ItemNbt.compress(Arrays.asList(items)); }

    @NotNull
    @Deprecated
    public static List<String> compress(@NotNull final List<ItemStack> items) {
        return new ArrayList<>(items.stream().map(ItemNbt::compress).filter(Objects::nonNull).toList());
    }

    @Deprecated
    public static ItemStack[] decompress(@NotNull final List<String> list) {
        final List<ItemStack> items = list.stream().map(ItemNbt::decompress).filter(Objects::nonNull).toList();
        return items.toArray(new ItemStack[list.size()]);
    }
}
