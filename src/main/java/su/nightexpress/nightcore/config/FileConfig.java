package su.nightexpress.nightcore.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.ItemNbt;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.Reflex;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.text.NightMessage;

public class FileConfig extends YamlConfiguration {

    public static final String EXTENSION = ".yml";

    private final File file;
    private boolean changed;

    public FileConfig(@NotNull final String path, @NotNull final String file) { this(new File(path, file)); }

    public FileConfig(@NotNull final File file) {
        this.changed = false;
        this.options().width(512);

        FileUtil.create(file);
        this.file = file;
        this.reload();
    }

    public static boolean isConfig(@NotNull final File file) { return file.getName().endsWith(FileConfig.EXTENSION); }

    @NotNull
    public static String getName(@NotNull final File file) {
        final String name = file.getName();

        if (FileConfig.isConfig(file)) {
            return name.substring(0, name.length() - FileConfig.EXTENSION.length());
        }
        return name;
    }

    @NotNull
    public static FileConfig loadOrExtract(@NotNull final NightCorePlugin plugin, @NotNull String path, @NotNull final String file) {
        if (!path.endsWith("/")) {
            path += "/";
        }
        return FileConfig.loadOrExtract(plugin, path + file);
    }

    @NotNull
    public static FileConfig loadOrExtract(@NotNull final NightCorePlugin plugin, @NotNull String filePath) {
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }

