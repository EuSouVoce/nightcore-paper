package su.nightexpress.nightcore.language.tag.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.language.message.MessageOptions;
import su.nightexpress.nightcore.language.message.OutputType;
import su.nightexpress.nightcore.language.tag.MessageTag;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.StringUtil;

public class OutputTag extends MessageTag {

    public OutputTag() { super("output"); }

    @NotNull
    public String enclose(@NotNull final OutputType type) {
        final String prefix = type.name().toLowerCase();

        return this.enclose(prefix);
    }

    @NotNull
    public String enclose(final int fade, final int stay) {
        final String prefix = OutputType.TITLES.name().toLowerCase();
        final String content = prefix + ":" + fade + ":" + stay + ":" + fade;

        return this.enclose(content);
    }

    @Override
    public void apply(@NotNull final MessageOptions options, @Nullable final String tagContent) {
        if (tagContent == null)
            return;

        final String[] split = tagContent.split(":");
        final OutputType outputType = StringUtil.getEnum(split[0], OutputType.class).orElse(OutputType.CHAT);

        options.setOutputType(outputType);
        if (outputType == OutputType.TITLES) {
            final int[] titleTimes = new int[3];
            if (split.length >= 4) {
                titleTimes[0] = NumberUtil.getInteger(split[1]);
                titleTimes[1] = NumberUtil.getAnyInteger(split[2], -1);
                titleTimes[2] = NumberUtil.getInteger(split[3]);
            }

            if (titleTimes[1] < 0)
                titleTimes[1] = Short.MAX_VALUE;

            options.setTitleTimes(titleTimes);
        }
    }
}
