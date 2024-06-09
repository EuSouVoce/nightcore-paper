package su.nightexpress.nightcore.util.text.tag.impl;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.util.text.tag.api.ContentTag;
import su.nightexpress.nightcore.util.text.tag.api.Tag;
import su.nightexpress.nightcore.util.text.tag.decorator.BaseColorDecorator;

public class ShortHexColorTag extends Tag implements ContentTag {

    public static final String NAME = "#";

    public ShortHexColorTag() { super(ShortHexColorTag.NAME); }

    @Override
    @Nullable
    public BaseColorDecorator parse(@NotNull final String tagContent) {
        if (tagContent.length() < 7)
            return null;

        try {
            final Color color = Color.decode(tagContent);
            return new BaseColorDecorator(color);
        } catch (final NumberFormatException exception) {
            return null;
        }
    }
}
