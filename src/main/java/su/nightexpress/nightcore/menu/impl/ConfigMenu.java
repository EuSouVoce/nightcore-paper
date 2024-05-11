package su.nightexpress.nightcore.menu.impl;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.click.ClickAction;
import su.nightexpress.nightcore.menu.click.ClickType;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ConfigMenu<P extends NightCorePlugin> extends AbstractMenu<P> {

    protected static final String DEFAULT_ITEM_SECTION = "Content";

    protected final FileConfig cfg;
    protected final Map<String, ItemHandler> handlerMap;
    protected String itemSection;

    public ConfigMenu(@NotNull final P plugin, @NotNull final FileConfig config) {
        super(plugin);
        this.cfg = config;
        this.handlerMap = new HashMap<>();
        this.itemSection = ConfigMenu.DEFAULT_ITEM_SECTION;

        this.addHandler(ItemHandler.forClose(this));
        if (this instanceof AutoFilled<?>) {
            this.addHandler(ItemHandler.forNextPage(this));
            this.addHandler(ItemHandler.forPreviousPage(this));
        }
    }

    @NotNull
    protected abstract MenuOptions createDefaultOptions();

    @NotNull
    protected abstract List<MenuItem> createDefaultItems();

    public void load() { this.loadConfig(); }

    protected abstract void loadAdditional();

    public void loadConfig() {
        final MenuOptions defaultOptions = this.createDefaultOptions();

        final String title = ConfigValue.create("Settings.Title", defaultOptions.getTitle(), "GUI title.").read(this.cfg);

        final int size = ConfigValue.create("Settings.Size", defaultOptions.getSize(), "GUI size. Must be multiply of 9.",
                "Useful for '" + InventoryType.CHEST.name() + "' Inventory Type only.").read(this.cfg);

        final InventoryType type = ConfigValue.create("Settings.Inventory_Type", InventoryType.class, defaultOptions.getType(), "GUI type.",
                "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/inventory/InventoryType.html").read(this.cfg);

        final int autoRefresh = ConfigValue.create("Settings.Auto_Refresh", defaultOptions.getAutoRefresh(),
                "Sets GUI auto-refresh interval (in seconds). Set this to 0 to disable.").read(this.cfg);

        this.getOptions().setTitle(NightMessage.asLegacy(title));
        this.getOptions().setSize(size);
        this.getOptions().setType(type);
        this.getOptions().setAutoRefresh(autoRefresh);

        this.loadAdditional();

        if (!this.cfg.contains(this.itemSection)) {
            this.createDefaultItems().forEach(menuItem -> {
                final AtomicInteger count = new AtomicInteger();
                final String raw = ItemUtil.getItemName(menuItem.getItemStack());
                final String name = StringUtil.lowerCaseUnderscoreStrict(NightMessage.asLegacy(raw));
                String finalName = name;

                while (this.cfg.contains(this.itemSection + "." + finalName)) {
                    finalName = name + "_" + count.incrementAndGet();
                }

                this.writeItem(menuItem, this.itemSection + "." + finalName);
            });
        }

        this.cfg.getSection(this.itemSection).forEach(sId -> {
            final MenuItem menuItem = this.readItem(this.itemSection + "." + sId);
            this.addItem(menuItem);
        });

        final List<String> comments = new ArrayList<>();
        comments.add("=".repeat(20) + " GUI CONTENT " + "=".repeat(20));
        comments.add("You can freely edit items in this section as you wish (add, remove, modify items).");
        comments.add("The following values are available as button Types:");
        comments.addAll(this.handlerMap.keySet().stream().map(String::toUpperCase).sorted(String::compareTo).toList());
        comments.add("=".repeat(20) + " ITEM OPTIONS " + "=".repeat(20));
        comments.add("> Item: Item to display. Please check: " + Placeholders.WIKI_ITEMS_URL);
        comments.add("> Priority: Button priority. Better values will override other item(s) in the same slot(s).");
        comments.add("> Slots: Button slots. From [0] to [Size - 1]. Split with commas.");
        comments.add("> Click_Commands: Execute custom commands on click. " + Plugins.PLACEHOLDER_API + " available here.");
        comments.add("    Available click types: " + String.join(", ", Lists.getEnums(ClickType.class)));
        comments.add("    Use prefix '" + Players.PLAYER_COMMAND_PREFIX + "' to run command by a player.");
        comments.add("    Click_Commands:");
        comments.add("      LEFT:");
        comments.add("      - say Hello");
        comments.add("      - give " + Placeholders.PLAYER_NAME + " diamond 1");
        comments.add("      - " + Players.PLAYER_COMMAND_PREFIX + " menu open shops");
        comments.add("=".repeat(50));
        this.cfg.setComments(this.itemSection, comments);
        this.cfg.saveChanges();
    }

    @Override
    public void clear() {
        super.clear();
        this.handlerMap.clear();
    }

    public void addHandler(@NotNull final ItemHandler handler) { this.handlerMap.put(handler.getName(), handler); }

    public void addHandler(@NotNull final String name, @NotNull final ClickAction action) { this.addHandler(new ItemHandler(name, action)); }

    @Nullable
    public ItemHandler getHandler(@NotNull final String name) { return this.handlerMap.get(name.toLowerCase()); }

    public boolean removeHandler(@NotNull final String name) { return this.handlerMap.remove(name.toLowerCase()) != null; }

    @NotNull
    protected MenuItem readItem(@NotNull final String path) {
        final String handlerName = this.cfg.getString(path + ".Type", Placeholders.DEFAULT);
        final ItemStack item = this.cfg.getItem(path + ".Item");
        final int[] slots = this.cfg.getIntArray(path + ".Slots");
        final int priority = this.cfg.getInt(path + ".Priority");

        final MenuItem menuItem = new MenuItem(item).setPriority(priority).setSlots(slots);

        final ItemHandler handler = this.getHandler(handlerName);
        if (handler != null) {
            menuItem.setHandler(handler);
        }

        if (this.cfg.contains(path + ".Click_Commands")) {
            final Map<ClickType, List<String>> commandMap = new HashMap<>();
            for (final String sType : this.cfg.getSection(path + ".Click_Commands")) {
                final ClickType clickType = StringUtil.getEnum(sType, ClickType.class).orElse(null);
                if (clickType == null)
                    continue;

                final List<String> commands = this.cfg.getStringList(path + ".Click_Commands." + sType);
                commandMap.put(clickType, commands);
            }
            commandMap.values().removeIf(List::isEmpty);

            final ClickAction clickCommands = (viewer, event) -> {
                final List<String> commands = commandMap.getOrDefault(ClickType.from(event), Collections.emptyList());
                commands.forEach(command -> Players.dispatchCommand(viewer.getPlayer(), command));
            };

            menuItem.addClick(clickCommands);
        }

        return menuItem;
    }

    protected void writeItem(@NotNull final MenuItem menuItem, @NotNull final String path) {
        this.cfg.set(path + ".Priority", menuItem.getPriority());
        this.cfg.setItem(path + ".Item", menuItem.getItemStack());
        this.cfg.setIntArray(path + ".Slots", menuItem.getSlots());
        this.cfg.set(path + ".Type", menuItem.getHandler().getName());
    }
}
