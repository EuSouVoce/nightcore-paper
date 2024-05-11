package su.nightexpress.nightcore.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Reflex {

    public static Class<?> getClass(@NotNull final String path, @NotNull final String name) { return Reflex.getClass(path + "." + name); }

    public static Class<?> getInnerClass(@NotNull final String path, @NotNull final String name) { return Reflex.getClass(path + "$" + name); }

    private static Class<?> getClass(@NotNull final String path) {
        try {
            return Class.forName(path);
        } catch (final ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static Constructor<?> getConstructor(@NotNull final Class<?> source, final Class<?>... types) {
        try {
            final Constructor<?> constructor = source.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return constructor;
        } catch (final ReflectiveOperationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Object invokeConstructor(@NotNull final Constructor<?> constructor, final Object... obj) {
        try {
            return constructor.newInstance(obj);
        } catch (final ReflectiveOperationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @NotNull
    public static <T> List<T> getFields(@NotNull final Class<?> source, @NotNull final Class<T> type) {
        final List<T> list = new ArrayList<>();

        for (final Field field : Reflex.getFields(source)) {
            if (!field.getDeclaringClass().equals(source))
                continue;
            // if (!field.canAccess(null)) continue;
            if (!Modifier.isStatic(field.getModifiers()))
                continue;
            if (!Modifier.isFinal(field.getModifiers()))
                continue;
            if (!type.isAssignableFrom(field.getType()))
                continue;
            if (!field.trySetAccessible())
                continue;

            try {
                list.add(type.cast(field.get(null)));
            } catch (IllegalArgumentException | IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }

        return list;
    }

    @NotNull
    public static List<Field> getFields(@NotNull final Class<?> source) {
        final List<Field> result = new ArrayList<>();

        Class<?> clazz = source;
        while (clazz != null && clazz != Object.class) {
            if (!result.isEmpty()) {
                result.addAll(0, Arrays.asList(clazz.getDeclaredFields()));
            } else {
                Collections.addAll(result, clazz.getDeclaredFields());
            }
            clazz = clazz.getSuperclass();
        }

        return result;
    }

    public static Field getField(@NotNull final Class<?> source, @NotNull final String name) {
        try {
            return source.getDeclaredField(name);
        } catch (final NoSuchFieldException exception) {
            final Class<?> superClass = source.getSuperclass();
            return superClass == null ? null : Reflex.getField(superClass, name);
        }
    }

    public static Object getFieldValue(@NotNull final Object source, @NotNull final String name) {
        try {
            final Class<?> clazz = source instanceof Class<?> ? (Class<?>) source : source.getClass();
            final Field field = Reflex.getField(clazz, name);
            if (field == null)
                return null;

            field.setAccessible(true);
            return field.get(source);
        } catch (final IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static boolean setFieldValue(@NotNull final Object source, @NotNull final String name, @Nullable final Object value) {
        try {
            final boolean isStatic = source instanceof Class;
            final Class<?> clazz = isStatic ? (Class<?>) source : source.getClass();

            final Field field = Reflex.getField(clazz, name);
            if (field == null)
                return false;

            field.setAccessible(true);
            field.set(isStatic ? null : source, value);
            return true;
        } catch (final IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public static Method getMethod(@NotNull final Class<?> source, @NotNull final String name, @NotNull final Class<?>... params) {
        try {
            return source.getDeclaredMethod(name, params);
        } catch (final NoSuchMethodException exception) {
            final Class<?> superClass = source.getSuperclass();
            return superClass == null ? null : Reflex.getMethod(superClass, name);
        }
    }

    public static Object invokeMethod(@NotNull final Method method, @Nullable final Object by, @Nullable final Object... param) {
        method.setAccessible(true);
        try {
            return method.invoke(by, param);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
