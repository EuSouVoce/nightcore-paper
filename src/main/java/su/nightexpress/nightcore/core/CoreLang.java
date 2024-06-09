package su.nightexpress.nightcore.core;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import su.nightexpress.nightcore.dialog.Dialog;
import su.nightexpress.nightcore.language.entry.LangItem;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.text.tag.Tags;

public class CoreLang {

    public static final LangString COMMAND_ARGUMENT_FORMAT_REQUIRED = LangString.of("Command.Argument.Type.Required",
            Tags.LIGHT_RED.enclose("<" + Placeholders.GENERIC_NAME + ">"));

    public static final LangString COMMAND_ARGUMENT_FORMAT_OPTIONAL = LangString.of("Command.Argument.Type.Optional",
            Tags.LIGHT_YELLOW.enclose("[" + Placeholders.GENERIC_NAME + "]"));

    public static final LangString COMMAND_FLAG_FORMAT = LangString.of("Command.Flag.Format",
            Tags.LIGHT_GRAY.enclose("[" + Placeholders.GENERIC_NAME + "]"));

    public static final LangString COMMAND_ARGUMENT_NAME_GENERIC = LangString.of("Command.Argument.Name.Generic", "value");
    public static final LangString COMMAND_ARGUMENT_NAME_TYPE = LangString.of("Command.Argument.Name.Type", "type");
    public static final LangString COMMAND_ARGUMENT_NAME_NAME = LangString.of("Command.Argument.Name.Name", "name");
    public static final LangString COMMAND_ARGUMENT_NAME_PLAYER = LangString.of("Command.Argument.Name.Player", "player");
    public static final LangString COMMAND_ARGUMENT_NAME_WORLD = LangString.of("Command.Argument.Name.World", "world");
    public static final LangString COMMAND_ARGUMENT_NAME_AMOUNT = LangString.of("Command.Argument.Name.Amount", "amount");
    public static final LangString COMMAND_ARGUMENT_NAME_MATERIAL = LangString.of("Command.Argument.Name.Material", "material");
    public static final LangString COMMAND_ARGUMENT_NAME_ITEM_MATERIAL = LangString.of("Command.Argument.Name.ItemMaterial", "item type");
    public static final LangString COMMAND_ARGUMENT_NAME_BLOCK_MATERIAL = LangString.of("Command.Argument.Name.BlockMaterial",
            "block type");
    public static final LangString COMMAND_ARGUMENT_NAME_ENCHANTMENT = LangString.of("Command.Argument.Name.Enchantment", "enchantment");
    // public static final LangString COMMAND_ARGUMENT_NAME_POTION_EFFECT =
    // LangString.of("Command.Argument.Name.Effect", "effect");
    // public static final LangString COMMAND_ARGUMENT_NAME_ATTRIBUTE =
    // LangString.of("Command.Argument.Name.Attribute", "attribute");

    public static final LangText COMMAND_HELP_LIST = LangText.of("Command.Help.List", Placeholders.TAG_NO_PREFIX, " ",
            "  " + Tags.YELLOW.enclose(Tags.BOLD.enclose(Placeholders.GENERIC_NAME)) + Tags.GRAY.enclose(" - ")
                    + Tags.YELLOW.enclose(Tags.BOLD.enclose("Commands:")),
            " ", Tags.GRAY.enclose("  " + Tags.RED.enclose(Tags.BOLD.enclose("<>")) + " - Required, "
                    + Tags.GREEN.enclose(Tags.BOLD.enclose("[]")) + " - Optional."),
            " ", Placeholders.GENERIC_ENTRY, " ");

    public static final LangString COMMAND_HELP_ENTRY = LangString.of("Command.Help.Entry",
            "  " + Tags.YELLOW.enclose("/" + Placeholders.COMMAND_LABEL) + " " + Tags.ORANGE.enclose(Placeholders.COMMAND_USAGE)
                    + Tags.GRAY.enclose(" - " + Placeholders.COMMAND_DESCRIPTION));

    public static final LangString COMMAND_HELP_DESC = LangString.of("Command.Help.Desc", "Show help page.");

    public static final LangString COMMAND_CHECKPERM_DESC = LangString.of("Command.CheckPerm.Desc", "Print player permission info.");

    public static final LangString COMMAND_RELOAD_DESC = LangString.of("Command.Reload.Desc", "Reload the plugin.");

    public static final LangText COMMAND_RELOAD_DONE = LangText.of("Command.Reload.Done",
            Tags.LIGHT_GRAY.enclose("Plugin " + Tags.LIGHT_GREEN.enclose("reloaded") + "!"));

    public static final LangString TIME_DAY = LangString.of("Time.Day", Placeholders.GENERIC_AMOUNT + "d.");
    public static final LangString TIME_HOUR = LangString.of("Time.Hour", Placeholders.GENERIC_AMOUNT + "h.");
    public static final LangString TIME_MINUTE = LangString.of("Time.Min", Placeholders.GENERIC_AMOUNT + "min.");
    public static final LangString TIME_SECOND = LangString.of("Time.Sec", Placeholders.GENERIC_AMOUNT + "sec.");
    public static final LangString TIME_DELIMITER = LangString.of("Time.Delimiter", " ");

