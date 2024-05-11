package su.nightexpress.nightcore.util;

import com.google.common.base.Splitter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.language.entry.LangItem;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.tag.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ItemReplacer {

    private final ItemStack item;
    private final ItemMeta meta;
    private final PlaceholderMap placeholderMap;

    private String displayName;
    private String lore;

    private boolean trimLore;
    private boolean hideFlags;
    private Player papi;

    public ItemReplacer(@NotNull final ItemStack item) { this(item, item.getItemMeta()); }

    public ItemReplacer(@NotNull final ItemMeta meta) { this(null, meta); }

    public ItemReplacer(@Nullable final ItemStack item, @Nullable final ItemMeta meta) {
        this.item = item;
        this.meta = meta;
        this.placeholderMap = new PlaceholderMap();
    }

    @NotNull
    public static ItemReplacer create(@NotNull final ItemStack item) { return new ItemReplacer(item); }

    @NotNull
    public static ItemReplacer create(@NotNull final ItemMeta meta) { return new ItemReplacer(meta); }

    @SuppressWarnings("deprecation")
    @NotNull
    public ItemReplacer readMeta() {
        if (this.hasMeta()) {
            this.setDisplayName(this.meta.getDisplayName());
            this.setLore(this.meta.getLore());
        }
        return this;
    }

    @NotNull
    public ItemReplacer readLocale(@NotNull final LangItem locale) {
        if (this.hasMeta()) {
            this.setDisplayName(locale.getLocalizedName());
            this.setLore(locale.getLocalizedLore());
        }
        return this;
    }

    @SuppressWarnings("deprecation")
    public void writeMeta() {
        if (!this.hasMeta())
            return;

        this.replace(this.placeholderMap.replacer());
        if (this.papi != null)
            this.injectPlaceholderAPI(this.papi);

        this.meta.setDisplayName(this.getDisplayName() == null ? null : NightMessage.asLegacy(this.getDisplayName()));
        this.meta.setLore(this.packTrimmedLore());

        if (this.isHideFlags()) {
            this.meta.addItemFlags(ItemFlag.values());
        }

        if (this.hasItem()) {
            this.item.setItemMeta(this.meta);
        }
    }

    public static void replace(@NotNull final ItemStack item, @NotNull final UnaryOperator<String> replacer) {
        ItemReplacer.create(item).trimmed().readMeta().replace(replacer).writeMeta();
    }

    @Deprecated
    public static void replace(@NotNull final ItemMeta meta, @NotNull final UnaryOperator<String> replacer) {
        ItemReplacer.create(meta).trimmed().readMeta().replace(replacer).writeMeta();
    }

    public static void replace(@NotNull final ItemStack item, @NotNull final PlaceholderMap replacer) {
        ItemReplacer.create(item).trimmed().readMeta().replace(replacer).writeMeta();
    }

    @Deprecated
    public static void replace(@NotNull final ItemMeta meta, @NotNull final PlaceholderMap replacer) {
        ItemReplacer.create(meta).trimmed().readMeta().replace(replacer).writeMeta();
    }

    public static void replacePlaceholderAPI(@NotNull final ItemStack item, @NotNull final Player player) {
        ItemReplacer.create(item).trimmed().readMeta().replacePlaceholderAPI(player).writeMeta();
    }

    @Deprecated
    public static void replacePlaceholderAPI(@NotNull final ItemMeta meta, @NotNull final Player player) {
        ItemReplacer.create(meta).trimmed().readMeta().replacePlaceholderAPI(player).writeMeta();
    }

    public boolean hasMeta() { return this.meta != null; }

    public boolean hasItem() { return this.item != null; }

    public boolean isTrimLore() { return this.trimLore; }

    public boolean isHideFlags() { return this.hideFlags; }

    @NotNull
    public ItemReplacer trimmed() {
        this.setTrimLore(true);
        return this;
    }

    @NotNull
    public ItemReplacer hideFlags() {
        this.setHideFlags(true);
        return this;
    }

    @NotNull
    public ItemReplacer setHideFlags(final boolean hideFlags) {
        this.hideFlags = hideFlags;
        return this;
    }

    @NotNull
    public ItemReplacer setTrimLore(final boolean trimLore) {
        this.trimLore = trimLore;
        return this;
    }

    @NotNull
    public ItemReplacer replace(@NotNull final String placeholder, @NotNull final String value) {
        return this.replace(placeholder, () -> value);
    }

    @NotNull
    public ItemReplacer replace(@NotNull final String placeholder, @NotNull final Supplier<String> value) {
        this.placeholderMap.add(placeholder, value);
        return this;
    }

    @NotNull
    public ItemReplacer replace(@NotNull final PlaceholderMap... placeholderMaps) {
        for (final PlaceholderMap placeholder : placeholderMaps) {
            this.placeholderMap.add(placeholder);
        }
        return this;
    }

    @NotNull
    public ItemReplacer replace(@NotNull final UnaryOperator<String> replacer) {
        if (this.getDisplayName() != null) {
            this.setDisplayName(replacer.apply(this.getDisplayName()));
        }

        if (this.getLore() != null) {
            this.setLore(replacer.apply(this.getLore()));
        }
        return this;
    }

    @NotNull
    public ItemReplacer replacePlaceholderAPI(@NotNull final Player player) {
        if (Plugins.hasPlaceholderAPI()) {
            this.papi = player;
        }
        return this;
    }

    @NotNull
    public ItemReplacer replaceLoreExact(@NotNull final String placeholder, @NotNull final List<String> replacer) {
        if (this.getLore() == null)
            return this;

        this.replace(placeholder, () -> String.join(Tags.LINE_BREAK.getFullName(), replacer));
        // this.setLore(Lists.replace(this.packLore(), placeholder, replacer));
        return this;
    }

    @NotNull
    public ItemReplacer replaceLore(@NotNull final String placeholder, @NotNull final Supplier<List<String>> replacer) {
        // if (this.getLore() == null) return this;

        this.replace(placeholder, () -> String.join(Tags.LINE_BREAK.getFullName(), replacer.get()));
        // this.setLore(Lists.replace(this.packLore(), placeholder, replacer));
        return this;
    }

    @NotNull
    public ItemReplacer injectLore(@NotNull final String placeholder, @NotNull final List<String> replacer) {
        if (this.getLore() == null)
            return this;

        this.setLore(Lists.replace(this.packLore(), placeholder, replacer));
        return this;
    }

    @NotNull
    public ItemReplacer injectPlaceholderAPI(@NotNull final Player player) {
        if (this.papi != null) {
            this.replace(str -> PlaceholderAPI.setPlaceholders(player, str));
        }
        return this;
    }

    @NotNull
    public ItemReplacer replaceLoreTrail(@NotNull final String placeholder, @NotNull final List<String> replacer) {
        if (this.getLore() == null)
            return this;

        final List<String> loreReplaced = new ArrayList<>();
        for (final String lineHas : this.packLore()) {
            if (lineHas.contains(placeholder)) {
                replacer.forEach(lineRep -> {
                    loreReplaced.add(lineHas.replace(placeholder, lineRep));
                });
                continue;
            }
            loreReplaced.add(lineHas);
        }
        this.setLore(loreReplaced);
        return this;
    }

    @NotNull
    public List<String> packLore() {
        if (this.getLore() == null)
            return new ArrayList<>();

        return Splitter.on("\n").splitToList(this.getLore());
    }

    @NotNull
    public List<String> packTrimmedLore() {
        List<String> lore = NightMessage.asLegacy(this.packLore());
        if (this.isTrimLore()) {
            lore = Lists.stripEmpty(lore);
        }
        return lore;
    }

    @Nullable
    public String getDisplayName() { return this.displayName; }

    @NotNull
    public ItemReplacer setDisplayName(@Nullable final String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Nullable
    public String getLore() { return this.lore; }

    @NotNull
    public ItemReplacer setLore(@Nullable final List<String> lore) { return this.setLore(lore == null ? null : String.join("\n", lore)); }

    @NotNull
    public ItemReplacer setLore(@Nullable final String lore) {
        this.lore = lore;
        return this;
    }
}
