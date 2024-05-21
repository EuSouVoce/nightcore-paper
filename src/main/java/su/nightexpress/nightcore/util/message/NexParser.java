package su.nightexpress.nightcore.util.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.regex.TimedMatcher;

@Deprecated
public class NexParser {

    private static final String OPTION_PATTERN = ":(?:'|\")(.*?)(?:'|\")(?=>|\\s|$)";

    private static final Pattern PATTERN_OPTIONS = Pattern.compile("<\\?(.*?)\\?>(.*?)(?:<\\/>)+");
    private static final Map<ClickEvent.Action, Pattern> PATTERN_CLICKS = new HashMap<>();
    private static final Map<HoverEvent.Action, Pattern> PATTERN_HOVERS = new HashMap<>();
    private static final Pattern PATTERN_FONT = Pattern.compile("font" + NexParser.OPTION_PATTERN);
    private static final Pattern PATTERN_INSERTION = Pattern.compile("insertion" + NexParser.OPTION_PATTERN);

    public static final String TAG_NEWLINE = "<newline>";

    static {
        for (final ClickEvent.Action action : ClickEvent.Action.values()) {
            NexParser.PATTERN_CLICKS.put(action, Pattern.compile(action.name().toLowerCase() + NexParser.OPTION_PATTERN));
        }
        for (final HoverEvent.Action action : HoverEvent.Action.values()) {
            NexParser.PATTERN_HOVERS.put(action, Pattern.compile(action.name().toLowerCase() + NexParser.OPTION_PATTERN));
        }
    }

    public static boolean contains(@NotNull final String message) {
        final TimedMatcher matcher = TimedMatcher.create(NexParser.PATTERN_OPTIONS, message);
        return matcher.find();
    }

    @NotNull
    public static String removeFrom(@NotNull final String message) { return NexParser.strip(message, false); }

    @NotNull
    public static String toPlainText(@NotNull final String message) { return NexParser.strip(message, true); }

    @NotNull
    private static String strip(@NotNull String message, final boolean toPlain) {
        final TimedMatcher timedMatcher = TimedMatcher.create(NexParser.PATTERN_OPTIONS, message);
        while (timedMatcher.find()) {
            final Matcher matcher = timedMatcher.getMatcher();
            final String matchFull = matcher.group(0);
            if (toPlain) {
                final String matchOptions = matcher.group(1).trim();
                final String matchText = matcher.group(2);
                message = message.replace(matchFull, matchText);
            } else
                message = message.replace(matchFull, "");
        }
        return message;
    }

    public static String[] getPlainParts(@NotNull final String message) {
        // message = StringUtil.color(message.replace("\n", " "));
        return NexParser.PATTERN_OPTIONS.split(message);
    }

    public static String[] getComponentParts(@NotNull final String message) {
        final List<String> components = new ArrayList<>();

        final TimedMatcher timedMatcher = TimedMatcher.create(NexParser.PATTERN_OPTIONS, message);
        while (timedMatcher.find()) {
            components.add(timedMatcher.getMatcher().group(0));
        }

        return components.toArray(new String[0]);
    }

    @NotNull
    public static NexMessage toMessage(@NotNull String message) {
        message = Colorizer.apply(message);

        final TimedMatcher timedMatcher = TimedMatcher.create(NexParser.PATTERN_OPTIONS, message);
        final Map<String, String> parameters = new HashMap<>();
        while (timedMatcher.find()) {
            final Matcher matcher = timedMatcher.getMatcher();
            final String matchFull = matcher.group(0);
            final String matchOptions = matcher.group(1).trim();
            final String matchText = matcher.group(2);

            message = message.replace(matchFull, matchText);
            parameters.put(matchText, matchOptions);
        }

        final NexMessage nexMessage = new NexMessage(message);
        for (final Map.Entry<String, String> entry : parameters.entrySet()) {
            final String text = entry.getKey();
            final String options = entry.getValue();

            final NexComponent component = nexMessage.addComponent(text, text);
            for (final Map.Entry<ClickEvent.Action, Pattern> entryParams : NexParser.PATTERN_CLICKS.entrySet()) {
                final String paramValue = NexParser.getOption(entryParams.getValue(), options);
                if (paramValue == null)
                    continue;

                component.addClickEvent(entryParams.getKey(), paramValue);
                break;
            }
            for (final Map.Entry<HoverEvent.Action, Pattern> entryParams : NexParser.PATTERN_HOVERS.entrySet()) {
                final String paramValue = NexParser.getOption(entryParams.getValue(), options);
                if (paramValue == null)
                    continue;

                component.addHoverEvent(entryParams.getKey(), paramValue);
                break;
            }
            component.setFont(NexParser.getOption(NexParser.PATTERN_FONT, options));
            component.setInsertion(NexParser.getOption(NexParser.PATTERN_INSERTION, options));
        }

        return nexMessage;
    }

    @Nullable
    private static String getOption(@NotNull final Pattern pattern, @NotNull final String from) {
        final TimedMatcher timedMatcher = TimedMatcher.create(pattern, from);
        if (!timedMatcher.find())
            return null;

        return timedMatcher.getMatcher().group(1).stripLeading();
    }
}
