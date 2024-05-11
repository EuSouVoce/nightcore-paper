package su.nightexpress.nightcore.util.wrapper;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;

public class UniParticle {

    private final Particle particle;
    private final Object data;

    public UniParticle(@Nullable final Particle particle, @Nullable final Object data) {
        this.particle = particle;
        this.data = data;
    }

    @NotNull
    public static UniParticle of(@Nullable final Particle particle) { return UniParticle.of(particle, null); }

    @NotNull
    public static UniParticle of(@Nullable final Particle particle, @Nullable final Object data) { return new UniParticle(particle, data); }

    @NotNull
    public static UniParticle itemCrack(@NotNull final ItemStack item) { return new UniParticle(Particle.ITEM, new ItemStack(item)); }

    @NotNull
    public static UniParticle itemCrack(@NotNull final Material material) {
        return new UniParticle(Particle.ITEM, new ItemStack(material));
    }

    @NotNull
    public static UniParticle blockCrack(@NotNull final Material material) {
        return new UniParticle(Particle.BLOCK, material.createBlockData());
    }

    @NotNull
    public static UniParticle blockDust(@NotNull final Material material) {
        return new UniParticle(Particle.BLOCK, material.createBlockData());
    }

    @NotNull
    public static UniParticle blockMarker(@NotNull final Material material) {
        return new UniParticle(Particle.BLOCK_MARKER, material.createBlockData());
    }

    @NotNull
    public static UniParticle fallingDust(@NotNull final Material material) {
        return new UniParticle(Particle.FALLING_DUST, material.createBlockData());
    }

    @NotNull
    public static UniParticle redstone(@NotNull final Color color, final float size) {
        return new UniParticle(Particle.DUST, new Particle.DustOptions(color, size));
    }

    @NotNull
    public static UniParticle read(@NotNull final FileConfig cfg, @NotNull final String path) {
        final String name = cfg.getString(path + ".Name", "");
        final Particle particle = StringUtil.getEnum(name, Particle.class).orElse(null);
        if (particle == null)
            return UniParticle.of(null);

        final Class<?> dataType = particle.getDataType();
        Object data = null;
        if (dataType == BlockData.class) {
            final Material material = Material.getMaterial(cfg.getString(path + ".Material", ""));
            data = material != null ? material.createBlockData() : Material.STONE.createBlockData();
        } else if (dataType == Particle.DustOptions.class) {
            final Color color = StringUtil.getColor(cfg.getString(path + ".Color", ""));
            final double size = cfg.getDouble(path + ".Size", 1D);
            data = new Particle.DustOptions(color, (float) size);
        } else if (dataType == Particle.DustTransition.class) {
            final Color colorStart = StringUtil.getColor(cfg.getString(path + ".Color_From", ""));
            final Color colorEnd = StringUtil.getColor(cfg.getString(path + ".Color_To", ""));
            final double size = cfg.getDouble(path + ".Size", 1D);
            data = new Particle.DustTransition(colorStart, colorEnd, (float) size);
        } else if (dataType == ItemStack.class) {
            final ItemStack item = cfg.getItem(path + ".Item");
            data = item.getType().isAir() ? new ItemStack(Material.STONE) : item;
        } else if (dataType == Float.class) {
            data = (float) cfg.getDouble(path + ".floatValue", 1F);
        } else if (dataType == Integer.class) {
            data = cfg.getInt(path + ".intValue", 1);
        } else if (dataType != Void.class)
            return UniParticle.of(Particle.DUST);

        return UniParticle.of(particle, data);
    }

    public void write(@NotNull final FileConfig cfg, @NotNull final String path) {
        cfg.set(path + ".Name", this.isEmpty() ? "null" : this.getParticle().name());

        final Object data = this.getData();
        if (data instanceof final BlockData blockData) {
            cfg.set(path + ".Material", blockData.getMaterial().name());
        } else if (data instanceof final Particle.DustTransition dustTransition) {
            final Color colorStart = dustTransition.getColor();
            final Color colorEnd = dustTransition.getToColor();
            cfg.set(path + ".Color_From", colorStart.getRed() + "," + colorStart.getGreen() + "," + colorStart.getBlue());
            cfg.set(path + ".Color_To", colorEnd.getRed() + "," + colorEnd.getGreen() + "," + colorEnd.getBlue());
            cfg.set(path + ".Size", dustTransition.getSize());
        } else if (data instanceof final Particle.DustOptions dustOptions) {
            final Color color = dustOptions.getColor();
            cfg.set(path + ".Color", color.getRed() + "," + color.getGreen() + "," + color.getBlue());
            cfg.set(path + ".Size", dustOptions.getSize());
        } else if (data instanceof final ItemStack item) {
            cfg.setItem(path + ".Item", item);
        } else if (data instanceof final Float f) {
            cfg.set(path + ".floatValue", f);
        } else if (data instanceof final Integer i) {
            cfg.set(path + ".intValue", i);
        }
    }

    public boolean isEmpty() { return this.particle == null; }

    public Particle getParticle() { return this.particle; }

    @Nullable
    public Object getData() { return this.data; }

    public void play(@NotNull final Location location, final double speed, final int amount) { this.play(location, 0D, speed, amount); }

    public void play(@NotNull final Location location, final double offsetAll, final double speed, final int amount) {
        this.play(location, offsetAll, offsetAll, offsetAll, speed, amount);
    }

    public void play(@NotNull final Location location, final double xOffset, final double yOffset, final double zOffset, final double speed,
            final int amount) {
        this.play(null, location, xOffset, yOffset, zOffset, speed, amount);
    }

    public void play(@NotNull final Player player, @NotNull final Location location, final double speed, final int amount) {
        this.play(player, location, 0D, speed, amount);
    }

    public void play(@NotNull final Player player, @NotNull final Location location, final double offsetAll, final double speed,
            final int amount) {
        this.play(player, location, offsetAll, offsetAll, offsetAll, speed, amount);
    }

    public void play(@Nullable final Player player, @NotNull final Location location, final double xOffset, final double yOffset,
            final double zOffset, final double speed, final int amount) {
        if (this.isEmpty())
            return;
        if (this.particle == null || (this.particle.getDataType() != Void.class && this.data == null))
            return;

        if (player == null) {

            final World world = location.getWorld();
            if (world == null)
                return;

            world.spawnParticle(this.getParticle(), location, amount, xOffset, yOffset, zOffset, speed, this.getData());
            // EffectUtil.playParticle(location, this.getParticle(), this.getData(),
            // xOffset, yOffset, zOffset, speed, amount);
        } else {
            player.spawnParticle(this.getParticle(), location, amount, xOffset, yOffset, zOffset, speed, this.getData());
            // EffectUtil.playParticle(player, location, this.getParticle(), this.getData(),
            // xOffset, yOffset, zOffset, speed, amount);
        }
    }
}
