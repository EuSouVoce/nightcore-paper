package su.nightexpress.nightcore.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCore;
import su.nightexpress.nightcore.core.CoreConfig;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.menu.api.Menu;
import su.nightexpress.nightcore.menu.click.ClickResult;
import su.nightexpress.nightcore.menu.impl.AbstractMenu;

public class MenuListener extends AbstractListener<NightCore> {

    public MenuListener(@NotNull final NightCore core) { super(core); }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final PlayerQuitEvent event) { AbstractMenu.PLAYER_MENUS.remove(event.getPlayer().getUniqueId()); }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMenuItemClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();

        final Menu menu = AbstractMenu.getMenu(player);
        if (menu == null)
            return;

        final MenuViewer viewer = menu.getViewer(player);
        if (viewer == null)
            return;

        if (!viewer.canClickAgain(CoreConfig.MENU_CLICK_COOLDOWN.get())) {
            event.setCancelled(true);
            return;
        }

        final Inventory inventory = event.getInventory();
        final ItemStack item = event.getCurrentItem();

        final int slot = event.getRawSlot();
        final boolean isMenu = slot < inventory.getSize();
        // boolean isEmptyItem = item == null || item.getType().isAir();
        final ClickResult result = new ClickResult(slot, item, isMenu);

        menu.onClick(viewer, result, event);
        viewer.setLastClickTime(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMenuItemDrag(final InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();

        final Menu menu = AbstractMenu.getMenu(player);
        if (menu == null)
            return;

        final MenuViewer viewer = menu.getViewer(player);
        if (viewer == null)
            return;

        menu.onDrag(viewer, event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMenuClose(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();

        final Menu menu = AbstractMenu.getMenu(player);
        if (menu == null)
            return;

        final MenuViewer viewer = menu.getViewer(player);
        if (viewer == null)
            return;

        menu.onClose(viewer, event);
    }
}
