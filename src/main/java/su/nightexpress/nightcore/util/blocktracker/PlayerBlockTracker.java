package su.nightexpress.nightcore.util.blocktracker;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import su.nightexpress.nightcore.util.Plugins;

public final class PlayerBlockTracker {

    public static final Set<Predicate<Block>> BLOCK_FILTERS = new HashSet<>();
    public static final NamespacedKey TRACKED_DATA_KEY = NamespacedKey.minecraft("tracked_chunk_data");
    private static final Map<UUID, TrackedWorld> TRACKED_WORLD_MAP = new Object2ObjectOpenHashMap<>();

    private static TrackListener<?> listener;

    public static void initialize() {
        if (PlayerBlockTracker.listener == null) {
            PlayerBlockTracker.initCurrentlyLoadedWorlds();
            (PlayerBlockTracker.listener = new TrackListener<>(Plugins.CORE)).registerListeners();
        }
    }

    public static void shutdown() {
        if (PlayerBlockTracker.listener != null) {
            PlayerBlockTracker.terminateCurrentlyLoadedWorlds();
            PlayerBlockTracker.listener.unregisterListeners();
            PlayerBlockTracker.listener = null;
            PlayerBlockTracker.BLOCK_FILTERS.clear();
        }
    }

    public static void initWorld(@NotNull final World world) {
        final TrackedWorld trackedWorld = new TrackedWorld();
        for (final Chunk loadedChunk : world.getLoadedChunks()) {
            trackedWorld.initChunk(loadedChunk);
        }
        PlayerBlockTracker.TRACKED_WORLD_MAP.put(world.getUID(), trackedWorld);
    }

    public static void terminateWorld(@NotNull final World world) {
        final TrackedWorld trackedWorld = PlayerBlockTracker.TRACKED_WORLD_MAP.remove(world.getUID());
        if (trackedWorld == null) {
            return;
        }
        for (final Chunk loadedChunk : world.getLoadedChunks()) {
            trackedWorld.terminateChunk(loadedChunk);
        }
    }

    public static void initChunk(@NotNull final Chunk chunk) {
        final TrackedWorld trackedWorld = PlayerBlockTracker.getTrackedWorldOf(chunk);
        if (trackedWorld == null) {
            return;
        }
        trackedWorld.initChunk(chunk);
    }

    public static void terminateChunk(@NotNull final Chunk chunk) {
        final TrackedWorld trackedWorld = PlayerBlockTracker.getTrackedWorldOf(chunk);
        if (trackedWorld == null) {
            return;
        }
        trackedWorld.terminateChunk(chunk);
    }

    public static void initCurrentlyLoadedWorlds() { Bukkit.getWorlds().forEach(PlayerBlockTracker::initWorld); }

    public static void terminateCurrentlyLoadedWorlds() { Bukkit.getWorlds().forEach(PlayerBlockTracker::terminateWorld); }

    public static boolean isTracked(@NotNull final Block block) {
        final TrackedWorld trackedWorld = PlayerBlockTracker.getTrackedWorldOf(block);
        if (trackedWorld == null) {
            return false;
        }
        return trackedWorld.isTracked(block);
    }

    public static void track(@NotNull final Block block) {
        if (PlayerBlockTracker.BLOCK_FILTERS.stream().noneMatch(filter -> filter.test(block)))
            return;
        PlayerBlockTracker.trackForce(block);
    }

    public static void trackForce(@NotNull final Block block) {
        final TrackedWorld trackedWorld = PlayerBlockTracker.getTrackedWorldOf(block);
        if (trackedWorld == null) {
            return;
        }
        trackedWorld.add(block);
    }

    public static void unTrack(@NotNull final Block block) {
        final TrackedWorld trackedWorld = PlayerBlockTracker.getTrackedWorldOf(block);
        if (trackedWorld == null) {
            return;
        }
        trackedWorld.remove(block);
    }

    public static void track(@NotNull final Collection<Block> trackedBlocks) { trackedBlocks.forEach(PlayerBlockTracker::trackForce); }

    public static void unTrack(@NotNull final Collection<Block> trackedBlocks) { trackedBlocks.forEach(PlayerBlockTracker::unTrack); }

    public static void shift(@NotNull final BlockFace direction, @NotNull final List<Block> blocks) {
        PlayerBlockTracker.unTrack(blocks);
        PlayerBlockTracker.track(blocks.stream().map(block -> block.getRelative(direction)).toList());
    }

    public static void move(@NotNull final Block from, @NotNull final Block to) {
        PlayerBlockTracker.unTrack(from);
        PlayerBlockTracker.track(to);
    }

    @Nullable
    private static TrackedWorld getTrackedWorldOf(@NotNull final Block block) {
        return PlayerBlockTracker.TRACKED_WORLD_MAP.get(block.getWorld().getUID());
    }

    @Nullable
    private static TrackedWorld getTrackedWorldOf(@NotNull final Chunk chunk) {
        return PlayerBlockTracker.TRACKED_WORLD_MAP.get(chunk.getWorld().getUID());
    }
}
