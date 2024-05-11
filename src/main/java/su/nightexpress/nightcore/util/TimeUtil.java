package su.nightexpress.nightcore.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.core.CoreLang;

public class TimeUtil {

    @NotNull
    public static String formatTime(final long time) {
        //@formatter:off
        final long days        = TimeUnit.MILLISECONDS.toDays(time);
        final long hours       = TimeUnit.MILLISECONDS.toHours(time)   % 24;
        final long minutes     = TimeUnit.MILLISECONDS.toMinutes(time) % 60;
        final long seconds     = TimeUnit.MILLISECONDS.toSeconds(time) % 60;
        final String delimiter = CoreLang.TIME_DELIMITER.getString();
        //@formatter:on

        final StringBuilder str = new StringBuilder();
        if (days > 0) {
            if (!str.isEmpty()) {
                str.append(delimiter);
            }
            str.append(CoreLang.TIME_DAY.getString().replace(Placeholders.GENERIC_AMOUNT, String.valueOf(days)));
        }
        if (hours > 0) {
            if (!str.isEmpty()) {
                str.append(delimiter);
            }
            str.append(CoreLang.TIME_HOUR.getString().replace(Placeholders.GENERIC_AMOUNT, String.valueOf(hours)));
        }
        if (minutes > 0) {
            if (!str.isEmpty()) {
                str.append(delimiter);
            }
            str.append(CoreLang.TIME_MINUTE.getString().replace(Placeholders.GENERIC_AMOUNT, String.valueOf(minutes)));
        }
        if (str.isEmpty() || seconds > 0) {
            if (!str.isEmpty()) {
                str.append(delimiter);
            }
            str.append(CoreLang.TIME_SECOND.getString().replace(Placeholders.GENERIC_AMOUNT, String.valueOf(seconds)));
        }

        return str.toString();
    }

    @NotNull
    public static String formatDuration(final long from, final long to) {
        final long time = to - from;
        return TimeUtil.formatTime(time);
    }

    @NotNull
    public static String formatDuration(final long until) { return TimeUtil.formatTime(until - System.currentTimeMillis()); }

    @NotNull
    public static LocalTime getLocalTimeOf(final long ms) {
        final long hours = TimeUnit.MILLISECONDS.toHours(ms) % 24;
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60;
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60;

        return LocalTime.of((int) hours, (int) minutes, (int) seconds);
    }

    @NotNull
    public static LocalDateTime getLocalDateTimeOf(final long ms) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), TimeZone.getDefault().toZoneId());
    }

    public static long toEpochMillis(@NotNull final LocalDateTime dateTime) {
        final Instant instant = dateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant();
        return instant.toEpochMilli();
    }
}
