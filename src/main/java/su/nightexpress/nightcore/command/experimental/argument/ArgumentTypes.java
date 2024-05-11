package su.nightexpress.nightcore.command.experimental.argument;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;

public class ArgumentTypes {

    public static final Function<String, String> STRING = string -> string;
    public static final Function<String, Boolean> BOOLEAN = Boolean::parseBoolean;
    public static final Function<String, Integer> INTEGER = string -> NumberUtil.parseInteger(string).orElse(null);
    public static final Function<String, Integer> INTEGER_ABS = string -> NumberUtil.parseInteger(string).map(Math::abs).orElse(null);
    public static final Function<String, Double> DOUBLE = string -> NumberUtil.parseDouble(string).orElse(null);
    public static final Function<String, Double> DOUBLE_ABS = string -> NumberUtil.parseDouble(string).map(Math::abs).orElse(null);
    public static final Function<String, Player> PLAYER = Players::getPlayer;
    public static final Function<String, World> WORLD = Bukkit::getWorld;
    public static final Function<String, Material> MATERIAL = BukkitThing::getMaterial;
    public static final Function<String, Material> ITEM_MATERIAL = string -> {
        final Material material = BukkitThing.getMaterial(string);
        return material == null || !material.isItem() ? null : material;
    };
    public static final Function<String, Material> BLOCK_MATERIAL = string -> {
        final Material material = BukkitThing.getMaterial(string);
        return material == null || !material.isBlock() ? null : material;
    };
    public static final Function<String, Enchantment> ENCHANTMENT = BukkitThing::getEnchantment;
    // public static final Function<String, PotionEffectType> POTION_EFFECT =
    // BukkitThing::getPotionEffect;
    // public static final Function<String, Attribute> ATTRIBUTE =
    // BukkitThing::getAttribute;

    @NotNull
    public static ArgumentBuilder<String> string(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.STRING).localized(CoreLang.COMMAND_ARGUMENT_NAME_GENERIC);
    }

    @NotNull
    public static ArgumentBuilder<Boolean> bool(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.BOOLEAN).localized(CoreLang.COMMAND_ARGUMENT_NAME_GENERIC);
    }

    @NotNull
    public static ArgumentBuilder<Integer> integer(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.INTEGER).localized(CoreLang.COMMAND_ARGUMENT_NAME_GENERIC)
                .customFailure(CoreLang.ERROR_COMMAND_INVALID_NUMBER_ARGUMENT);
    }

    @NotNull
    public static ArgumentBuilder<Integer> integerAbs(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.INTEGER_ABS).localized(CoreLang.COMMAND_ARGUMENT_NAME_GENERIC)
                .customFailure(CoreLang.ERROR_COMMAND_INVALID_NUMBER_ARGUMENT);
    }

    @NotNull
    public static ArgumentBuilder<Double> decimal(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.DOUBLE).localized(CoreLang.COMMAND_ARGUMENT_NAME_GENERIC)
                .customFailure(CoreLang.ERROR_COMMAND_INVALID_NUMBER_ARGUMENT);
    }

    @NotNull
    public static ArgumentBuilder<Double> decimalAbs(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.DOUBLE_ABS).localized(CoreLang.COMMAND_ARGUMENT_NAME_GENERIC)
                .customFailure(CoreLang.ERROR_COMMAND_INVALID_NUMBER_ARGUMENT);
    }

    @NotNull
    public static ArgumentBuilder<Player> player(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.PLAYER).localized(CoreLang.COMMAND_ARGUMENT_NAME_PLAYER)
                .customFailure(CoreLang.ERROR_COMMAND_INVALID_PLAYER_ARGUMENT)
                .withSamples(tabContext -> Players.playerNames(tabContext.getPlayer()));
    }

    @NotNull
    public static ArgumentBuilder<String> playerName(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.STRING).localized(CoreLang.COMMAND_ARGUMENT_NAME_PLAYER)
                .withSamples(tabContext -> Players.playerNames(tabContext.getPlayer()));
    }

    @NotNull
    public static ArgumentBuilder<World> world(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.WORLD).localized(CoreLang.COMMAND_ARGUMENT_NAME_WORLD)
                .customFailure(CoreLang.ERROR_COMMAND_INVALID_WORLD_ARGUMENT).withSamples(tabContext -> Lists.worldNames());
    }

    @NotNull
    public static ArgumentBuilder<Material> material(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.MATERIAL).localized(CoreLang.COMMAND_ARGUMENT_NAME_MATERIAL)
                .customFailure(CoreLang.ERROR_COMMAND_INVALID_MATERIAL_ARGUMENT);
    }

    @NotNull
    public static ArgumentBuilder<Material> itemMaterial(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.ITEM_MATERIAL).localized(CoreLang.COMMAND_ARGUMENT_NAME_ITEM_MATERIAL)
                .customFailure(CoreLang.ERROR_COMMAND_INVALID_MATERIAL_ARGUMENT);
    }

    @NotNull
    public static ArgumentBuilder<Material> blockMaterial(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.BLOCK_MATERIAL).localized(CoreLang.COMMAND_ARGUMENT_NAME_BLOCK_MATERIAL)
                .customFailure(CoreLang.ERROR_COMMAND_INVALID_MATERIAL_ARGUMENT);
    }

    @NotNull
    public static ArgumentBuilder<Enchantment> enchantment(@NotNull final String name) {
        return CommandArgument.builder(name, ArgumentTypes.ENCHANTMENT).localized(CoreLang.COMMAND_ARGUMENT_NAME_ENCHANTMENT)
                .customFailure(CoreLang.ERROR_COMMAND_INVALID_ENCHANTMENT_ARGUMENT);
    }

    /*
     * @NotNull public static ArgumentBuilder<PotionEffectType>
     * potionEffect(@NotNull String name) { return CommandArgument.builder(name,
     * POTION_EFFECT) .localized(CoreLang.COMMAND_ARGUMENT_NAME_POTION_EFFECT)
     * .customFailure(CoreLang.ERROR_INVALID_POTION_EFFECT); }
     * @NotNull public static ArgumentBuilder<Attribute> attribute(@NotNull String
     * name) { return CommandArgument.builder(name, ATTRIBUTE)
     * .localized(CoreLang.COMMAND_ARGUMENT_NAME_ATTRIBUTE)
     * .customFailure(CoreLang.ERROR_INVALID_ATTRIBUTE); }
     */
}
