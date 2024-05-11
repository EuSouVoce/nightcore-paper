package su.nightexpress.nightcore.util.regex;

import org.jetbrains.annotations.NotNull;

public class TimeoutCharSequence implements CharSequence {

    private final CharSequence chars;
    private final long timeout;
    private final long maxTime;

    public TimeoutCharSequence(@NotNull final CharSequence chars, final long timeout) {
        this.chars = chars;
        this.timeout = timeout;
        this.maxTime = (System.currentTimeMillis() + timeout);
    }

    @Override
    public char charAt(final int index) {
        if (System.currentTimeMillis() > this.maxTime) {
            throw new MatcherTimeoutException(this.chars, this.timeout);
        }
        return this.chars.charAt(index);
    }

    @Override
    public int length() { return this.chars.length(); }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return new TimeoutCharSequence(this.chars.subSequence(start, end), this.timeout);
    }

    @Override
    public String toString() { return this.chars.toString(); }
}