        final File file = new File(plugin.getDataFolder() + filePath);
        if (FileUtil.create(file)) {
            try {
                final InputStream input = plugin.getClass().getResourceAsStream(filePath);
                if (input != null)
                    FileUtil.copy(input, file);
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
        return new FileConfig(file);
    }

    @NotNull
    public static List<FileConfig> loadAll(@NotNull final String path) { return FileConfig.loadAll(path, false); }

    @NotNull
    public static List<FileConfig> loadAll(@NotNull final String path, final boolean deep) {
        return FileUtil.getConfigFiles(path, deep).stream().map(FileConfig::new).toList();
    }

    public void initializeOptions(@NotNull final Class<?> clazz) { FileConfig.initializeOptions(clazz, this); }

    public static void initializeOptions(@NotNull final Class<?> clazz, @NotNull final FileConfig config) {
        for (final ConfigValue<?> value : Reflex.getFields(clazz, ConfigValue.class)) {
            value.read(config);
        }
        // config.saveChanges();
    }

    @NotNull
    public File getFile() { return this.file; }

    public void save() {
        try {
            this.save(this.file);
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean saveChanges() {
        if (!this.changed)
            return false;

        this.save();
        this.changed = false;
        return true;
    }

    public boolean reload() {
        try {
            this.load(this.file);
            this.changed = false;
            return true;
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public boolean addMissing(@NotNull final String path, @Nullable final Object val) {
        if (this.contains(path))
            return false;
        this.set(path, val);
        return true;
    }

    @Override
    public void set(@NotNull final String path, @Nullable Object value) {
        if (value instanceof final String str) {
            value = Colorizer.plain(str);
        } else if (value instanceof final Collection<?> collection) {
            final List<Object> list = new ArrayList<>(collection);
            list.replaceAll(obj -> obj instanceof final String str ? Colorizer.plain(str) : obj);
            value = list;
        } else if (value instanceof final Location location) {
            value = LocationUtil.serialize(location);
        } else if (value instanceof final Enum<?> en) {
            value = en.name();
        }
        super.set(path, value);
        this.changed = true;
    }

    public void setComments(@NotNull final String path, @Nullable final String... comments) {
        this.setComments(path, Arrays.asList(comments));
    }

    public void setInlineComments(@NotNull final String path, @Nullable final String... comments) {
        this.setInlineComments(path, Arrays.asList(comments));
    }

    @Override
    public void setComments(@NotNull final String path, @Nullable final List<String> comments) {
        if (this.getComments(path).equals(comments))
            return;

        super.setComments(path, comments);
        this.changed = true;
    }

    @Override
    public void setInlineComments(@NotNull final String path, @Nullable final List<String> comments) {
        super.setInlineComments(path, comments);
    }

    public boolean remove(@NotNull final String path) {
        if (!this.contains(path))
            return false;
        this.set(path, null);
        return true;
    }

    @NotNull
    public Set<String> getSection(@NotNull final String path) {
        final ConfigurationSection section = this.getConfigurationSection(path);
        return section == null ? Collections.emptySet() : section.getKeys(false);
    }

    @Override
    @Nullable
    public String getString(@NotNull final String path) {
        final String str = super.getString(path);
        return str == null || str.isEmpty() ? null : str;
    }

    @Override
    @NotNull
    public String getString(@NotNull final String path, @Nullable final String def) {
        final String str = super.getString(path, def);
        return str == null ? "" : str;
    }

    @NotNull
    public Set<String> getStringSet(@NotNull final String path) { return new HashSet<>(this.getStringList(path)); }

    @Override
    @Nullable
    public Location getLocation(@NotNull final String path) {
        final String raw = this.getString(path);
        return raw == null ? null : LocationUtil.deserialize(raw);
    }

    public int[] getIntArray(@NotNull final String path) { return this.getIntArray(path, new int[0]); }

    public int[] getIntArray(@NotNull final String path, final int[] def) {
        final String str = this.getString(path);
        return str == null ? def : NumberUtil.getIntArray(str);
    }

    public void setIntArray(@NotNull final String path, final int[] arr) {
        if (arr == null) {
            this.set(path, null);
            return;
        }
        this.set(path, String.join(",", IntStream.of(arr).boxed().map(String::valueOf).toList()));
    }

    @NotNull
    public String @NotNull [] getStringArray(@NotNull final String path, @NotNull final String[] def) {
        final String str = this.getString(path);
        return str == null ? def : str.split(",");
    }

    public void setStringArray(@NotNull final String path, final String[] arr) {
        if (arr == null) {
            this.set(path, null);
            return;
        }
        this.set(path, String.join(",", arr));
    }

    @Nullable
    public <T extends Enum<T>> T getEnum(@NotNull final String path, @NotNull final Class<T> clazz) {
        return StringUtil.getEnum(this.getString(path), clazz).orElse(null);
    }

    @NotNull
    public <T extends Enum<T>> T getEnum(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final T def) {
        return StringUtil.getEnum(this.getString(path), clazz).orElse(def);
    }

    @NotNull
    public <T extends Enum<T>> List<T> getEnumList(@NotNull final String path, @NotNull final Class<T> clazz) {
        return this.getStringSet(path).stream().map(str -> StringUtil.getEnum(str, clazz).orElse(null)).filter(Objects::nonNull).toList();
    }

    /*
     * @NotNull public Set<FireworkEffect> getFireworkEffects(@NotNull String path)
     * { Set<FireworkEffect> effects = new HashSet<>(); for (String sId :
     * this.getSection(path)) { String path2 = path + "." + sId + ".";
     * FireworkEffect.Type type = this.getEnum(path2 + "Type",
     * FireworkEffect.Type.class); if (type == null) continue; boolean flicker =
     * this.getBoolean(path2 + "Flicker"); boolean trail = this.getBoolean(path2 +
     * "Trail"); Set<Color> colors = new HashSet<>(); for (String colorRaw :
     * this.getStringList(path2 + "Colors")) {
     * colors.add(StringUtil.parseColor(colorRaw)); } Set<Color> fadeColors = new
     * HashSet<>(); for (String colorRaw : this.getStringList(path2 +
     * "Fade_Colors")) { fadeColors.add(StringUtil.parseColor(colorRaw)); }
     * FireworkEffect.Builder builder = FireworkEffect.builder()
     * .with(type).flicker(flicker).trail(trail).withColor(colors).withFade(
     * fadeColors); effects.add(builder.build()); } return effects; }
     */

    @NotNull
    public ItemStack getItem(@NotNull final String path, @Nullable final ItemStack def) {
        final ItemStack item = this.getItem(path);
        return item.getType().isAir() && def != null ? def : item;
    }

    @NotNull
    public ItemStack getItem(@NotNull String path) {
        if (!path.isEmpty() && !path.endsWith("."))
            path = path + ".";

        final Material material = Material.getMaterial(this.getString(path + "Material", "").toUpperCase());
        if (material == null || material == Material.AIR)
            return new ItemStack(Material.AIR);

        final ItemStack item = new ItemStack(material);
        item.setAmount(this.getInt(path + "Amount", 1));

        final String headSkin = this.getString(path + "SkinURL", "");
        if (!headSkin.isEmpty()) {
            ItemUtil.setHeadSkin(item, headSkin);
        } else {
            final String headTexture = this.getString(path + "Head_Texture", "");
            if (!headTexture.isEmpty()) {
                ItemUtil.setSkullTexture(item, headTexture);
            }
        }

        final ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return item;

        final int durability = this.getInt(path + "Durability");
        if (durability > 0 && meta instanceof final Damageable damageable) {
            damageable.setDamage(durability);
        }

        final String name = this.getString(path + "Name");
        meta.setDisplayName(name != null ? NightMessage.asLegacy(Colorizer.apply(name)) : null);
        meta.setLore(NightMessage.asLegacy(Colorizer.apply(this.getStringList(path + "Lore"))));

        for (final String sKey : this.getSection(path + "Enchants")) {
            final Enchantment enchantment = BukkitThing.getEnchantment(sKey);
            if (enchantment == null)
                continue;

            final int eLvl = this.getInt(path + "Enchants." + sKey);
            if (eLvl <= 0)
                continue;

            meta.addEnchant(enchantment, eLvl, true);
        }

        final int model = this.getInt(path + "Custom_Model_Data");
        meta.setCustomModelData(model != 0 ? model : null);

        final List<String> flags = this.getStringList(path + "Item_Flags");
        if (flags.contains(Placeholders.WILDCARD)) {
            meta.addItemFlags(ItemFlag.values());
        } else {
            flags.stream().map(str -> StringUtil.getEnum(str, ItemFlag.class).orElse(null)).filter(Objects::nonNull)
                    .forEach(meta::addItemFlags);
        }

        final String colorRaw = this.getString(path + "Color");
        if (colorRaw != null && !colorRaw.isEmpty()) {
            final Color color = StringUtil.getColor(colorRaw);
            if (meta instanceof final LeatherArmorMeta armorMeta) {
                armorMeta.setColor(color);
            } else if (meta instanceof final PotionMeta potionMeta) {
                potionMeta.setColor(color);
            }
        }

        meta.setUnbreakable(this.getBoolean(path + "Unbreakable"));
        item.setItemMeta(meta);

        return item;
    }

    public void setItem(@NotNull String path, @Nullable final ItemStack item) {
        if (item == null) {
            this.set(path, null);
            return;
        }

        if (!path.endsWith("."))
            path = path + ".";
        this.set(path.substring(0, path.length() - 1), null);

        final Material material = item.getType();
        this.set(path + "Material", material.name());
        this.set(path + "Amount", item.getAmount() <= 1 ? null : item.getAmount());
        this.set(path + "SkinURL", ItemUtil.getHeadSkin(item));
        if (!this.contains(path + "SkinURL")) {
            this.set(path + "Head_Texture", ItemUtil.getSkullTexture(item));
        }

        final ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        if (meta instanceof final Damageable damageable) {
            this.set(path + "Durability", damageable.getDamage() <= 0 ? null : damageable.getDamage());
        }

        this.set(path + "Name", meta.getDisplayName().isEmpty() ? null : meta.getDisplayName());
        this.set(path + "Lore", meta.getLore());

        for (final Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            this.set(path + "Enchants." + entry.getKey().getKey().getKey(), entry.getValue());
        }
        this.set(path + "Custom_Model_Data", meta.hasCustomModelData() ? meta.getCustomModelData() : null);

        Color color = null;
        String colorRaw = null;
        if (meta instanceof final PotionMeta potionMeta) {
            color = potionMeta.getColor();
        } else if (meta instanceof final LeatherArmorMeta armorMeta) {
            color = armorMeta.getColor();
        }
        if (color != null) {
            colorRaw = color.getRed() + "," + color.getGreen() + "," + color.getBlue();
        }
        this.set(path + "Color", colorRaw);

        final List<String> itemFlags = new ArrayList<>(meta.getItemFlags().stream().map(ItemFlag::name).toList());
        this.set(path + "Item_Flags", itemFlags.isEmpty() ? null : itemFlags);
        this.set(path + "Unbreakable", meta.isUnbreakable() ? true : null);
    }

    @Nullable
    public ItemStack getItemEncoded(@NotNull final String path) {
        final String compressed = this.getString(path);
        if (compressed == null)
            return null;

        return ItemNbt.decompress(compressed);
    }

    public void setItemEncoded(@NotNull final String path, @Nullable final ItemStack item) {
        this.set(path, item == null ? null : ItemNbt.compress(item));
    }

    @NotNull
    public ItemStack[] getItemsEncoded(@NotNull final String path) { return ItemNbt.decompress(this.getStringList(path)); }

    public void setItemsEncoded(@NotNull final String path, @NotNull final List<ItemStack> item) { this.set(path, ItemNbt.compress(item)); }
}
