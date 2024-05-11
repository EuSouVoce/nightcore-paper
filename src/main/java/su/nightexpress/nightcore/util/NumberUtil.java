package su.nightexpress.nightcore.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.core.CoreConfig;
import su.nightexpress.nightcore.core.CoreLang;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

public class NumberUtil {

    private final static TreeMap<Integer, String> ROMAN_MAP = new TreeMap<>();
    private final static TreeMap<Integer, Supplier<String>> NUMERIC_MAP = new TreeMap<>();

    static {
        NumberUtil.NUMERIC_MAP.put(0, () -> "");
        NumberUtil.NUMERIC_MAP.put(1, CoreLang.NUMBER_SHORT_THOUSAND::getString);
        NumberUtil.NUMERIC_MAP.put(2, CoreLang.NUMBER_SHORT_MILLION::getString);
        NumberUtil.NUMERIC_MAP.put(3, CoreLang.NUMBER_SHORT_BILLION::getString);
        NumberUtil.NUMERIC_MAP.put(4, CoreLang.NUMBER_SHORT_TRILLION::getString);
        NumberUtil.NUMERIC_MAP.put(5, CoreLang.NUMBER_SHORT_QUADRILLION::getString);

        NumberUtil.ROMAN_MAP.put(1000, "M");
        NumberUtil.ROMAN_MAP.put(900, "CM");
        NumberUtil.ROMAN_MAP.put(500, "D");
        NumberUtil.ROMAN_MAP.put(400, "CD");
        NumberUtil.ROMAN_MAP.put(100, "C");
        NumberUtil.ROMAN_MAP.put(90, "XC");
        NumberUtil.ROMAN_MAP.put(50, "L");
        NumberUtil.ROMAN_MAP.put(40, "XL");
        NumberUtil.ROMAN_MAP.put(10, "X");
        NumberUtil.ROMAN_MAP.put(9, "IX");
        NumberUtil.ROMAN_MAP.put(5, "V");
        NumberUtil.ROMAN_MAP.put(4, "IV");
        NumberUtil.ROMAN_MAP.put(1, "I");
    }

    @NotNull
    public static String format(final double value) { return CoreConfig.NUMBER_FORMAT.get().format(value); }

    @NotNull
    public static String compact(final double value) {
        final Pair<String, String> pair = NumberUtil.formatCompact(value);
        return pair.getFirst() + pair.getSecond();
    }

    @NotNull
    public static Pair<String, String> formatCompact(double value) {
        boolean negative = false;
        if (value < 0) {
            value = Math.abs(value);
            negative = true;
        }
        int index = 0;
        while ((value / 1000) >= 1 && index < NumberUtil.NUMERIC_MAP.size() - 1) {
            value = value / 1000;
            index++;
        }

        return Pair.of(CoreConfig.NUMBER_FORMAT.get().format(negative ? -value : value),
                NumberUtil.NUMERIC_MAP.get(NumberUtil.NUMERIC_MAP.floorKey(index)).get());
    }

    public static double round(final double value) {
        return new BigDecimal(value).setScale(2, CoreConfig.NUMBER_FORMAT.get().getRounding()).doubleValue();
    }

    @NotNull
    public static String toRoman(final int number) {
        if (number <= 0)
            return String.valueOf(number);

        final int key = NumberUtil.ROMAN_MAP.floorKey(number);
        if (number == key) {
            return NumberUtil.ROMAN_MAP.get(number);
        }
        return NumberUtil.ROMAN_MAP.get(key) + NumberUtil.toRoman(number - key);
    }

    public static int[] splitIntoParts(final int whole, final int parts) {
        final int[] arr = new int[parts];
        int remain = whole;
        int partsLeft = parts;
        for (int i = 0; partsLeft > 0; i++) {
            final int size = (remain + partsLeft - 1) / partsLeft; // rounded up, aka ceiling
            arr[i] = size;
            remain -= size;
            partsLeft--;
        }
        return arr;
    }

    public static double getDouble(@NotNull final String input) { return NumberUtil.getDouble(input, 0D); }

    public static double getDouble(@NotNull final String input, final double defaultValue) { return Math.abs(NumberUtil.getAnyDouble(input, defaultValue)); }

    public static double getAnyDouble(@NotNull final String input, final double defaultValue) { return NumberUtil.parseDouble(input).orElse(defaultValue); }

    public static int getInteger(@NotNull final String input) { return NumberUtil.getInteger(input, 0); }

    public static int getInteger(@NotNull final String input, final int defaultValue) { return Math.abs(NumberUtil.getAnyInteger(input, defaultValue)); }

    public static int getAnyInteger(@NotNull final String input, final int defaultValue) { return NumberUtil.parseInteger(input).orElse(defaultValue); }

    @NotNull
    public static Optional<Integer> parseInteger(@NotNull final String input) {
        try {
            return Optional.of(Integer.parseInt(input));
        } catch (final NumberFormatException exception) {
            return Optional.empty();
        }
    }

    @NotNull
    public static Optional<Double> parseDouble(@NotNull final String input) {
        try {
            final double amount = Double.parseDouble(input);
            if (!Double.isNaN(amount) && !Double.isInfinite(amount)) {
                return Optional.of(amount);
            }
            return Optional.empty();
        } catch (final NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public static int[] getIntArray(@NotNull final String str) {
        final String[] split = str.split(",");
        final int[] array = new int[split.length];
        for (int index = 0; index < split.length; index++) {
            try {
                array[index] = Integer.parseInt(split[index].trim());
            } catch (final NumberFormatException e) {
                array[index] = 0;
            }
        }
        return array;
    }
}
