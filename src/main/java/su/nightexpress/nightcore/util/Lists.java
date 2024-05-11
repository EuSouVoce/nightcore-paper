package su.nightexpress.nightcore.util;

import org.bukkit.Bukkit;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lists {

    @NotNull
    public static List<String> worldNames() { return Bukkit.getServer().getWorlds().stream().map(WorldInfo::getName).toList(); }

    public static int indexOf(final Object[] array, @NotNull final Object objectToFind) { return Lists.indexOf(array, objectToFind, 0); }

    public static int indexOf(final Object[] array, @NotNull final Object objectToFind, int startIndex) {
        if (array == null || array.length == 0)
            return -1;
        if (!array.getClass().getComponentType().isInstance(objectToFind))
            return -1;

        if (startIndex < 0)
            startIndex = 0;

        int index;
        for (index = startIndex; index < array.length; ++index) {
            if (objectToFind.equals(array[index])) {
                return index;
            }
        }

        return -1;
    }

    public static boolean contains(final Object[] array, final Object objectToFind) { return Lists.indexOf(array, objectToFind) != -1; }

    public static int indexOf(final int[] array, final int valueToFind) { return Lists.indexOf(array, valueToFind, 0); }

    public static int indexOf(final int[] array, final int valueToFind, int startIndex) {
        if (array == null || array.length == 0)
            return -1;

        if (startIndex < 0)
            startIndex = 0;

        for (int index = startIndex; index < array.length; ++index) {
            if (valueToFind == array[index]) {
                return index;
            }
        }

        return -1;
    }

    public static boolean contains(final int[] array, final int valueToFind) { return Lists.indexOf(array, valueToFind) != -1; }

    @NotNull
    public static <T> List<List<T>> split(@NotNull final List<T> list, final int targetSize) {
        final List<List<T>> lists = new ArrayList<>();
        if (targetSize <= 0)
            return lists;

        for (int index = 0; index < list.size(); index += targetSize) {
            lists.add(list.subList(index, Math.min(index + targetSize, list.size())));
        }
        return lists;
    }

    @NotNull
    public static List<String> replace(@NotNull final List<String> origin, @NotNull final String var, final String... with) {
        return Lists.replace(origin, var, Arrays.asList(with));
    }

    @NotNull
    public static List<String> replace(@NotNull final List<String> origin, @NotNull final String var, @NotNull final List<String> with) {
        final List<String> replaced = new ArrayList<>();
        for (final String line : origin) {
            if (line.equalsIgnoreCase(var)) {
                replaced.addAll(with);
            } else
                replaced.add(line);
        }
        return replaced;
    }

    /**
     * @param original List to remove empty lines from.
     * @return A list with no multiple empty lines in a row.
     */
    @NotNull
    public static List<String> stripEmpty(@NotNull final List<String> original) {
        final List<String> stripped = new ArrayList<>();
        for (int index = 0; index < original.size(); index++) {
            final String line = original.get(index);
            if (line.isEmpty()) {
                final String last = stripped.isEmpty() ? null : stripped.get(stripped.size() - 1);
                if (last == null || last.isEmpty() || index == (original.size() - 1))
                    continue;
            }
            stripped.add(line);
        }
        return stripped;
    }

    @NotNull
    public static List<String> getSequentialMatches(@NotNull final List<String> results, @NotNull final String input) {
        final char[] chars = input.toCharArray();
        final List<String> goods = new ArrayList<>();

        Result: for (final String sub : results) {
            int lastIndex = -1;

            for (final char letter : chars) {
                final int index = sub.indexOf(letter, lastIndex == -1 ? 0 : lastIndex);
                if (index <= lastIndex) {
                    continue Result;
                }
                lastIndex = index;
            }
            goods.add(sub);
        }
        return goods;
    }

    @NotNull
    public static <K, V extends Comparable<? super V>> Map<K, V> sortAscent(@NotNull final Map<K, V> map) {
        return Lists.sort(map, Map.Entry.comparingByValue());
    }

    @NotNull
    public static <K, V extends Comparable<? super V>> Map<K, V> sortDescent(@NotNull final Map<K, V> map) {
        return Lists.sort(map, Collections.reverseOrder(Map.Entry.comparingByValue()));
    }

    @SafeVarargs
    @NotNull
    public static <T> List<T> newList(final T... values) {
        final List<T> list = new ArrayList<>();
        Collections.addAll(list, values);
        return list;
    }

    @SafeVarargs
    @NotNull
    public static <T> Set<T> newSet(final T... values) {
        final Set<T> list = new HashSet<>();
        Collections.addAll(list, values);
        return list;
    }

    @NotNull
    public static <K, V extends Comparable<? super V>> Map<K, V> sort(@NotNull final Map<K, V> map,
            @NotNull final Comparator<Map.Entry<K, V>> comparator) {
        return new LinkedList<>(map.entrySet()).stream().sorted(comparator)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (old, nev) -> nev, LinkedHashMap::new));
    }

    @NotNull
    public static List<String> getEnums(@NotNull final Class<? extends Enum<?>> clazz) {
        return new ArrayList<>(Stream.of(clazz.getEnumConstants()).map(Object::toString).toList());
    }

    @NotNull
    @Deprecated
    public static String getEnums(@NotNull final Class<? extends Enum<?>> clazz, @NotNull final String delimiter) {
        return StringUtil.inlineEnum(clazz, delimiter);
    }

    @NotNull
    public static <T extends Enum<T>> T next(@NotNull final Enum<T> numeration) { return Lists.shifted(numeration, 1); }

    @NotNull
    public static <T extends Enum<T>> T next(@NotNull final Enum<T> numeration, @NotNull final Predicate<T> predicate) {
        return Lists.shifted(numeration, 1, predicate);
    }

    @NotNull
    public static <T extends Enum<T>> T previous(@NotNull final Enum<T> numeration) { return Lists.shifted(numeration, -1); }

    @NotNull
    public static <T extends Enum<T>> T previous(@NotNull final Enum<T> numeration, @NotNull final Predicate<T> predicate) {
        return Lists.shifted(numeration, -1, predicate);
    }

    @NotNull
    public static <T extends Enum<T>> T shifted(@NotNull final Enum<T> numeration, final int shift) { return Lists.shifted(numeration, shift, null); }

    @NotNull
    private static <T extends Enum<T>> T shifted(@NotNull final Enum<T> numeration, final int shift, @Nullable final Predicate<T> predicate) {
        final T[] values = numeration.getDeclaringClass().getEnumConstants();
        return Lists.shifted(values, numeration/* .ordinal() */, shift, predicate);
    }

    @NotNull
    private static <T extends Enum<T>> T shifted(final T[] values, @NotNull final Enum<T> origin, final int shift, @Nullable final Predicate<T> predicate) {
        if (predicate != null) {
            final T source = origin.getDeclaringClass().cast(origin);
            final List<T> filtered = new ArrayList<>(Arrays.asList(values));
            filtered.removeIf(num -> !predicate.test(num) && num != source);

            final int currentIndex = filtered.indexOf(source);
            // List<T> filtered = Stream.of(values).filter(predicate).toList();
            if (currentIndex < 0 | filtered.isEmpty())
                return source;// values[currentIndex];

            return Lists.shifted(filtered, currentIndex, shift);
        }
        return Lists.shifted(values, origin.ordinal(), shift);
    }

    @NotNull
    public static <T> T shifted(final T[] values, final int currentIndex, final int shift) {
        final int index = currentIndex + shift;
        return values[index >= values.length || index < 0 ? 0 : index];
    }

    @NotNull
    public static <T> T shifted(@NotNull final List<T> values, final int currentIndex, final int shift) {
        final int index = currentIndex + shift;
        if (index < 0)
            return values.get(values.size() - 1);

        return values.get(index >= values.size() ? 0 : index);
    }
}