    public static final LangString OTHER_YES = LangString.of("Other.Yes", Tags.GREEN.enclose("Yes"));
    public static final LangString OTHER_NO = LangString.of("Other.No", Tags.RED.enclose("No"));
    public static final LangString OTHER_ENABLED = LangString.of("Other.Enabled", Tags.GREEN.enclose("Enabled"));
    public static final LangString OTHER_DISABLED = LangString.of("Other.Disabled", Tags.RED.enclose("Disabled"));
    public static final LangString OTHER_ANY = LangString.of("Other.Any", "Any");
    public static final LangString OTHER_NONE = LangString.of("Other.None", "None");
    public static final LangString OTHER_NEVER = LangString.of("Other.Never", "Never");
    public static final LangString OTHER_ONE_TIMED = LangString.of("Other.OneTimed", "One-Timed");
    public static final LangString OTHER_UNLIMITED = LangString.of("Other.Unlimited", "Unlimited");
    public static final LangString OTHER_INFINITY = LangString.of("Other.Infinity", "∞");

    public static final LangString ENTRY_GOOD = LangString.of("Entry.Good",
            Tags.GREEN.enclose("✔") + " " + Tags.GRAY.enclose(Placeholders.GENERIC_ENTRY));
    public static final LangString ENTRY_BAD = LangString.of("Entry.Bad",
            Tags.RED.enclose("✘") + " " + Tags.GRAY.enclose(Placeholders.GENERIC_ENTRY));
    public static final LangString ENTRY_WARN = LangString.of("Entry.Warn",
            Tags.ORANGE.enclose("[❗]") + " " + Tags.GRAY.enclose(Placeholders.GENERIC_ENTRY));

    public static final LangText ERROR_INVALID_PLAYER = LangText.of("Error.Invalid_Player", Tags.RED.enclose("Invalid player!"));

    public static final LangText ERROR_INVALID_WORLD = LangText.of("Error.Invalid_World", Tags.RED.enclose("Invalid world!"));

    public static final LangText ERROR_INVALID_NUMBER = LangText.of("Error.Invalid_Number", Tags.RED.enclose("Invalid number!"));

    public static final LangText ERROR_INVALID_MATERIAL = LangText.of("Error.InvalidMaterial", Tags.RED.enclose("Invalid material!"));

    public static final LangText ERROR_INVALID_ENCHANTMENT = LangText.of("Error.InvalidEnchantment",
            Tags.RED.enclose("Invalid enchantment!"));

    /*
     * public static final LangText ERROR_INVALID_POTION_EFFECT =
     * LangText.of("Error.InvalidPotionEffectType",
     * LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) +
     * " is not a valid potion effect type!")); public static final LangText
     * ERROR_INVALID_ATTRIBUTE = LangText.of("Error.InvalidAttribute",
     * LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) +
     * " is not a valid attribute!"));
     */

    public static final LangText ERROR_NO_PERMISSION = LangText.of("Error.NoPermission",
            Tags.RED.enclose("You don't have permissions to do that!"));

    public static final LangText ERROR_COMMAND_PARSE_FLAG = LangText.of("Error.Command.ParseFlag", Tags.LIGHT_GRAY.enclose("Invalid value "
            + Tags.LIGHT_RED.enclose(Placeholders.GENERIC_VALUE) + " for " + Tags.LIGHT_RED.enclose(Placeholders.GENERIC_NAME) + " flag."));

    public static final LangText ERROR_COMMAND_PARSE_ARGUMENT = LangText.of("Error.Command.ParseArgument",
            Tags.LIGHT_GRAY.enclose("Invalid value " + Tags.LIGHT_RED.enclose(Placeholders.GENERIC_VALUE) + " for "
                    + Tags.LIGHT_RED.enclose(Placeholders.GENERIC_NAME) + " argument."));

    public static final LangText ERROR_COMMAND_INVALID_PLAYER_ARGUMENT = LangText.of("Error.Command.Argument.InvalidPlayer",
            Tags.LIGHT_GRAY.enclose(Tags.LIGHT_RED.enclose("Can not find player " + Placeholders.GENERIC_VALUE) + "!"));

    public static final LangText ERROR_COMMAND_INVALID_WORLD_ARGUMENT = LangText.of("Error.Command.Argument.InvalidWorld",
            Tags.LIGHT_GRAY.enclose(Tags.LIGHT_RED.enclose("Can not find world " + Placeholders.GENERIC_VALUE) + "!"));

    public static final LangText ERROR_COMMAND_INVALID_NUMBER_ARGUMENT = LangText.of("Error.Command.Argument.InvalidNumber",
            Tags.LIGHT_GRAY.enclose(Tags.LIGHT_RED.enclose(Placeholders.GENERIC_VALUE) + " is not a valid number!"));

