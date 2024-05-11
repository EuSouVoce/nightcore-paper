package su.nightexpress.nightcore.util.blocktracker;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class TrackUtil {

    public static long getChunkKey(@NotNull final Chunk chunk) { return TrackUtil.getChunkKey(chunk.getX(), chunk.getZ()); }

    public static long getChunkKey(final int chunkX, final int chunkZ) {
        return (long) chunkX & 0xFFFFFFFFL | ((long) chunkZ & 0xFFFFFFFFL) << 32;
    }

    public static long getChunkKeyOfBlock(@NotNull final Block block) { return TrackUtil.getChunkKey(block.getX() >> 4, block.getZ() >> 4); }

    public static int getRelativeChunkPosition(@NotNull final Block block) {
        final int relX = (block.getX() % 16 + 16) % 16;
        final int relZ = (block.getZ() % 16 + 16) % 16;
        final int relY = block.getY();
        return (relY & 0xFFFF) | ((relX & 0xFF) << 16) | ((relZ & 0xFF) << 24);
    }

}