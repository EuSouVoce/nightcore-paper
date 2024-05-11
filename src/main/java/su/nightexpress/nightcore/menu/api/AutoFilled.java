package su.nightexpress.nightcore.menu.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.item.ItemOptions;
import su.nightexpress.nightcore.menu.item.MenuItem;

import java.util.ArrayList;
import java.util.List;

public interface AutoFilled<I> extends Menu {

    default boolean open(@NotNull final Player player, final int page) { return this.open(this.getViewerOrCreate(player), page); }

    default boolean open(@NotNull final MenuViewer viewer, final int page) {
        viewer.setPage(page);
        return this.open(viewer);
    }

    void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<I> autoFill);

    /*
     * @Deprecated int[] getAutoFillSlots();
     * @Deprecated @NotNull List<I> getAutoFills(@NotNull Player player);
     * @Deprecated @NotNull ItemStack getAutoFillStack(@NotNull Player
     * player, @NotNull I object);
     * @Deprecated@NotNull ClickAction getAutoFillClick(@NotNull I object);
     */

    default void autoFill(@NotNull final MenuViewer viewer) {
        final AutoFill<I> autoFill = new AutoFill<>();
        this.onAutoFill(viewer, autoFill);
        this.getAutoFillItems(viewer, autoFill).forEach(this::addItem);
    }

    @NotNull
    default List<MenuItem> getAutoFillItems(@NotNull final MenuViewer viewer, @NotNull final AutoFill<I> autoFill) {
        final Player player = viewer.getPlayer();
        final List<MenuItem> items = new ArrayList<>();
        final List<I> origin = autoFill.getItems();// this.getAutoFills(player);

        final int[] slots = autoFill.getSlots();// this.getAutoFillSlots();
        final int limit = slots.length;
        final int pages = (int) Math.ceil((double) origin.size() / (double) limit);
        viewer.setPages(pages);
        viewer.setPage(Math.min(viewer.getPage(), viewer.getPages()));

        final int skip = (viewer.getPage() - 1) * limit;

        final List<I> list = origin.stream().skip(skip).limit(limit).toList();

        int count = 0;
        for (final I object : list) {
            final ItemStack item = autoFill.getItemCreator().apply(object);// this.getAutoFillStack(player, object);
            final ItemOptions options = ItemOptions.personalWeak(player);
            final MenuItem menuItem = new MenuItem(item).setPriority(100).setOptions(options).setSlots(slots[count++])
                    .addClick(autoFill.getClickAction().apply(object)/* this.getAutoFillClick(object) */);
            items.add(menuItem);
        }

        return items;
    }
}
