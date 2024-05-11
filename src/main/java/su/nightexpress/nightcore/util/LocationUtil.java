package su.nightexpress.nightcore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class LocationUtil {

    @Nullable
    public static String serialize(@NotNull final Location location) {
        final World world = location.getWorld();
        if (world == null)
            return null;

        return location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getPitch() + "," + location.getYaw() + ","
                + world.getName();
    }

    @NotNull
    public static List<String> serialize(@NotNull final Collection<Location> list) {
        return new ArrayList<>(list.stream().map(LocationUtil::serialize).filter(Objects::nonNull).toList());
    }

    @Nullable
    public static Location deserialize(@NotNull final String raw) {
        final String[] split = raw.split(",");
        if (split.length != 6)
            return null;

        final World world = Bukkit.getWorld(split[5]);
        if (world == null) {
            Plugins.CORE.error("Invalid/Unloaded world for: '" + raw + "' location!");
            return null;
        }

        final double x = NumberUtil.getAnyDouble(split[0], 0);
        final double y = NumberUtil.getAnyDouble(split[1], 0);
        final double z = NumberUtil.getAnyDouble(split[2], 0);
        final float pitch = (float) NumberUtil.getAnyDouble(split[3], 0);
        final float yaw = (float) NumberUtil.getAnyDouble(split[4], 0);

        return new Location(world, x, y, z, yaw, pitch);
    }

    @NotNull
    public static List<Location> deserialize(@NotNull final Collection<String> list) {
        return new ArrayList<>(list.stream().map(LocationUtil::deserialize).filter(Objects::nonNull).toList());
    }

    @NotNull
    public static String getWorldName(@NotNull final Location location) {
        final World world = location.getWorld();
        return world == null ? "null" : world.getName();
    }

    @NotNull
    public static Location getCenter(@NotNull final Location location) { return LocationUtil.getCenter(location, true); }

    @NotNull
    public static Location getCenter(@NotNull final Location location, final boolean doVertical) {
        // Location centered = location.clone();
        location.setX(location.getBlockX() + 0.5);
        location.setY(location.getBlockY() + (doVertical ? 0.5 : 0));
        location.setZ(location.getBlockZ() + 0.5);
        return location;
    }

    @NotNull
    public static Vector getDirection(@NotNull final Location from, @NotNull final Location to) {
        final Location origin = from.clone();
        origin.setDirection(to.toVector().subtract(origin.toVector()));
        return origin.getDirection();
    }
}
