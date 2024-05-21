package su.nightexpress.nightcore.menu;

import java.util.function.UnaryOperator;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.util.text.NightMessage;

public class MenuOptions {

    private String title;
    private int size;
    private InventoryType type;
    private int autoRefresh;

    private long lastAutoRefresh;

    @Deprecated
    public MenuOptions(@NotNull final String title, final int size, @NotNull final InventoryType type) { this(title, size, type, 0); }

    public MenuOptions(@NotNull final String title, @NotNull final MenuSize size) { this(title, size.getSize(), InventoryType.CHEST, 0); }

    public MenuOptions(@NotNull final String title, @NotNull final InventoryType type) { this(title, 27, type, 0); }

    public MenuOptions(@NotNull final String title, final int size, @NotNull final InventoryType type, final int autoRefresh) {
        this.setTitle(title);
        this.setSize(size);
        this.setType(type);
        this.setAutoRefresh(autoRefresh);
    }

    public MenuOptions(@NotNull final MenuOptions options) {
        this(options.getTitle(), options.getSize(), options.getType(), options.getAutoRefresh());
        this.size = options.getSize();
        this.autoRefresh = options.getAutoRefresh();
        this.lastAutoRefresh = 0L;
    }

    @NotNull
    public Inventory createInventory() {
        if (this.getType() == InventoryType.CHEST) {
            return Bukkit.getServer().createInventory(null, this.getSize(), this.getTitleFormatted());
        } else {
            return Bukkit.getServer().createInventory(null, this.getType(), this.getTitleFormatted());
        }
    }

    @NotNull
    public String getTitle() { return this.title; }

    @NotNull
    public String getTitleFormatted() { return NightMessage.asLegacy(this.getTitle()); }

    public void setTitle(@NotNull final String title) { this.title = title; }

    public void editTitle(@NotNull final UnaryOperator<String> function) { this.setTitle(function.apply(this.getTitle())); }

    public int getSize() { return this.size; }

    public void setSize(int size) {
        if (size <= 0 || size % 9 != 0 || size > 54)
            size = 27;
        this.size = size;
    }

    @NotNull
    public InventoryType getType() { return this.type; }

    public void setType(@NotNull final InventoryType type) { this.type = type; }

    public int getAutoRefresh() { return this.autoRefresh; }

    public void setAutoRefresh(final int autoRefresh) { this.autoRefresh = Math.max(0, autoRefresh); }

    public long getLastAutoRefresh() { return this.lastAutoRefresh; }

    public void setLastAutoRefresh(final long lastAutoRefresh) { this.lastAutoRefresh = lastAutoRefresh; }

    public boolean isReadyToRefresh() {
        return this.getAutoRefresh() > 0 && System.currentTimeMillis() - this.getLastAutoRefresh() >= this.getAutoRefresh();
    }
}
