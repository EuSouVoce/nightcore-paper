package su.nightexpress.nightcore.util.text.tag.impl;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.util.text.decoration.DecoratorParser;
import su.nightexpress.nightcore.util.text.decoration.GradientDecorator;
import su.nightexpress.nightcore.util.text.decoration.ParsedDecorator;
import su.nightexpress.nightcore.util.text.tag.api.Tag;

public class GradientTag extends Tag implements DecoratorParser {

    public static final String NAME = "gradient";

    public GradientTag() { super(GradientTag.NAME); }

    @Override
    public int getWeight() { return Integer.MAX_VALUE - 1; }

    @Override
    public boolean conflictsWith(@NotNull final Tag tag) {
        return super.conflictsWith(tag) || tag instanceof ColorTag || tag instanceof ShortHexColorTag || tag instanceof HexColorTag;
    }

    @Override
    @Nullable
    public ParsedDecorator parse(@NotNull String content) {
        final int tagLength = this.getName().length() + 1; // 1 for semicolon
        content = content.substring(tagLength);

        final String[] split = content.split(":");
        if (split.length < 2)
            return null;

        final String code1 = split[0];
        final String code2 = split[1];
        // if (!code1.startsWith("#")) code1 = "#" + code1;
        // if (!code2.startsWith("#")) code2 = "#" + code2;
        // if (code1.length() != 7 || code2.length() != 7) return null;

        Color from;
        Color to;

        try {
            from = Color.decode(code1);
            to = Color.decode(code2);
        } catch (final NumberFormatException exception) {
            return null;
        }

        final GradientDecorator decorator = new GradientDecorator(from, to);

        return new ParsedDecorator(decorator, 15 + tagLength); // x2 hex (7+7), +1 for semicolon between
    }
}
