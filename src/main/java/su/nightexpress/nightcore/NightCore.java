package su.nightexpress.nightcore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import su.nightexpress.nightcore.command.experimental.ImprovedCommands;
import su.nightexpress.nightcore.command.experimental.impl.ReloadCommand;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.core.CoreConfig;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.core.CoreManager;
import su.nightexpress.nightcore.core.CorePerms;
import su.nightexpress.nightcore.core.command.CheckPermCommand;
import su.nightexpress.nightcore.dialog.Dialog;
import su.nightexpress.nightcore.integration.VaultHook;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.ItemNbt;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.blocktracker.PlayerBlockTracker;

public class NightCore extends NightPlugin implements ImprovedCommands {

    private final Set<NightCorePlugin> childrens;
    private final CoreManager coreManager;

    public NightCore() {
        this.childrens = new HashSet<>();
        this.coreManager = new CoreManager(this);
    }

    @Override
    public void enable() {
        LangAssets.load();

        final ChainedNode rootNode = this.getRootNode();

        if (Plugins.hasVault()) {
            VaultHook.setup();
            CheckPermCommand.inject(this, rootNode);
        }
        ReloadCommand.inject(this, rootNode, CorePerms.COMMAND_RELOAD);

        this.testMethods();
        this.coreManager.setup();
    }

    @Override
    public void disable() {
        this.coreManager.shutdown();

        Dialog.shutdown();
        if (Plugins.hasVault()) {
            VaultHook.shutdown();
        }
        PlayerBlockTracker.shutdown();
    }

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("nightcore", new String[] { "nightcore", "ncore" }).setConfigClass(CoreConfig.class)
                .setLangClass(CoreLang.class).setPermissionsClass(CorePerms.class);
    }

    void addChildren(@NotNull final NightCorePlugin child) {
        this.childrens.add(child);
        child.info("Powered by " + this.getName());
    }

    @NotNull
    public Set<NightCorePlugin> getChildrens() { return new HashSet<>(this.childrens); }

    private void testMethods() {
        if (Version.getCurrent() == Version.UNKNOWN) {
            this.warn("Server Version: UNSUPPORTED ✘");
        } else
            this.info("Server Version: " + Version.getCurrent().getLocalized() + " ✔");

        if (EntityUtil.setupEntityCounter(this)) {
            this.info("Entity Id Counter: OK ✔");
        } else
            this.error("Entity Id Counter: FAIL ✘");

        if (this.testItemNbt()) {
            this.info("Item NBT Compress: OK ✔");
        } else
            this.error("Item NBT Compress: FAIL ✘");
    }

    private boolean testItemNbt() {
        if (!ItemNbt.setup(this))
            return false;

        final ItemStack testItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemUtil.editMeta(testItem, meta -> {
            meta.displayName(Component.text("Test Item"));

            final List<Component> testlore = new ArrayList<>();
            testlore.add(Component.text("Test Lore 1"));
            testlore.add(Component.text("Test Lore 2"));
            testlore.add(Component.text("Test Lore 3"));
            meta.lore(testlore);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 10, true);
        });

        final String nbt = ItemNbt.compress(testItem);
        if (nbt == null)
            return false;

        final ItemStack decompressed = ItemNbt.decompress(nbt);
        return decompressed != null && decompressed.getType() == testItem.getType();
    }
}
