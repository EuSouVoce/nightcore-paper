package su.nightexpress.nightcore.util.wrapper;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;

public class UniSound {

    private final String soundName;
    private final Sound soundType;
    private final float volume;
    private final float pitch;

    public UniSound(@NotNull final String soundName, @Nullable final Sound soundType, final float volume, final float pitch) {
        this.soundName = soundName;
        this.soundType = soundType;
        this.volume = volume;
        this.pitch = pitch;
    }

    @NotNull
    public static UniSound of(@NotNull final Sound sound) { return UniSound.of(sound, 0.8F); }

    @NotNull
    public static UniSound of(@NotNull final Sound sound, final float volume) { return UniSound.of(sound, volume, 1F); }

    @NotNull
    public static UniSound of(@NotNull final Sound sound, final float volume, final float pitch) {
        return new UniSound(sound.name(), sound, volume, pitch);
    }

    @NotNull
    public static UniSound read(@NotNull final FileConfig cfg, @NotNull final String path) {
        final String soundName = ConfigValue
                .create(path + ".Name", "null", "Sound name. You can use Spigot sound names, or ones from your resource pack.",
                        "Spigot Sounds: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html")
                .read(cfg);

        final float volume = ConfigValue.create(path + ".Volume", 0.8D, "Sound volume. From 0.0 to 1.0.").read(cfg).floatValue();

        final float pitch = ConfigValue.create(path + ".Pitch", 1D, "Sound speed. From 0.5 to 2.0").read(cfg).floatValue();

        final Sound soundType = StringUtil.getEnum(soundName, Sound.class).orElse(null);

        return new UniSound(soundName, soundType, volume, pitch);
    }

    public void write(@NotNull final FileConfig cfg, @NotNull final String path) {
        cfg.set(path + ".Name", this.getSoundName());
        cfg.set(path + ".Volume", this.getVolume());
        cfg.set(path + ".Pitch", this.getPitch());
    }

    public boolean isEmpty() { return this.getVolume() <= 0F || this.getSoundName().isEmpty(); }

    public void play(@NotNull final Player player) {
        if (this.isEmpty())
            return;

        final Location location = player.getLocation();
        if (this.getSoundType() == null) {
            player.playSound(location, this.getSoundName(), this.getVolume(), this.getPitch());
        } else {
            player.playSound(location, this.getSoundType(), this.getVolume(), this.getPitch());
        }
    }

    public void play(@NotNull final Location location) {
        if (this.isEmpty())
            return;

        final World world = location.getWorld();
        if (world == null)
            return;

        if (this.getSoundType() == null) {
            world.playSound(location, this.getSoundName(), this.getVolume(), this.getPitch());
        } else {
            world.playSound(location, this.getSoundType(), this.getVolume(), this.getPitch());
        }
    }

    @NotNull
    public String getSoundName() { return this.soundName; }

    @Nullable
    public Sound getSoundType() { return this.soundType; }

    public float getVolume() { return this.volume; }

    public float getPitch() { return this.pitch; }
}