    public static final LangText ERROR_COMMAND_INVALID_MATERIAL_ARGUMENT = LangText.of("Error.Command.Argument.InvalidMaterial",
            Tags.LIGHT_GRAY.enclose(Tags.LIGHT_RED.enclose(Placeholders.GENERIC_VALUE) + " is not a valid material!"));

    public static final LangText ERROR_COMMAND_INVALID_ENCHANTMENT_ARGUMENT = LangText.of("Error.Command.Argument.InvalidEnchantment",
            Tags.LIGHT_GRAY.enclose(Tags.LIGHT_RED.enclose(Placeholders.GENERIC_VALUE) + " is not a valid enchantment!"));

    public static final LangText ERROR_COMMAND_NOT_YOURSELF = LangText.of("Error.Command.NotYourself",
            Tags.RED.enclose("This command can not be used on yourself."));

    public static final LangText ERROR_COMMAND_PLAYER_ONLY = LangText.of("Error.Command.PlayerOnly",
            Tags.RED.enclose("This command is for players only."));

    public static final LangText ERROR_COMMAND_USAGE = LangText.of("Error.Command.Usage", Placeholders.TAG_NO_PREFIX, " ",
            Tags.RED.enclose("Error: ") + Tags.GRAY.enclose("Wrong arguments!"), Tags.RED.enclose("Usage: ")
                    + Tags.YELLOW.enclose("/" + Placeholders.COMMAND_LABEL) + " " + Tags.ORANGE.enclose(Placeholders.COMMAND_USAGE),
            " ");

    public static final LangText EDITOR_ACTION_EXIT = LangText.of("Editor.Action.Exit", Placeholders.TAG_NO_PREFIX, "",
            Tags.GRAY.enclose("Click "
                    + Tags.CLICK.enclose(Tags.HOVER.enclose(Tags.GREEN.enclose("[Here]"), HoverEvent.Action.SHOW_TEXT,
                            Tags.GRAY.enclose("Click to cancel")), ClickEvent.Action.RUN_COMMAND, "/" + Dialog.EXIT)
                    + " to leave input mode."),
            "");

    public static final LangString EDITOR_INPUT_HEADER_MAIN = LangString.of("Editor.Input.Header.Main",
            Tags.GREEN.enclose(Tags.BOLD.enclose("Input Mode")));
    public static final LangString EDITOR_INPUT_HEADER_ERROR = LangString.of("Editor.Input.Header.Error",
            Tags.RED.enclose(Tags.BOLD.enclose("ERROR")));
    public static final LangString EDITOR_INPUT_ERROR_NOT_INTEGER = LangString.of("Editor.Input.Error.NotInteger",
            Tags.GRAY.enclose("Expecting " + Tags.RED.enclose("whole") + " number!"));
    public static final LangString EDITOR_INPUT_ERROR_GENERIC = LangString.of("Editor.Input.Error.Generic",
            Tags.GRAY.enclose("Invalid value!"));

    public static final LangItem EDITOR_ITEM_CLOSE = LangItem.of("Editor.Generic.Close", Tags.LIGHT_RED.enclose(Tags.BOLD.enclose("Exit")));
    public static final LangItem EDITOR_ITEM_RETURN = LangItem.of("Editor.Generic.Return",
            Tags.LIGHT_GRAY.enclose(Tags.BOLD.enclose("Return")));
    public static final LangItem EDITOR_ITEM_NEXT_PAGE = LangItem.of("Editor.Generic.NextPage", Tags.LIGHT_GRAY.enclose("Next Page →"));
    public static final LangItem EDITOR_ITEM_PREVIOUS_PAGE = LangItem.of("Editor.Generic.PreviousPage",
            Tags.LIGHT_GRAY.enclose("← Previous Page"));

    public static final LangString NUMBER_SHORT_THOUSAND = LangString.of("Number.Thousand", "k");
    public static final LangString NUMBER_SHORT_MILLION = LangString.of("Number.Million", "m");
    public static final LangString NUMBER_SHORT_BILLION = LangString.of("Number.Billion", "b");
    public static final LangString NUMBER_SHORT_TRILLION = LangString.of("Number.Trillion", "t");
    public static final LangString NUMBER_SHORT_QUADRILLION = LangString.of("Number.Quadrillion", "q");

    @NotNull
    public static String getYesOrNo(final boolean value) { return (value ? CoreLang.OTHER_YES : CoreLang.OTHER_NO).getString(); }

    @NotNull
    public static String getEnabledOrDisabled(final boolean value) {
        return (value ? CoreLang.OTHER_ENABLED : CoreLang.OTHER_DISABLED).getString();
    }

    @NotNull
    public static String goodEntry(@NotNull final String str) {
        return CoreLang.ENTRY_GOOD.getString().replace(Placeholders.GENERIC_ENTRY, str);
    }

    @NotNull
    public static String badEntry(@NotNull final String str) {
        return CoreLang.ENTRY_BAD.getString().replace(Placeholders.GENERIC_ENTRY, str);
    }

    @NotNull
    public static String warnEntry(@NotNull final String str) {
        return CoreLang.ENTRY_WARN.getString().replace(Placeholders.GENERIC_ENTRY, str);
    }
}
