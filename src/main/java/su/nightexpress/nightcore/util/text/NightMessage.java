package su.nightexpress.nightcore.util.text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import su.nightexpress.nightcore.core.CoreConfig;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.Pair;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.regex.TimedMatcher;
import su.nightexpress.nightcore.util.text.decoration.Decorator;
import su.nightexpress.nightcore.util.text.decoration.DecoratorParser;
import su.nightexpress.nightcore.util.text.decoration.GradientDecorator;
import su.nightexpress.nightcore.util.text.decoration.ParsedDecorator;
import su.nightexpress.nightcore.util.text.tag.TagPool;
import su.nightexpress.nightcore.util.text.tag.Tags;
import su.nightexpress.nightcore.util.text.tag.api.DynamicTag;
import su.nightexpress.nightcore.util.text.tag.api.OrphanTag;
import su.nightexpress.nightcore.util.text.tag.api.Tag;
import su.nightexpress.nightcore.util.text.tag.impl.GradientTag;

public class NightMessage {

    private static final Map<String, Tag> TAG_MAP = new HashMap<>();
    //@formatter:off
    static {
        NightMessage.registerTags(
            Tags.BLACK, Tags.WHITE, Tags.GRAY, Tags.GREEN,
            Tags.YELLOW, Tags.ORANGE, Tags.RED,
            Tags.BLUE, Tags.CYAN, Tags.PURPLE, Tags.PINK,

            Tags.DARK_GRAY, Tags.LIGHT_GRAY, Tags.LIGHT_GREEN,
            Tags.LIGHT_YELLOW, Tags.LIGHT_ORANGE, Tags.LIGHT_RED,
            Tags.LIGHT_BLUE, Tags.LIGHT_CYAN, Tags.LIGHT_PURPLE, Tags.LIGHT_PINK
        );

        NightMessage.registerTags(Tags.BOLD, Tags.ITALIC, Tags.OBFUSCATED, Tags.STRIKETHROUGH, Tags.UNDERLINED);

        NightMessage.registerTags(
            Tags.FONT, Tags.HEX_COLOR, Tags.HEX_COLOR_SHORT, Tags.GRADIENT,
            Tags.HOVER, Tags.CLICK,
            Tags.LINE_BREAK, Tags.RESET,
            Tags.TRANSLATE
        );
    }
    //@formatter:on
    @NotNull
    public static Collection<Tag> getTags() { return NightMessage.TAG_MAP.values(); }

    public static void registerTags(@NotNull final Tag... tags) {
        for (final Tag tag : tags) {
            NightMessage.registerTag(tag);
        }
    }

    public static void registerTag(@NotNull final Tag tag) {
        NightMessage.TAG_MAP.put(tag.getName(), tag);
        for (final String alias : tag.getAliases()) {
            NightMessage.TAG_MAP.put(alias, tag);
        }
    }

    @Nullable
    public static Tag getTag(@NotNull final String name) { return NightMessage.TAG_MAP.get(name.toLowerCase()); }

    @NotNull
    public static String clean(@NotNull final String string) { return NightMessage.create(string, TagPool.NONE).toLegacy(); }

    @NotNull
    public static String asJson(@NotNull final String string) { return NightMessage.create(string).toJson(); }

    @NotNull
    public static String asLegacy(@NotNull final String string) { return NightMessage.create(string).toLegacy(); }

    public static BaseComponent asComponent(@NotNull final String string) { return NightMessage.create(string).toComponent(); }

    @NotNull
    public static List<String> asLegacy(@NotNull final List<String> string) {
        final List<String> list = new ArrayList<>();
        for (final String str : string) {
            for (final String br : str.split(Placeholders.TAG_LINE_BREAK)) {
                list.add(NightMessage.asLegacy(br));
            }
        }
        return list;
    }

    /**
     * @param string Text to deserialize.
     * @return Precompiled WrappedMessage object.
     */
    @NotNull
    public static WrappedMessage create(@NotNull final String string) { return NightMessage.create(string, TagPool.ALL); }

    @NotNull
    public static WrappedMessage from(@NotNull final String string) { return NightMessage.from(string, TagPool.ALL); }

