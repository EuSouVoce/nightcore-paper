package su.nightexpress.nightcore.language;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCore;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.StringUtil;

public class LangAssets {

    private static FileConfig config;

    public static void load() {
        final NightCore core = Plugins.CORE;
        final String langCode = core.getLanguage();

        final String assetsCode = LangAssets.downloadAssets(core, langCode);
        LangAssets.config = FileConfig.loadOrExtract(core, LangManager.DIR_LANG, LangAssets.getFileName(assetsCode));
    }

    @NotNull
    private static String downloadAssets(@NotNull final NightCore plugin, @NotNull final String langCode) {
        final File file = new File(plugin.getDataFolder().getAbsolutePath() + LangManager.DIR_LANG, LangAssets.getFileName(langCode));
        if (file.exists())
            return langCode;

        FileUtil.create(file);

        final String url = Placeholders.GITHUB_URL + "/raw/master/assets/" + langCode + ".yml";
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            plugin.info("Downloading '" + langCode + "' assets from github...");
            final byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            return langCode;
        } catch (final IOException exception) {
            // exception.printStackTrace();
            plugin.error("Could not download language assets for '" + langCode + "' (no such assets?).");
            return LangManager.isDefault(langCode) ? langCode : LangAssets.downloadAssets(plugin, LangManager.DEFAULT_LANGUAGE);
        }
    }

    @NotNull
    private static String getFileName(@NotNull final String langCode) { return "assets_" + langCode + ".yml"; }

    @NotNull
    public static FileConfig getConfig() { return LangAssets.config; }

    @NotNull
    public static String get(@NotNull final PotionEffectType type) { return LangAssets.getAsset("PotionEffectType", type); }

    @NotNull
    public static String get(@NotNull final EntityType type) { return LangAssets.getAsset("EntityType", type); }

    @NotNull
    public static String get(@NotNull final Material type) { return LangAssets.getAsset("Material", type); }

    @NotNull
    public static String get(@NotNull final World world) { return LangAssets.getOrCreate("World", world); }

    @NotNull
    public static String get(@NotNull final Enchantment enchantment) { return LangAssets.getOrCreate("Enchantment", enchantment); }

    @NotNull
    public static String getAsset(@NotNull final String path, @NotNull final Keyed keyed) {
        return LangAssets.getAsset(path, BukkitThing.toString(keyed));
    }

    @NotNull
    public static String getAsset(@NotNull final String path, @NotNull final String nameRaw) {
        return LangAssets.getAsset(path + "." + nameRaw).orElse(nameRaw);
    }

    @NotNull
    public static Optional<String> getAsset(@NotNull final String path) {
        return Optional.ofNullable(LangAssets.config.getString(path));// .map(NightMessage::asLegacy);
    }

    @NotNull
    public static String getOrCreate(@NotNull final String path, @NotNull final Keyed keyed) {
        return LangAssets.getOrCreate(path, BukkitThing.toString(keyed));
    }

    @NotNull
    public static String getOrCreate(@NotNull final String path, @NotNull final String nameRaw) {
        LangAssets.config.addMissing(path + "." + nameRaw, StringUtil.capitalizeUnderscored(nameRaw));
        LangAssets.config.saveChanges();

        return LangAssets.getAsset(path, nameRaw);
    }
}
