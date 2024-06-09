package su.nightexpress.nightcore.dialog;

import static su.nightexpress.nightcore.util.text.tag.Tags.CLICK;
import static su.nightexpress.nightcore.util.text.tag.Tags.CYAN;
import static su.nightexpress.nightcore.util.text.tag.Tags.DARK_GRAY;
import static su.nightexpress.nightcore.util.text.tag.Tags.GRAY;
import static su.nightexpress.nightcore.util.text.tag.Tags.GREEN;
import static su.nightexpress.nightcore.util.text.tag.Tags.HOVER;
import static su.nightexpress.nightcore.util.text.tag.Tags.LIGHT_RED;
import static su.nightexpress.nightcore.util.text.tag.Tags.ORANGE;
import static su.nightexpress.nightcore.util.text.tag.Tags.YELLOW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.chat.ClickEvent;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.menu.api.Menu;
import su.nightexpress.nightcore.menu.impl.AbstractMenu;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.TextRoot;
import su.nightexpress.nightcore.util.text.tag.Tags;

public class Dialog {

    private static final Map<UUID, Dialog> DIALOG_MAP = new ConcurrentHashMap<>();
    private static final int DEFAULT_TIMEOUT = 60;

    public static final String EXIT = "#exit";
    public static final String VALUES = "#values";

    private final Player player;
    private final DialogHandler handler;

    private Menu lastMenu;
    private List<String> suggestions;
    private long timeoutDate;

    public Dialog(@NotNull final Player player, @NotNull final DialogHandler handler) {
        this.player = player;
        this.handler = handler;
        this.suggestions = new ArrayList<>();
        this.setTimeout(Dialog.DEFAULT_TIMEOUT);
    }

    public boolean isTimedOut() { return this.timeoutDate > 0 && System.currentTimeMillis() >= this.timeoutDate; }

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

    public long getTimeoutDate() { return this.timeoutDate; }

    public void setTimeout(final int timeout) {
        this.timeoutDate = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout, TimeUnit.SECONDS);
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

        final StringBuilder builder = new StringBuilder()
                .append(Tags.ORANGE.enclose("=".repeat(8) + "[ " + Tags.YELLOW.enclose("Value Helper") + " ]" + "=".repeat(8)))
                .append(Placeholders.TAG_LINE_BREAK);

        items.forEach(element -> {
            final String hoverHint = Tags.GRAY.enclose("Click me to select " + Tags.CYAN.enclose(element) + ".");
            final String clickCommand = element.charAt(0) == '/' ? element : '/' + element;

            builder.append(Tags.DARK_GRAY.enclose("> "))
                    .append(Tags.GREEN.enclose(Tags.HOVER.encloseHint(Tags.CLICK.encloseRun(element, clickCommand), hoverHint)));
            builder.append(Placeholders.TAG_LINE_BREAK);
        });

        builder.append(Tags.ORANGE.enclose("=".repeat(9))).append(" ");

        if (isFirstPage) {
            builder.append(Tags.GRAY.enclose("[<]"));
        } else {
            builder.append(Tags.LIGHT_RED.enclose(Tags.HOVER.encloseHint(Tags.CLICK.encloseRun("[<]", "/" + Dialog.VALUES + " " + (page - 1) + " " + autoRun),
                    Tags.GRAY.enclose("Previous Page"))));
        }

        builder.append(Tags.YELLOW.enclose(" " + page));
        builder.append(Tags.ORANGE.enclose("/"));
        builder.append(Tags.YELLOW.enclose(pages + " "));

        if (isLastPage) {
            builder.append(Tags.GRAY.enclose("[>]"));
        } else {
            builder.append(Tags.LIGHT_RED.enclose(Tags.HOVER.encloseHint(Tags.CLICK.encloseRun("[>]", "/" + Dialog.VALUES + " " + (page + 1) + " " + autoRun),
                    Tags.GRAY.enclose("Next Page"))));

        }

        builder.append(Tags.ORANGE.enclose(" " + "=".repeat(9)));

        Players.sendModernMessage(this.player, builder.toString());
    }

    @Deprecated
    public void prompt(@NotNull final String text) {
        this.sendInfo(CoreLang.EDITOR_INPUT_HEADER_MAIN.getMessage().toLegacy(), NightMessage.asLegacy(text));
    }

    public void prompt(@NotNull final TextRoot text) { this.info(CoreLang.EDITOR_INPUT_HEADER_MAIN.getMessage(), text); }

    @Deprecated
    public void error(@NotNull final String text) {
        this.sendInfo(CoreLang.EDITOR_INPUT_HEADER_ERROR.getMessage().toLegacy(), NightMessage.asLegacy(text));
    }

    public void error(@NotNull final TextRoot text) { this.info(CoreLang.EDITOR_INPUT_HEADER_ERROR.getMessage(), text); }

    public void info(@NotNull final TextRoot title, @NotNull final TextRoot text) { this.sendInfo(title.toLegacy(), text.toLegacy()); }

    @Deprecated
    public void info(@NotNull final String title, @NotNull final String text) {
        this.sendInfo(NightMessage.asLegacy(title), NightMessage.asLegacy(text));
    }

    private void sendInfo(@NotNull final String title, @NotNull final String text) {
        this.getPlayer().sendTitle(title, text, 20, Short.MAX_VALUE, 20);
    }

    public static void checkTimeOut() {
        new HashSet<>(Dialog.DIALOG_MAP.values()).forEach(dialog -> {
            if (dialog.isTimedOut()) {
                Dialog.stop(dialog.player);
            }
        });
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
