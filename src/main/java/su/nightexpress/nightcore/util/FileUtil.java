package su.nightexpress.nightcore.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class FileUtil {

    public static void copy(@NotNull final InputStream inputStream, @NotNull final File file) {
        try {
            final FileOutputStream outputStream = new FileOutputStream(file);
            final byte[] array = new byte[1024];
            int read;
            while ((read = inputStream.read(array)) > 0) {
                outputStream.write(array, 0, read);
            }
            outputStream.close();
            inputStream.close();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean create(@NotNull final File file) {
        if (file.exists())
            return false;

        final File parent = file.getParentFile();
        if (parent == null)
            return false;

        parent.mkdirs();
        try {
            return file.createNewFile();
        } catch (final IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @NotNull
    public static List<File> getConfigFiles(@NotNull final String path) { return FileUtil.getConfigFiles(path, false); }

    @NotNull
    public static List<File> getConfigFiles(@NotNull final String path, final boolean deep) { return FileUtil.getFiles(path, FileConfig.EXTENSION, deep); }

    @NotNull
    public static List<File> getFiles(@NotNull final String path) { return FileUtil.getFiles(path, false); }

    @NotNull
    public static List<File> getFiles(@NotNull final String path, final boolean deep) { return FileUtil.getFiles(path, null, deep); }

    @NotNull
    public static List<File> getFiles(@NotNull final String path, @Nullable final String extension, final boolean deep) {
        final List<File> files = new ArrayList<>();

        final File folder = new File(path);
        final File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null)
            return files;

        for (final File file : listOfFiles) {
            if (file.isFile()) {
                if (extension == null || file.getName().endsWith(extension)) {
                    files.add(file);
                }
            } else if (file.isDirectory() && deep) {
                files.addAll(FileUtil.getFiles(file.getPath(), true));
            }
        }
        return files;
    }

    @NotNull
    public static List<File> getFolders(@NotNull final String path) {
        final File folder = new File(path);
        final File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null)
            return Collections.emptyList();

        return Stream.of(listOfFiles).filter(File::isDirectory).toList();
    }

    public static boolean deleteRecursive(@NotNull final String path) { return FileUtil.deleteRecursive(new File(path)); }

    public static boolean deleteRecursive(@NotNull final File dir) {
        if (!dir.exists())
            return false;

        final File[] inside = dir.listFiles();
        if (inside != null) {
            for (final File file : inside) {
                FileUtil.deleteRecursive(file);
            }
        }
        return dir.delete();
    }

    public static void extractResources(@NotNull final File pluginFile, @NotNull final String fromPath, @NotNull final File destination) {
        if (!destination.exists()) {
            if (!destination.mkdirs()) {
                return;
            }
        }

        try {
            final JarFile jar = new JarFile(pluginFile);
            final Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String path = entry.getName();
                if (entry.isDirectory() || !path.startsWith(fromPath))
                    continue;

                final File file = new File(destination, path.replaceFirst(fromPath, ""));
                if (file.exists())
                    continue;

                FileUtil.create(file);
                final InputStream inputStream = jar.getInputStream(entry);
                final FileOutputStream outputStream = new FileOutputStream(file);

                while (inputStream.available() > 0) {
                    outputStream.write(inputStream.read());
                }
                outputStream.close();
                inputStream.close();
            }

            jar.close();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }
}
