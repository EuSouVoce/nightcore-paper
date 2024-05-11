package su.nightexpress.nightcore.integration;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import su.nightexpress.nightcore.util.Plugins;

public class VaultHook {

    private static Economy economy;
    private static Permission permission;
    private static Chat chat;

    public static void setup() {
        VaultHook.setPermission();
        VaultHook.setEconomy();
        VaultHook.setChat();
    }

    public static void shutdown() {
        VaultHook.economy = null;
        VaultHook.permission = null;
        VaultHook.chat = null;
    }

    @Nullable
    private static <T> T getProvider(@NotNull final Class<T> clazz) {
        final RegisteredServiceProvider<T> rsp = Bukkit.getServer().getServicesManager().getRegistration(clazz);
        return rsp == null ? null : rsp.getProvider();
    }

    private static void setPermission() {
        VaultHook.permission = VaultHook.getProvider(Permission.class);
        if (VaultHook.permission != null) {
            Plugins.CORE.info("Found permissions provider: " + VaultHook.permission.getName());
        }
    }

    private static void setEconomy() {
        VaultHook.economy = VaultHook.getProvider(Economy.class);
        if (VaultHook.economy != null) {
            Plugins.CORE.info("Found economy provider: " + VaultHook.economy.getName());
        }
    }

    private static void setChat() {
        VaultHook.chat = VaultHook.getProvider(Chat.class);
        if (VaultHook.chat != null) {
            Plugins.CORE.info("Found chat provider: " + VaultHook.chat.getName());
        }
    }

    public static void onServiceRegisterEvent(@NotNull final ServiceRegisterEvent event) {
        final Object provider = event.getProvider().getProvider();

        if (VaultHook.economy == null && provider instanceof Economy) {
            VaultHook.setEconomy();
        } else if (VaultHook.permission == null && provider instanceof Permission) {
            VaultHook.setPermission();
        } else if (VaultHook.chat == null && provider instanceof Chat) {
            VaultHook.setChat();
        }
    }

    public static boolean hasPermissions() { return VaultHook.getPermissions() != null; }

    @Nullable
    public static Permission getPermissions() { return VaultHook.permission; }

    public static boolean hasChat() { return VaultHook.getChat() != null; }

    @Nullable
    public static Chat getChat() { return VaultHook.chat; }

    public static boolean hasEconomy() { return VaultHook.getEconomy() != null; }

    @Nullable
    public static Economy getEconomy() { return VaultHook.economy; }

    @NotNull
    public static String getEconomyName() { return VaultHook.hasEconomy() ? VaultHook.economy.getName() : "null"; }

    @NotNull
    public static String getPermissionGroup(@NotNull final Player player) {
        if (!VaultHook.hasPermissions() || !VaultHook.permission.hasGroupSupport())
            return "";

        final String group = VaultHook.permission.getPrimaryGroup(player);
        return group == null ? "" : group.toLowerCase();
    }

    @NotNull
    public static Set<String> getPermissionGroups(@NotNull final Player player) {
        if (!VaultHook.hasPermissions() || !VaultHook.permission.hasGroupSupport())
            return Collections.emptySet();

        String[] groups = VaultHook.permission.getPlayerGroups(player);
        if (groups == null)
            groups = new String[] { VaultHook.getPermissionGroup(player) };

        return Stream.of(groups).map(String::toLowerCase).collect(Collectors.toSet());
    }

    @NotNull
    public static String getPrefix(@NotNull final Player player) {
        return VaultHook.hasChat() ? VaultHook.chat.getPlayerPrefix(player) : "";
    }

    @NotNull
    public static String getSuffix(@NotNull final Player player) {
        return VaultHook.hasChat() ? VaultHook.chat.getPlayerSuffix(player) : "";
    }

    public static double getBalance(@NotNull final Player player) { return VaultHook.economy.getBalance(player); }

    public static double getBalance(@NotNull final OfflinePlayer player) { return VaultHook.economy.getBalance(player); }

    public static boolean addMoney(@NotNull final Player player, final double amount) {
        return VaultHook.addMoney((OfflinePlayer) player, amount);
    }

    public static boolean addMoney(@NotNull final OfflinePlayer player, final double amount) {
        return VaultHook.economy.depositPlayer(player, amount).transactionSuccess();
    }

    public static boolean takeMoney(@NotNull final Player player, final double amount) {
        return VaultHook.takeMoney((OfflinePlayer) player, amount);
    }

    public static boolean takeMoney(@NotNull final OfflinePlayer player, final double amount) {
        return VaultHook.economy.withdrawPlayer(player, Math.abs(amount)).transactionSuccess();
    }
}
