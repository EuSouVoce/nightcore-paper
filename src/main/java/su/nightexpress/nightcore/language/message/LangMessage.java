package su.nightexpress.nightcore.language.message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.core.CoreConfig;
import su.nightexpress.nightcore.language.tag.MessageDecorator;
import su.nightexpress.nightcore.language.tag.MessageTags;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.WrappedMessage;
import su.nightexpress.nightcore.util.text.tag.api.DynamicTag;
import su.nightexpress.nightcore.util.text.tag.api.Tag;
import su.nightexpress.nightcore.util.wrapper.UniSound;

public class LangMessage {

    private final NightCorePlugin plugin;
    private final String defaultText;
    private final MessageOptions options;
    private final WrappedMessage message;

    public LangMessage(@NotNull final NightCorePlugin plugin, @NotNull final String defaultText, @NotNull final MessageOptions options) {
        this(plugin, defaultText, options, null);
    }

    public LangMessage(@NotNull final NightCorePlugin plugin, @NotNull final String defaultText, @NotNull final MessageOptions options,
            @Nullable final String prefix) {
        this.plugin = plugin;
        this.defaultText = defaultText;
        this.options = options;
        this.message = NightMessage.from(prefix == null || !options.hasPrefix() ? defaultText : prefix + defaultText);
        if (CoreConfig.MODERN_TEXT_PRECOMPILE_LANG.get()) {
            this.message.compile();
        }
    }

    private LangMessage(@NotNull final LangMessage from) {
        this.plugin = from.plugin;
        this.defaultText = from.defaultText;
        this.options = from.options.copy();
        this.message = from.message.copy();
    }

    @NotNull
    public static LangMessage parse(@NotNull final NightCorePlugin plugin, @NotNull String string) {
        final MessageOptions options = new MessageOptions();
        final StringBuilder builder = new StringBuilder();

        // LEGACY STUFF START
        final int legTagOpen = string.indexOf("<!");
        final int legTagClose = string.indexOf("!>");
        if (legTagOpen >= 0 && legTagClose > legTagOpen) {
            final String legContent = string.substring(legTagOpen, legTagClose + 2);
            string = string.substring(legTagClose + 2);

            final String[] types = { "type", "sound", "prefix", "papi" };
            for (final String type : types) {
                final int typeIndex = legContent.indexOf(type + ":");
                if (typeIndex == -1)
                    continue;

                final String leading = legContent.substring(typeIndex + type.length() + 1);
                final String typeContent = StringUtil.parseQuotedContent(leading);
                if (typeContent == null)
                    continue;

                if (type.equalsIgnoreCase("type")) {
                    if (typeContent.equalsIgnoreCase("action_bar")) {
                        options.setOutputType(OutputType.ACTION_BAR);
                    } else if (typeContent.equalsIgnoreCase("chat")) {
                        options.setOutputType(OutputType.CHAT);
                    } else if (typeContent.equalsIgnoreCase("none")) {
                        options.setOutputType(OutputType.NONE);
                    } else if (typeContent.startsWith("titles")) {
                        final String[] split = typeContent.split(":");
                        final int fadeIn = NumberUtil.getAnyInteger(split[1], 20);
                        final int stay = NumberUtil.getAnyInteger(split[2], 60);
                        final int fadeOut = NumberUtil.getAnyInteger(split[3], 20);
                        options.setOutputType(OutputType.TITLES);
                        options.setTitleTimes(new int[] { fadeIn, stay, fadeOut });
                    }
                } else if (type.equalsIgnoreCase("sound")) {
                    StringUtil.getEnum(typeContent, Sound.class).ifPresent(options::setSound);
                } else if (type.equalsIgnoreCase("prefix")) {
                    final boolean b = Boolean.parseBoolean(typeContent);
                    options.setHasPrefix(b);
                } else if (type.equalsIgnoreCase("papi")) {
                    final boolean b = Boolean.parseBoolean(typeContent);
                    options.setUsePlaceholderAPI(b);
                }
            }
        }
        // LEGACY STUFF END

        final int length = string.length();
        for (int index = 0; index < length; index++) {
            final char letter = string.charAt(index);

            Tag: if (letter == Tag.OPEN_BRACKET && index != (length - 1)) {
                int indexEnd = string.indexOf(Tag.CLOSE_BRACKET, index);
                if (indexEnd == -1)
                    break Tag;

                final char next = string.charAt(index + 1);
                if (next == Tag.CLOSE_BRACKET)
                    break Tag;

                final String leading = string.substring(index + 1);
                final String brackets = string.substring(index + 1, indexEnd);

                Tag tag = MessageTags.getTag(brackets);

                if (tag == null) {
                    for (final Tag registered : MessageTags.getTags()) {
                        final String tagName = registered.getName();
                        if (leading.startsWith(tagName)) {
                            final char latest = leading.charAt(tagName.length());

                            if (latest == Tag.CLOSE_BRACKET || registered instanceof DynamicTag) {
                                tag = registered;
                                break;
                            }
                        }
                    }
                }
                if (!(tag instanceof final MessageDecorator decorator))
                    break Tag;

                if (tag instanceof DynamicTag) {
                    final int prefixSize = tag.getName().length() + 1; // 1 for semicolon

                    final String content = StringUtil.parseQuotedContent(leading.substring(prefixSize));
                    if (content == null)
                        break Tag;

                    decorator.apply(options, content);

                    indexEnd = index + prefixSize + content.length() + 3; // 1 for close bracket, 2 for quotes
                } else {
                    decorator.apply(options, leading);
                }

                index = indexEnd;
                continue;
            }
            builder.append(letter);
        }

        final String text = builder.toString();
        final String prefix = options.getOutputType() == OutputType.CHAT && options.hasPrefix() ? plugin.getPrefix() : null;

        // Remove completely empty lines.
        // Especially useful for message tags lines without extra text.
        final StringBuilder stripper = new StringBuilder();
        for (final String part : text.split(Placeholders.TAG_LINE_BREAK)) {
            if (part.isEmpty())
                continue;

            if (!stripper.isEmpty())
                stripper.append(Placeholders.TAG_LINE_BREAK);
            stripper.append(part);
        }
        final String strippedText = stripper.toString();

        return new LangMessage(plugin, strippedText, options, prefix);
    }

