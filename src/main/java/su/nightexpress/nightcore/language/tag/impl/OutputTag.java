package su.nightexpress.nightcore.language.tag.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.language.message.MessageOptions;
import su.nightexpress.nightcore.language.message.OutputType;
import su.nightexpress.nightcore.language.tag.MessageDecorator;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.text.tag.api.DynamicTag;

public class OutputTag extends DynamicTag implements MessageDecorator {

    public OutputTag() { super("output"); }

    @Override
    public int getWeight() { return 0; }

    @NotNull
    public String enclose(@NotNull final OutputType type) {
        final String prefix = type.name().toLowerCase();
        return this.leading(null, prefix);
    }

    @NotNull
    public String enclose(final int fade, final int stay) {
        final String prefix = OutputType.TITLES.name().toLowerCase();
        return this.leading(null, prefix + ":" + fade + ":" + stay + ":" + fade);
    }

    @Override
    public void apply(@NotNull final MessageOptions options, @NotNull final String content) {
        final String[] split = content.split(":");
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
