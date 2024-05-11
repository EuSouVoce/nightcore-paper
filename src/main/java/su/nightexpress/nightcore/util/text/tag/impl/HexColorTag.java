package su.nightexpress.nightcore.util.text.tag.impl;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.util.text.decoration.ColorDecorator;
import su.nightexpress.nightcore.util.text.decoration.DecoratorParser;
import su.nightexpress.nightcore.util.text.decoration.ParsedDecorator;
import su.nightexpress.nightcore.util.text.tag.api.Tag;

public class HexColorTag extends Tag implements DecoratorParser {

    public static final String NAME = "color";

    public HexColorTag() { super(HexColorTag.NAME); }

    @Override
    public int getWeight() { return 10; }

    @Override
    public boolean conflictsWith(@NotNull final Tag tag) { return tag instanceof ColorTag || tag instanceof HexColorTag; }

    @Override
    @Nullable
    public ParsedDecorator parse(@NotNull String content) {
        final int tagLength = this.getName().length() + 1; // 1 for semicolon
        content = content.substring(tagLength);

        try {
            final Color color = Color.decode(content);
            final ColorDecorator decorator = new ColorDecorator(color);

            return new ParsedDecorator(decorator, 7 + tagLength);
        } catch (final NumberFormatException exception) {
            return null;
        }
    }
}
