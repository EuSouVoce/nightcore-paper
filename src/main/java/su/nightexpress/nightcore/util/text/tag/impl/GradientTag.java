package su.nightexpress.nightcore.util.text.tag.impl;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.util.text.tag.api.ContentTag;
import su.nightexpress.nightcore.util.text.tag.api.Tag;
import su.nightexpress.nightcore.util.text.tag.decorator.GradientColorDecorator;

public class GradientTag extends Tag implements ContentTag {

    public static final String NAME = "gradient";

    public GradientTag() { super(GradientTag.NAME); }

    @Override
    @Nullable
    public GradientColorDecorator parse(@NotNull final String content) {
        final String[] split = content.split(":");
        if (split.length < 2)
            return null;

        final String code1 = split[0];
        final String code2 = split[1];

        Color from;
        Color to;

        // TODO Support for named colors
        try {
            from = Color.decode(code1);
            to = Color.decode(code2);
        } catch (final NumberFormatException exception) {
            return null;
        }

        return new GradientColorDecorator(from, to);
    }
}
