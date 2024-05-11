package su.nightexpress.nightcore.menu.click;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ClickResult {

    private final int slot;
    private final ItemStack itemStack;
    private final boolean isMenu;

    public ClickResult(final int slot, @Nullable final ItemStack itemStack, final boolean isMenu) {
        this.slot = slot;
        this.itemStack = itemStack;
        this.isMenu = isMenu;
    }

    public int getSlot() { return this.slot; }

    @Nullable
    public ItemStack getItemStack() { return this.itemStack; }

    public boolean isEmptySlot() { return this.itemStack == null || this.itemStack.getType().isAir(); }

    public boolean isMenu() { return this.isMenu; }

    public boolean isInventory() { return !this.isMenu(); }
}