    @NotNull
    public LangMessage setPrefix(@NotNull final String prefix) {
        return new LangMessage(this.plugin, this.defaultText, this.options, prefix);
    }

    @NotNull
    public String getDefaultText() { return this.defaultText; }

    @NotNull
    public WrappedMessage getMessage() { return this.message; }

    @NotNull
    public WrappedMessage getMessage(@NotNull final CommandSender sender) {
        if (!this.options.usePlaceholderAPI() || !(sender instanceof final Player player))
            return this.getMessage();

        return this.message.copy().replace(line -> PlaceholderAPI.setPlaceholders(player, line));
    }

    @NotNull
    public LangMessage replace(@NotNull final String var, @NotNull final Object replacer) {
        return this.replace(str -> str.replace(var, String.valueOf(replacer)));
    }

    @NotNull
    public LangMessage replace(@NotNull final String var, @NotNull final Consumer<List<String>> replacer) {
        final List<String> list = new ArrayList<>();
        replacer.accept(list);

        return this.replace(var, list);
    }

    @NotNull
    public LangMessage replace(@NotNull final String var, @NotNull final List<String> replacer) {
        // String delimiter = CoreConfig.MODERN_TEXT_PRECOMPILE_LANG.get() ? "\n" :
        // StandardTags.LINE_BREAK;

        return this.replace(str -> str.replace(var, String.join(Placeholders.TAG_LINE_BREAK, replacer)));
    }

    @NotNull
    public LangMessage replace(@NotNull final UnaryOperator<String> replacer) {
        if (this.isDisabled())
            return this;

        final LangMessage copy = new LangMessage(this);
        copy.getMessage().replace(replacer);

        return copy;
    }

    /*
     * @NotNull public LangMessage replace(@NotNull Predicate<String>
     * predicate, @NotNull BiConsumer<String, List<String>> replacer) { if
     * (this.isEmpty()) return this; LangMessage msgCopy = new LangMessage(this);
     * List<String> replaced = new ArrayList<>(); List<String> text
     * msgCopy.getText().forEach(line -> { if (predicate.test(line)) {
     * replacer.accept(line, replaced); return; } replaced.add(line); });
     * msgCopy.setText(replaced); return msgCopy; }
     */

    public boolean isDisabled() { return this.options.getOutputType() == OutputType.NONE; }

    public void broadcast() {
        if (this.isDisabled())
            return;

        this.plugin.getServer().getOnlinePlayers().forEach(this::send);
        this.send(this.plugin.getServer().getConsoleSender());
    }

    public void send(@NotNull final CommandSender sender) {
        if (this.isDisabled())
            return;

        if (this.options.getSound() != null && sender instanceof final Player player) {
            UniSound.of(this.options.getSound()).play(player);
        }

        if (this.options.getOutputType() == OutputType.CHAT) {
            this.getMessage(sender).send(sender);
            return;
        }

        if (sender instanceof final Player player) {
            if (this.options.getOutputType() == OutputType.ACTION_BAR) {
                Players.sendActionBar(player, this.getMessage(player));
                // player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                // this.getMessage(player).parseIfAbsent());
            } else if (this.options.getOutputType() == OutputType.TITLES) {
                final ComponentBuilder titleBuilder = new ComponentBuilder();
                final ComponentBuilder subBuilder = new ComponentBuilder();
                ComponentBuilder current = titleBuilder;

                for (final BaseComponent component : this.getMessage(player).parseIfAbsent()) {
                    if (component instanceof final TextComponent textComponent && textComponent.getText().equals("\n")) {
                        current = subBuilder;
                        continue;
                    }
                    current.append(component);
                }

                final String title = TextComponent.toLegacyText(titleBuilder.create());
                final String subtitle = TextComponent.toLegacyText(subBuilder.create());
                player.sendTitle(title, subtitle, this.options.getTitleTimes()[0], this.options.getTitleTimes()[1],
                        this.options.getTitleTimes()[2]);
            }
        }
    }
}