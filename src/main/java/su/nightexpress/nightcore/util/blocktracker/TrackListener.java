package su.nightexpress.nightcore.util.blocktracker;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.manager.AbstractListener;

public class TrackListener<P extends NightCorePlugin> extends AbstractListener<P> {

    private static final String META_TRACK_FALLING_BLOCK = "tracker_falling_block";

    public TrackListener(@NotNull final P plugin) { super(plugin); }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLoad(final WorldLoadEvent event) { PlayerBlockTracker.initWorld(event.getWorld()); }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onUnload(final WorldUnloadEvent event) { PlayerBlockTracker.terminateWorld(event.getWorld()); }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLoad(final ChunkLoadEvent event) { PlayerBlockTracker.initChunk(event.getChunk()); }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onUnload(final ChunkUnloadEvent event) { PlayerBlockTracker.terminateChunk(event.getChunk()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(final BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        PlayerBlockTracker.track(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(final BlockBreakEvent event) { PlayerBlockTracker.unTrack(event.getBlock()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(final BlockExplodeEvent event) { PlayerBlockTracker.unTrack(event.blockList()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(final EntityExplodeEvent event) { PlayerBlockTracker.unTrack(event.blockList()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBurn(final BlockBurnEvent event) { PlayerBlockTracker.unTrack(event.getBlock()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFade(final BlockFadeEvent event) {
        if (event.getBlock().getBlockData() instanceof Lightable)
            return;
        PlayerBlockTracker.unTrack(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onForm(final BlockFormEvent event) { PlayerBlockTracker.unTrack(event.getBlock()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFlow(final BlockFromToEvent event) { PlayerBlockTracker.unTrack(event.getBlock()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGrow(final BlockGrowEvent event) { PlayerBlockTracker.unTrack(event.getBlock()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStructureGrow(final StructureGrowEvent e) {
        if (e.getPlayer() == null)
            return;
        PlayerBlockTracker.track(e.getBlocks().stream().map(BlockState::getBlock).toList());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMultiPlace(final BlockMultiPlaceEvent event) {
        PlayerBlockTracker.track(event.getReplacedBlockStates().stream().map(BlockState::getBlock).toList());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonExtend(final BlockPistonExtendEvent event) { PlayerBlockTracker.shift(event.getDirection(), event.getBlocks()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonRetract(final BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }
        PlayerBlockTracker.shift(event.getDirection(), event.getBlocks());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpread(final BlockSpreadEvent event) { PlayerBlockTracker.unTrack(event.getBlock()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityForm(final EntityBlockFormEvent event) { PlayerBlockTracker.unTrack(event.getBlock()); }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityFallingSpawn(final EntitySpawnEvent event) {
        final Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock))
            return;

        final Block block = entity.getLocation().getBlock();
        if (!PlayerBlockTracker.isTracked(block))
            return;

        entity.setMetadata(TrackListener.META_TRACK_FALLING_BLOCK, new FixedMetadataValue(this.plugin, true));
        PlayerBlockTracker.unTrack(block);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityFallingLand(final EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();
        if (!entity.hasMetadata(TrackListener.META_TRACK_FALLING_BLOCK))
            return;

        PlayerBlockTracker.trackForce(event.getBlock());
    }
}
