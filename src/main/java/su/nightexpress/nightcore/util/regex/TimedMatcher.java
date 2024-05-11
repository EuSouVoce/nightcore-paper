package su.nightexpress.nightcore.util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.util.Plugins;

public class TimedMatcher {

    private final Matcher matcher;
    private boolean debug;

    public TimedMatcher(@NotNull final Matcher matcher) { this.matcher = matcher; }

    @NotNull
    public static TimedMatcher create(@NotNull final String pattern, @NotNull final String str) {
        return TimedMatcher.create(pattern, str, 200);
    }

    @NotNull
    public static TimedMatcher create(@NotNull final Pattern pattern, @NotNull final String str) {
        return new TimedMatcher(TimedMatcher.getMatcher(pattern, str, 200));
    }

    @NotNull
    public static TimedMatcher create(@NotNull final String rawPattern, @NotNull final String str, final long timeout) {
        return TimedMatcher.create(Pattern.compile(rawPattern), str, timeout);
    }

    @NotNull
    public static TimedMatcher create(@NotNull final Pattern pattern, @NotNull final String str, final long timeout) {
        return new TimedMatcher(TimedMatcher.getMatcher(pattern, str, timeout));
    }

    @NotNull
    private static Matcher getMatcher(@NotNull final Pattern pattern, @NotNull final String text, final long timeout) {
        if (timeout <= 0) {
            return pattern.matcher(text);
        }
        return pattern.matcher(new TimeoutCharSequence(text, timeout));
    }

    @NotNull
    public Matcher getMatcher() { return this.matcher; }

    public boolean isDebug() { return this.debug; }

    public void setDebug(final boolean debug) { this.debug = debug; }

    @NotNull
    public String replaceAll(@NotNull final String with) {
        try {
            return this.matcher.replaceAll(with);
        } catch (final MatcherTimeoutException exception) {
            if (this.isDebug()) {
                Plugins.CORE.warn("Matcher " + exception.getTimeout() + "ms timeout error for replaceAll: '"
                        + this.matcher.pattern().pattern() + "'.");
            }
            return "";
        }
    }

    public boolean matches() {
        try {
            return this.matcher.matches();
        } catch (final MatcherTimeoutException exception) {
            if (this.isDebug()) {
                Plugins.CORE
                        .warn("Matcher " + exception.getTimeout() + "ms timeout error for: '" + this.matcher.pattern().pattern() + "'.");
            }
            return false;
        }
    }

    public boolean find() {
        try {
            return this.matcher.find();
        } catch (final MatcherTimeoutException exception) {
            if (this.isDebug()) {
                Plugins.CORE
                        .warn("Matcher " + exception.getTimeout() + "ms timeout error for: '" + this.matcher.pattern().pattern() + "'.");
            }
            return false;
        }
    }
}
