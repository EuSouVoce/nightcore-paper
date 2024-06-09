package su.nightexpress.nightcore.language.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Pair;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.TextRoot;
import su.nightexpress.nightcore.util.text.tag.Tags;

public class LangItem extends LangEntry<Pair<String, List<String>>> {

    private final List<String> defaultLore;

    private String localizedName;
    private List<String> localizedLore;

    private TextRoot wrappedName;
    private List<TextRoot> wrappedLore;

    public LangItem(@NotNull final String key, @NotNull final String defaultName, @NotNull final List<String> defaultLore) {
        super(key, defaultName);
        this.defaultLore = defaultLore;
    }

    @NotNull
    public static LangItem of(@NotNull final String key, @NotNull final String name, @NotNull final String... lore) {
        return new LangItem(key, name, Arrays.asList(lore));
    }

    @Override
    public boolean write(@NotNull final FileConfig config) {
        if (!config.contains(this.getPath())) {
            config.set(this.getPath() + ".Name", this.getDefaultName());
            config.set(this.getPath() + ".Lore", this.getDefaultLore());
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public Pair<String, List<String>> load(@NotNull final NightCorePlugin plugin) {
        final FileConfig config = plugin.getLang();

        this.write(config);

        this.setLocalizedName(config.getString(this.getPath() + ".Name", this.getDefaultName()));
        this.setLocalizedLore(config.getStringList(this.getPath() + ".Lore"));

        return Pair.of(this.getLocalizedName(), this.getLocalizedLore());
    }

    public void apply(@NotNull final ItemStack item) {
        ItemUtil.editMeta(item, meta -> {
            meta.setDisplayName(this.getWrappedName().toLegacy());
            meta.setLore(this.getWrappedLore().stream().map(TextRoot::toLegacy).toList());
        });
    }

    @NotNull
    public String getDefaultName() { return this.getDefaultText(); }

    @NotNull
    public List<String> getDefaultLore() { return this.defaultLore; }

    @NotNull
    public String getLocalizedName() { return this.localizedName; }

    public void setLocalizedName(@NotNull final String localizedName) {
        this.localizedName = localizedName;
        this.wrappedName = NightMessage.create(localizedName);
    }

    @NotNull
    public List<String> getLocalizedLore() { return this.localizedLore; }

    public void setLocalizedLore(@NotNull final List<String> localizedLore) {
        this.localizedLore = localizedLore;
        this.wrappedLore = new ArrayList<>();
        localizedLore.forEach(line -> this.wrappedLore.add(NightMessage.create(line)));
    }

    @NotNull
    public TextRoot getWrappedName() { return this.wrappedName; }

    @NotNull
    public List<TextRoot> getWrappedLore() { return this.wrappedLore; }

    public static final String CLICK = "Click";
    public static final String LMB = "Left-Click";
    public static final String RMB = "Right-Click";
    public static final String DROP_KEY = "[Q / Drop] Key";
    public static final String SWAP_KEY = "[F / Swap] Key";
    public static final String SHIFT_LMB = "Shift-Left";
    public static final String SHIFT_RMB = "Shift-Right";
    public static final String DRAG_DROP = "Drag & Drop";

    @NotNull
    public static Builder builder(@NotNull final String key) { return new Builder(key); }

    public static final class Builder {

        private final String key;
        private String name;
        private final List<String> lore;

        public Builder(@NotNull final String key) {
            this.key = key;
            this.name = "";
            this.lore = new ArrayList<>();
        }

        @NotNull
        public LangItem build() { return new LangItem(this.key, this.name, this.lore); }

        @NotNull
        public Builder name(@NotNull final String name) {
            this.name = Tags.LIGHT_YELLOW.enclose(Tags.BOLD.enclose(name));
            return this;
        }

        @NotNull
        public Builder text(@NotNull final String... text) {
            for (final String str : text) {
                this.addLore(Tags.LIGHT_GRAY.enclose(str));
            }
            return this;
        }

        @NotNull
        public Builder textRaw(@NotNull final String... text) { return this.addLore(text); }

        @NotNull
        public Builder currentHeader() { return this.addLore(Tags.LIGHT_YELLOW.enclose(Tags.BOLD.enclose("Current:"))); }

        @NotNull
        public Builder current(@NotNull final String type, @NotNull final String value) {
            return this.addLore(Tags.LIGHT_YELLOW.enclose("● " + Tags.LIGHT_GRAY.enclose(type + ": ") + value));
        }

        @NotNull
        public Builder current(@NotNull final String value) {
            return this.addLore(Tags.LIGHT_YELLOW.enclose("● " + Tags.LIGHT_GRAY.enclose(value)));
        }

        @NotNull
        public Builder click(@NotNull final String action) { return this.click(LangItem.CLICK, action); }

        @NotNull
        public Builder leftClick(@NotNull final String action) { return this.click(LangItem.LMB, action); }

        @NotNull
        public Builder rightClick(@NotNull final String action) { return this.click(LangItem.RMB, action); }

        @NotNull
        public Builder shiftLeft(@NotNull final String action) { return this.click(LangItem.SHIFT_LMB, action); }

        @NotNull
        public Builder shiftRight(@NotNull final String action) { return this.click(LangItem.SHIFT_RMB, action); }

        @NotNull
        public Builder dropKey(@NotNull final String action) { return this.click(LangItem.DROP_KEY, action); }

        @NotNull
        public Builder swapKey(@NotNull final String action) { return this.click(LangItem.SWAP_KEY, action); }

        @NotNull
        public Builder dragAndDrop(@NotNull final String action) { return this.click(LangItem.DRAG_DROP, action); }

        @NotNull
        public Builder click(@NotNull final String click, @NotNull final String action) {
            return this.addLore(Tags.LIGHT_YELLOW.enclose("[▶]") + " "
                    + Tags.LIGHT_GRAY.enclose(click + " to " + Tags.LIGHT_YELLOW.enclose(action) + "."));
        }

        @NotNull
        public Builder emptyLine() { return this.addLore(""); }

        /*
         * @NotNull private Builder addLore(@NotNull String prefix, @NotNull String...
         * text) { for (String str : text) { this.lore.add(prefix + str); } return this;
         * }
         */

        @NotNull
        private Builder addLore(@NotNull final String... text) {
            Collections.addAll(this.lore, text);
            return this;
        }
    }
}
