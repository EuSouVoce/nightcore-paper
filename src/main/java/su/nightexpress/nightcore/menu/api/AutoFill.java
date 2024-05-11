package su.nightexpress.nightcore.menu.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.menu.click.ClickAction;

public class AutoFill<T> {

    private int[] slots;
    private List<T> items;
    private Function<T, ItemStack> itemCreator;
    private Function<T, ClickAction> clickAction;

    public AutoFill() {
        this.setSlots();
        this.setItems(new ArrayList<>());
        this.setItemCreator(obj -> new ItemStack(Material.AIR));
        this.setClickAction(obj -> (viewer, event) -> {
        });
    }

    public int[] getSlots() { return this.slots; }

    public void setSlots(final int... slots) { this.slots = slots; }

    @NotNull
    public List<T> getItems() { return this.items; }

    public void setItems(@NotNull final Collection<T> items) { this.setItems(new ArrayList<>(items)); }

    public void setItems(@NotNull final List<T> items) { this.items = items; }

    @NotNull
    public Function<T, ItemStack> getItemCreator() { return this.itemCreator; }

    public void setItemCreator(@NotNull final Function<T, ItemStack> itemCreator) { this.itemCreator = itemCreator; }

    @NotNull
    public Function<T, ClickAction> getClickAction() { return this.clickAction; }

    public void setClickAction(@NotNull final Function<T, ClickAction> clickAction) { this.clickAction = clickAction; }
}
