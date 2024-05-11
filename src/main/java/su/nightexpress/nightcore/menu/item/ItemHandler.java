package su.nightexpress.nightcore.menu.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.menu.api.Menu;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.click.ClickAction;
import su.nightexpress.nightcore.util.Placeholders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class ItemHandler {
    // @formatter:off
    public static final String RETURN        = "return";
    public static final String CLOSE         = "close";
    public static final String NEXT_PAGE     = "page_next";
    public static final String PREVIOUS_PAGE = "page_previous";

    private final String                name;
    private final List<ClickAction>     clickActions;
    private final Predicate<MenuViewer> visibilityPolicy;
    // @formatter:on

    public ItemHandler() { this(Placeholders.DEFAULT, null, null); }

    public ItemHandler(@NotNull final String name) { this(name, null, null); }

    public ItemHandler(@NotNull final String name, @Nullable final ClickAction clickAction) { this(name, clickAction, null); }

    public ItemHandler(@NotNull final String name, @Nullable final ClickAction clickAction,
            @Nullable final Predicate<MenuViewer> visibilityPolicy) {
        this.name = name.toLowerCase();
        this.clickActions = new ArrayList<>();
        this.visibilityPolicy = visibilityPolicy;

        if (clickAction != null) {
            this.getClickActions().add(clickAction);
        }
    }

    /**
     * The main purpose of this method is to quickly create ItemHandler object for
     * non-configurable GUIs. <br>
     * <br>
     * Do NOT use this for items requires specific handler name.
     * 
     * @param action Click action
     * @return ItemHandler with random UUID as a name.
     */
    @NotNull
    public static ItemHandler forClick(@NotNull final ClickAction action) { return new ItemHandler(UUID.randomUUID().toString(), action); }

    @NotNull
    public static ItemHandler forNextPage(@NotNull final Menu menu) {
        return new ItemHandler(ItemHandler.NEXT_PAGE, (viewer, event) -> {
            if (viewer.getPage() < viewer.getPages()) {
                viewer.setPage(viewer.getPage() + 1);
                menu.open(viewer.getPlayer());
            }
        }, viewer -> viewer.getPage() < viewer.getPages());
    }

    @NotNull
    public static ItemHandler forPreviousPage(@NotNull final Menu menu) {
        return new ItemHandler(ItemHandler.PREVIOUS_PAGE, (viewer, event) -> {
            if (viewer.getPage() > 1) {
                viewer.setPage(viewer.getPage() - 1);
                menu.open(viewer.getPlayer());
            }
        }, viewer -> viewer.getPage() > 1);
    }

    @NotNull
    public static ItemHandler forClose(@NotNull final Menu menu) {
        return new ItemHandler(ItemHandler.CLOSE, (viewer, event) -> menu.runNextTick(() -> viewer.getPlayer().closeInventory()));
    }

    @NotNull
    public static ItemHandler forReturn(@NotNull final Menu menu, @NotNull final ClickAction action) {
        return new ItemHandler(ItemHandler.RETURN, action);
    }

    @NotNull
    public String getName() { return this.name; }

    @NotNull
    public List<ClickAction> getClickActions() { return this.clickActions; }

    @Nullable
    public Predicate<MenuViewer> getVisibilityPolicy() { return this.visibilityPolicy; }
}
