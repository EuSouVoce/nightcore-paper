package su.nightexpress.nightcore.util;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import su.nightexpress.nightcore.core.CoreConfig;
import su.nightexpress.nightcore.integration.VaultHook;
import su.nightexpress.nightcore.util.message.NexParser;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.WrappedMessage;

@SuppressWarnings("deprecation")
public class Players {

    public static final String PLAYER_COMMAND_PREFIX = "player:";

    @NotNull
    public static List<String> playerNames() { return Players.playerNames(null); }

    @NotNull
    public static List<String> playerNames(@Nullable final Player viewer) { return Players.playerNames(viewer, true); }

    @NotNull
    public static List<String> realPlayerNames() { return Players.realPlayerNames(null); }

    @NotNull
    public static List<String> realPlayerNames(@Nullable final Player viewer) { return Players.playerNames(viewer, false); }

    @NotNull
    public static List<String> playerNames(@Nullable final Player viewer, final boolean includeCustom) {
        final Set<String> names = new HashSet<>();
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (viewer != null && !viewer.canSee(player))
                continue;

            names.add(player.getName());
            if (includeCustom && CoreConfig.RESPECT_PLAYER_DISPLAYNAME.get()) {
                names.add(Colorizer.strip(player.getDisplayName()));
            }
        }
        return names.stream().sorted(String::compareTo).toList();
    }

    @NotNull
    public static Optional<Player> find(@NotNull final String nameOrNick) { return Optional.ofNullable(Players.getPlayer(nameOrNick)); }

    @Nullable
    public static Player getPlayer(@NotNull final String nameOrNick) {
        if (!CoreConfig.RESPECT_PLAYER_DISPLAYNAME.get()) {
            return Bukkit.getServer().getPlayer(nameOrNick);
        }

        Player found = Bukkit.getServer().getPlayerExact(nameOrNick);
        if (found != null) {
            return found;
        }

        final String lowerName = nameOrNick.toLowerCase();
        final int lowerLength = lowerName.length();
        int delta = Integer.MAX_VALUE;

        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            final String nameReal = player.getName().toLowerCase(Locale.ENGLISH);
            final String nameCustom = player.getDisplayName().toLowerCase();

            int length;
            if (nameReal.startsWith(lowerName)) {
                length = player.getName().length();
            } else if (nameCustom.startsWith(lowerName)) {
                length = player.getDisplayName().length();
            } else
                continue;

            final int curDelta = Math.abs(length - lowerLength);
            if (curDelta < delta) {
                found = player;
                delta = curDelta;
            }

            if (curDelta == 0)
                break;
        }

        return found;
    }

    public static boolean isBedrock(@NotNull final Player player) {
        return Plugins.hasFloodgate() && FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }

    public static boolean isReal(@NotNull final Player player) { return Bukkit.getServer().getPlayer(player.getUniqueId()) != null; }

    @NotNull
    public static String getPermissionGroup(@NotNull final Player player) {
        return Plugins.hasVault() && VaultHook.hasPermissions() ? VaultHook.getPermissionGroup(player).toLowerCase() : Placeholders.DEFAULT;
    }

    @NotNull
    public static Set<String> getPermissionGroups(@NotNull final Player player) {
        return Plugins.hasVault() && VaultHook.hasPermissions() ? VaultHook.getPermissionGroups(player) : Set.of(Placeholders.DEFAULT);
    }

    @NotNull
    public static String getPrefix(@NotNull final Player player) { return Plugins.hasVault() ? VaultHook.getPrefix(player) : ""; }

    @NotNull
    public static String getSuffix(@NotNull final Player player) { return Plugins.hasVault() ? VaultHook.getSuffix(player) : ""; }

    @Deprecated
    public static void sendRichMessage(@NotNull final CommandSender sender, @NotNull final String message) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Colorizer.apply(NexParser.toPlainText(message)));
            return;
        }
        NexParser.toMessage(message).send(sender);
    }

    public static void sendModernMessage(@NotNull final CommandSender sender, @NotNull final String message) {
        NightMessage.create(message).send(sender);
    }

    @Deprecated
    public static void sendActionBar(@NotNull final Player player, @NotNull final String msg) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, NexParser.toMessage(msg).build());
    }

    public static void sendActionBarText(@NotNull final Player player, @NotNull final String message) {
        Players.sendActionBar(player, NightMessage.create(message));
    }

    public static void sendActionBar(@NotNull final Player player, @NotNull final WrappedMessage message) {
        player.sendActionBar(BungeeComponentSerializer.get().deserialize(message.parseIfAbsent()));
    }

    public static void dispatchCommands(@NotNull final Player player, @NotNull final String... commands) {
        for (final String command : commands) {
            Players.dispatchCommand(player, command);
        }
    }

    public static void dispatchCommands(@NotNull final Player player, @NotNull final List<String> commands) {
        for (final String command : commands) {
            Players.dispatchCommand(player, command);
        }
    }

    public static void dispatchCommand(@NotNull final Player player, @NotNull String command) {
        CommandSender sender = Bukkit.getConsoleSender();
        if (command.startsWith(Players.PLAYER_COMMAND_PREFIX)) {
            command = command.substring(Players.PLAYER_COMMAND_PREFIX.length());
            sender = player;
        }

        command = Placeholders.forPlayer(player).apply(command).trim();

        if (Plugins.hasPlaceholderAPI()) {
            command = PlaceholderAPI.setPlaceholders(player, command);
        }
        Bukkit.dispatchCommand(sender, command);
    }

    public static boolean hasEmptyInventory(@NotNull final Player player) {
        return Stream.of(player.getInventory().getContents()).allMatch(item -> item == null || item.getType().isAir());
    }

    public static boolean hasEmptyContents(@NotNull final Player player) {
        return Stream.of(player.getInventory().getContents()).allMatch(item -> item == null || item.getType().isAir());
    }

    public static int countItemSpace(@NotNull final Player player, @NotNull final ItemStack item) {
        final int stackSize = item.getType().getMaxStackSize();
        return Stream.of(player.getInventory().getContents()).mapToInt(itemHas -> {
            if (itemHas == null || itemHas.getType().isAir()) {
                return stackSize;
            }
            if (itemHas.isSimilar(item)) {
                return (stackSize - itemHas.getAmount());
            }
            return 0;
        }).sum();
    }

    public static int countItem(@NotNull final Player player, @NotNull final Predicate<ItemStack> predicate) {
        return Stream.of(player.getInventory().getContents()).filter(item -> item != null && predicate.test(item))
                .mapToInt(ItemStack::getAmount).sum();
    }

    public static int countItem(@NotNull final Player player, @NotNull final ItemStack item) {
        return Players.countItem(player, item::isSimilar);
    }

    public static int countItem(@NotNull final Player player, @NotNull final Material material) {
        return Players.countItem(player, itemHas -> itemHas.getType() == material);
    }

    public static void takeItem(@NotNull final Player player, @NotNull final ItemStack item) { Players.takeItem(player, item, -1); }

    public static void takeItem(@NotNull final Player player, @NotNull final ItemStack item, final int amount) {
        Players.takeItem(player, itemHas -> itemHas.isSimilar(item), amount);
    }

    public static void takeItem(@NotNull final Player player, @NotNull final Material material) { Players.takeItem(player, material, -1); }

    public static void takeItem(@NotNull final Player player, @NotNull final Material material, final int amount) {
        Players.takeItem(player, itemHas -> itemHas.getType() == material, amount);
    }

    public static void takeItem(@NotNull final Player player, @NotNull final Predicate<ItemStack> predicate) {
        Players.takeItem(player, predicate, -1);
    }

    public static void takeItem(@NotNull final Player player, @NotNull final Predicate<ItemStack> predicate, final int amount) {
        int takenAmount = 0;

        final Inventory inventory = player.getInventory();
        for (final ItemStack itemHas : inventory.getContents()) {
            if (itemHas == null || !predicate.test(itemHas))
                continue;

            if (amount < 0) {
                itemHas.setAmount(0);
                continue;
            }

            final int hasAmount = itemHas.getAmount();
            if (takenAmount + hasAmount > amount) {
                final int diff = (takenAmount + hasAmount) - amount;
                itemHas.setAmount(diff);
                break;
            }

            itemHas.setAmount(0);
            if ((takenAmount += hasAmount) == amount) {
                break;
            }
        }
    }

    public static void addItem(@NotNull final Player player, @NotNull final ItemStack... items) {
        for (final ItemStack item : items) {
            Players.addItem(player, item, item.getAmount());
        }
    }

    public static void addItem(@NotNull final Player player, @NotNull final ItemStack item2, int amount) {
        if (amount <= 0 || item2.getType().isAir())
            return;

        final World world = player.getWorld();
        final ItemStack item = new ItemStack(item2);

        final int realAmount = Math.min(item.getMaxStackSize(), amount);
        item.setAmount(realAmount);
        player.getInventory().addItem(item).values().forEach(left -> {
            world.dropItem(player.getLocation(), left);
        });

        amount -= realAmount;
        if (amount > 0)
            Players.addItem(player, item2, amount);
    }
}
