package su.nightexpress.nightcore.util.random;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.util.Pair;

public class Rnd {

    public static final MTRandom RANDOM = new MTRandom();

    public static float getChance() { return Rnd.nextFloat() * 100f; }

    public static int get(final int n) { return Rnd.nextInt(n); }

    public static int get(final int min, final int max) { return min + (int) Math.floor(Rnd.RANDOM.nextDouble() * (max - min + 1)); }

    public static double getDouble(final double max) { return Rnd.getDouble(0, max); }

    public static double getDouble(final double min, final double max) { return min + (max - min) * Rnd.RANDOM.nextDouble(); }

    @NotNull
    public static <E> E get(@NotNull final E[] list) { return list[Rnd.get(list.length)]; }

    public static int get(final int[] list) { return list[Rnd.get(list.length)]; }

    @NotNull
    public static <E> E get(@NotNull final List<E> list) {
        if (list.isEmpty())
            throw new NoSuchElementException("Empty list provided!");

        return list.get(Rnd.get(list.size()));
    }

    @NotNull
    public static <E> E get(@NotNull final Set<E> list) { return Rnd.get(new ArrayList<>(list)); }

    @NotNull
    public static <T> T getByWeight(@NotNull final Map<T, Double> itemsMap) {
        final List<Pair<T, Double>> items = itemsMap.entrySet().stream().filter(entry -> entry.getValue() > 0D)
                .map(entry -> Pair.of(entry.getKey(), entry.getValue())).sorted(Comparator.comparing(Pair::getSecond)).toList();
        final double totalWeight = items.stream().mapToDouble(Pair::getSecond).sum();

        int index = 0;
        for (double roll = Rnd.nextDouble() * totalWeight; index < items.size() - 1; ++index) {
            roll -= items.get(index).getSecond();
            if (roll <= 0D)
                break;
        }
        return items.get(index).getFirst();
    }

    public static boolean chance(final int chance) { return chance >= 1 && (chance > 99 || Rnd.nextInt(99) + 1 <= chance); }

    public static boolean chance(final double chance) { return Rnd.nextDouble() <= chance / 100.0; }

    public static int nextInt(final int n) { return (int) Math.floor(Rnd.RANDOM.nextDouble() * n); }

    public static int nextInt() { return Rnd.RANDOM.nextInt(); }

    public static double nextDouble() { return Rnd.RANDOM.nextDouble(); }

    public static float nextFloat() { return Rnd.RANDOM.nextFloat(); }

    public static double nextGaussian() { return Rnd.RANDOM.nextGaussian(); }

    public static boolean nextBoolean() { return Rnd.RANDOM.nextBoolean(); }
}
