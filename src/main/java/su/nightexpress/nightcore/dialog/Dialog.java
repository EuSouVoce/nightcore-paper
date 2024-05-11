package su.nightexpress.nightcore.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.chat.ClickEvent;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.menu.api.Menu;
import su.nightexpress.nightcore.menu.impl.AbstractMenu;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.WrappedMessage;
import su.nightexpress.nightcore.util.text.tag.Tags;

public class Dialog {

    private static final Map<UUID, Dialog> DIALOG_MAP = new ConcurrentHashMap<>();

    public static final String EXIT = "#exit";
    public static final String VALUES = "#values";

    private final Player player;
    private final DialogHandler handler;

    private Menu lastMenu;
    private List<String> suggestions;

    public Dialog(@NotNull final Player player, @NotNull final DialogHandler handler) {
        this.player = player;
        this.handler = handler;
        this.suggestions = new ArrayList<>();
    }

    @NotNull
    public Player getPlayer() { return this.player; }

    @NotNull
    public DialogHandler getHandler() { return this.handler; }

    @Nullable
    public Menu getLastMenu() { return this.lastMenu; }

    @NotNull
    public Dialog setLastMenu(@Nullable final Menu lastMenu) {
        this.lastMenu = lastMenu;
        return this;
    }

    @NotNull
    public List<String> getSuggestions() { return this.suggestions; }

    @NotNull
    public Dialog setSuggestions(@NotNull final Collection<String> suggestions, final boolean autoRun) {
        this.suggestions = suggestions.stream().sorted(String::compareTo).collect(Collectors.toCollection(ArrayList::new));
        this.displaySuggestions(autoRun, 1);
        return this;
    }

    public void displaySuggestions(final boolean autoRun, int page) {
        final List<String> values = this.getSuggestions();
        if (values.isEmpty())
            return;

        final int perPage = 10;
        final int pages = (int) Math.ceil((double) values.size() / (double) perPage);
        if (page < 1)
            page = 1;
        else if (page > pages)
            page = pages;
        final int skip = (page - 1) * perPage;

        final boolean isLastPage = page == pages;
        final boolean isFirstPage = page == 1;
        final List<String> items = values.stream().skip(skip).limit(perPage).toList();
        final ClickEvent.Action action = autoRun ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;

        final NightMessage.Builder builder = NightMessage.builder();
        builder.append("=".repeat(8) + "[ ", Tags.ORANGE);
        builder.append("Value Helper", Tags.YELLOW);
        builder.append("] " + "=".repeat(8), Tags.ORANGE);
        builder.appendLineBreak();

        items.forEach(element -> {
            final String value = element.charAt(0) == '/' ? element : '/' + element;
            builder.append("> ", Tags.DARK_GRAY);
            builder.append(element, Tags.GREEN).showText(Tags.GRAY.enclose("Click me to select " + Tags.CYAN.enclose(element) + "."))
                    .clickEvent(action, value);
            builder.appendLineBreak();
        });

        builder.append("=".repeat(9) + " ", Tags.ORANGE);

        if (isFirstPage) {
            builder.append("[<]", Tags.GRAY);
        } else {
            builder.append("[<]", Tags.LIGHT_RED).showText(Tags.GRAY.enclose("Previous Page"))
                    .runCommand("/" + Dialog.VALUES + " " + (page - 1) + " " + autoRun);
        }

        builder.append(" " + page, Tags.YELLOW);
        builder.append("/", Tags.ORANGE);
        builder.append(pages + " ", Tags.YELLOW);

        if (isLastPage) {
            builder.append("[>]", Tags.GRAY);
        } else {
            builder.append("[>]", Tags.LIGHT_RED).showText(Tags.GRAY.enclose("Next Page"))
                    .runCommand("/" + Dialog.VALUES + " " + (page + 1) + " " + autoRun);
        }

        builder.append(" " + "=".repeat(9), Tags.ORANGE);
        builder.send(this.getPlayer());
    }

    @Deprecated
    public void prompt(@NotNull final String text) {
        this.sendInfo(CoreLang.EDITOR_INPUT_HEADER_MAIN.getMessage().toLegacy(), Colorizer.apply(text));
    }

    public void prompt(@NotNull final WrappedMessage text) { this.info(CoreLang.EDITOR_INPUT_HEADER_MAIN.getMessage(), text); }

    @Deprecated
    public void error(@NotNull final String text) {
        this.sendInfo(CoreLang.EDITOR_INPUT_HEADER_ERROR.getMessage().toLegacy(), Colorizer.apply(text));
    }

    public void error(@NotNull final WrappedMessage text) { this.info(CoreLang.EDITOR_INPUT_HEADER_ERROR.getMessage(), text); }

    public void info(@NotNull final WrappedMessage title, @NotNull final WrappedMessage text) {
        this.sendInfo(title.toLegacy(), text.toLegacy());
    }

    @Deprecated
    public void info(@NotNull final String title, @NotNull final String text) {
        this.sendInfo(Colorizer.apply(title), Colorizer.apply(text));
    }

    @SuppressWarnings("deprecation")
    private void sendInfo(@NotNull final String title, @NotNull final String text) {
        this.getPlayer().sendTitle(title, text, 20, Short.MAX_VALUE, 20);
    }

    @NotNull
    public static Dialog create(@NotNull final Player player, @NotNull final DialogHandler handler) {
        final Dialog dialog = new Dialog(player, handler).setLastMenu(AbstractMenu.getMenu(player));

        Dialog.DIALOG_MAP.put(player.getUniqueId(), dialog);
        CoreLang.EDITOR_ACTION_EXIT.getMessage().send(player);
        return dialog;
    }

    @Nullable
    public static Dialog get(@NotNull final Player player) { return Dialog.DIALOG_MAP.get(player.getUniqueId()); }

    @SuppressWarnings("deprecation")
    public static void stop(@NotNull final Player player) {
        final Dialog dialog = Dialog.DIALOG_MAP.remove(player.getUniqueId());
        if (dialog == null)
            return;

        final Menu menu = dialog.getLastMenu();
        if (menu != null) {
            menu.open(player);
        }
        player.sendTitle("", "", 1, 1, 1);
    }

    public static void shutdown() { Dialog.DIALOG_MAP.clear(); }

    public static boolean contains(@NotNull final Player player) { return Dialog.get(player) != null; }
}