    @NotNull
    public static WrappedMessage create(@NotNull final String string, @NotNull final TagPool tagPool) {
        return NightMessage.from(string, tagPool).compile();
    }

    @NotNull
    public static WrappedMessage from(@NotNull final String string, @NotNull final TagPool tagPool) {
        return new WrappedMessage(string, tagPool);
    }

    public static BaseComponent[] parse(@NotNull final String string) { return NightMessage.parse(string, TagPool.ALL); }

    public static BaseComponent[] parse(@NotNull String string, @NotNull final TagPool tagPool) {
        if (CoreConfig.LEGACY_COLOR_SUPPORT.get()) {
            final TimedMatcher timedMatcher = TimedMatcher.create(Colorizer.PATTERN_HEX_LEGACY, string);
            final Set<String> rawCodes = new HashSet<>();
            while (timedMatcher.find()) {
                final String hex = timedMatcher.getMatcher().group(1);
                rawCodes.add(hex);
            }
            for (final String hex : rawCodes) {
                string = string.replace(hex, Tag.brackets(hex));
            }
        }

        final List<WrappedText> parts = new ArrayList<>();

        WrappedText currentText = new WrappedText();
        parts.add(currentText);

        Tag lastTag = null;

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

                boolean closeTag = false;
                if (next == Tag.CLOSE_MARK) {
                    closeTag = true;
                    index++;
                }

                String leading = string.substring(index + 1);
                final String brackets = string.substring(index + 1, indexEnd);

                Tag tag = NightMessage.getTag(brackets);
                Decorator decorator = null;
                // System.out.println("brackets = " + brackets);

                if (tag == null) {
                    for (final Tag registered : NightMessage.getTags()) {
                        final String tagName = registered.getName();
                        if (leading.startsWith(tagName)) {
                            final char latest = leading.charAt(tagName.length());

                            if (latest == Tag.CLOSE_BRACKET || registered instanceof DecoratorParser) {
                                tag = registered;
                                break;
                            }
                        }
                    }
                }
                // System.out.println("found tag = " + tag);

                // if (tag == null) break Tag;
                /*
                 * if (tag != null && !tagPool.isGoodTag(tag)) { break Tag; }
                 */

                if (!closeTag) {
                    // If tag is variable like hex codes or hover/click events.
                    if (tag instanceof final DecoratorParser parser) {
                        // If tag has no dynamic content, strip it to the next close bracket.
                        if (!(tag instanceof DynamicTag)) {
                            leading = brackets;
                        }
                        // System.out.println("tag = " + tag.getName());
                        // System.out.println("content = " + leading);
                        final ParsedDecorator parsedDecorator = parser.parse(leading);
                        if (parsedDecorator != null) {
                            decorator = parsedDecorator.getDecorator();
                            indexEnd = index + parsedDecorator.getLength() + 1; // 1 for close bracket
                        }
                    } else if (tag instanceof final Decorator textDecorator) {
                        decorator = textDecorator;
                    }
                }

                if (tag != null) {
                    if (tagPool.isGoodTag(tag)) {
                        if (tag instanceof final OrphanTag orphanTag) {
                            if (currentText.getTextBuilder().isEmpty()) {
                                currentText.getTextBuilder().append(orphanTag.getContent());
                            } else {
                                currentText = currentText.nested();
                                currentText.setText(orphanTag.getContent());
                                parts.add(currentText);
                            }
                            currentText = currentText.nested();
                            parts.add(currentText);
                        }

                        // Создаем новую "часть текста" только если обнаружен новый тег после текста (но
                        // не других тегов).
                        if (lastTag == null && !currentText.getTextBuilder().isEmpty()) {
                            currentText = currentText.nested();
                            parts.add(currentText);
                        }

                        if (closeTag) {
                            currentText.removeLatestTag(tag);
                        } else if (!(tag instanceof OrphanTag)) {
                            currentText.addTag(tag, decorator);
                        }

                        lastTag = tag;
                    }
                    index = indexEnd;
                    continue;
                }

                // Move cursor back to '/' of invalid closing tag.
                if (closeTag) {
                    index--;
                }
            }
            currentText.getTextBuilder().append(letter);
            lastTag = null;
        }

        // System.out.println("parsedTexts = " + parts);

        parts.removeIf(wrappedText -> wrappedText.getText().isEmpty());

        NightMessage.fixGradients(parts);

        final ComponentBuilder builder = new ComponentBuilder();
        for (final WrappedText wrappedText : parts) {
            if (CoreConfig.LEGACY_COLOR_SUPPORT.get()) {
                wrappedText.setText(Colorizer.apply(wrappedText.getText()));
            }
            builder.append(wrappedText.toComponent(), ComponentBuilder.FormatRetention.NONE);
        }

        return builder.create();
    }

    // Fix gradient smoothness when multiple WrappedTexts have the same gradient
    // colors, but different tag lists.
    private static void fixGradients(@NotNull final List<WrappedText> parts) {
        if (parts.size() < 2)
            return;

        final List<Pair<GradientDecorator, List<WrappedText>>> pairs = new ArrayList<>();

        Pair<GradientDecorator, List<WrappedText>> currentList = null;
        GradientDecorator lastGradient = null;
        int lastIndex = -1;

        // Create a list of sequental parts with the same gradient.
        for (int index = 0; index < parts.size(); index++) {
            final WrappedText text = parts.get(index);
            final GradientDecorator gradient = (GradientDecorator) text.getDecorator(GradientTag.NAME);

            // Check for next list
            if (gradient != null) {
                final boolean bigStep = (index - lastIndex) > 1;
                final boolean sameGradient = lastGradient != null && gradient.isSimilar(lastGradient);
                final boolean lastElement = (index == parts.size() - 1);
                if (bigStep || !sameGradient || lastElement) {
                    if (currentList != null && currentList.getSecond().size() > 1) {
                        pairs.add(currentList);
                    }
                    currentList = Pair.of(gradient, new ArrayList<>());
                }
                currentList.getSecond().add(text);
            } else if (currentList != null) {
                pairs.add(currentList);
                currentList = null;
            }

            lastGradient = gradient;
            lastIndex = index;
        }
        // System.out.println("pairs = " + pairs);

        // Recreate Gradient Decorator with shifted colors depends on text length.
        for (final var pair : pairs) {
            final GradientDecorator gradient = pair.getFirst();
            final List<WrappedText> texts = pair.getSecond();

            final int length = texts.stream().mapToInt(text -> text.getText().length()).sum();

            final Color[] colors = gradient.createGradient(length);
            int shift = 0;

            for (final WrappedText text : texts) {
                if (shift >= colors.length)
                    break;

                final int textLength = text.getText().length();

                final Color start = colors[shift];
                final Color end = colors[textLength + shift - 1];

                final GradientDecorator decorator = new GradientDecorator(start, end);
                text.getDecoratorMap().put(GradientTag.NAME, decorator);

                shift += textLength;
            }
        }
    }

    @NotNull
    public static Builder builder() { return new Builder(); }

    public static class Builder {

        private final List<WrappedText> texts;

        public Builder() { this.texts = new ArrayList<>(); }

        public BaseComponent[] create() {
            final ComponentBuilder builder = new ComponentBuilder();
            for (final WrappedText wrappedText : this.texts) {
                builder.append(wrappedText.toComponent(), ComponentBuilder.FormatRetention.NONE);
            }
            return builder.create();
        }

        public net.kyori.adventure.text.@NotNull Component serialize(final BaseComponent[] text) {
            return BungeeComponentSerializer.get().deserialize(text);
        }

        @SuppressWarnings("deprecation")
        public void send(@NotNull final CommandSender sender) { sender.spigot().sendMessage(this.create()); }

        public Builder append(@NotNull final WrappedText.Builder textBuilder) {
            this.texts.add(textBuilder.getText());
            return this;
        }

        public WrappedText.Builder append(@NotNull final String text, @NotNull final Tag... tags) {
            final WrappedText.Builder builder = WrappedText.builder(text);
            this.texts.add(builder.getText());
            for (final Tag tag : tags) {
                builder.tag(tag);
            }
            return builder;
        }

        public Builder appendLineBreak() { return this.append(WrappedText.builder("\n")); }
    }
}
