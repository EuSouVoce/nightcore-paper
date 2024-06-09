package su.nightexpress.nightcore.util.text;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.chat.BaseComponent;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.text.tag.TagPool;

public class NightMessage {

    @NotNull
    public static TextRoot from(@NotNull final String string) { return NightMessage.from(string, TagPool.ALL); }

    @NotNull
    public static TextRoot from(@NotNull final String string, @NotNull final TagPool tagPool) { return new TextRoot(string, tagPool); }

    /**
     * @param string Text to deserialize.
     * @return Precompiled TextRoot object.
     */
    @NotNull
    public static TextRoot create(@NotNull final String string) { return NightMessage.create(string, TagPool.ALL); }

    @NotNull
    public static TextRoot create(@NotNull final String string, @NotNull final TagPool tagPool) { return NightMessage.from(string, tagPool).compile(); }

    @NotNull
    public static BaseComponent parse(@NotNull final String string) { return NightMessage.parse(string, TagPool.ALL); }

    @NotNull
    public static BaseComponent parse(@NotNull final String string, @NotNull final TagPool tagPool) { return NightMessage.from(string, tagPool).parseIfAbsent(); }

    @NotNull
    public static String clean(@NotNull final String string) { return NightMessage.create(string, TagPool.NONE).toLegacy(); }

    @NotNull
    public static String stripAll(@NotNull final String string) { return Colorizer.strip(NightMessage.clean(string)); }

    @NotNull
    public static String asJson(@NotNull final String string) { return NightMessage.create(string).toJson(); }

    @NotNull
    public static String asLegacy(@NotNull final String string) { return NightMessage.create(string).toLegacy(); }

    @Deprecated
    public static BaseComponent asComponent(@NotNull final String string) { return NightMessage.create(string).toComponent(); }

    @NotNull
    public static List<String> asLegacy(@NotNull final List<String> string) {
        final List<String> list = new ArrayList<>();
        for (final String str : string) {
            for (final String br : str.split(Placeholders.TAG_LINE_BREAK)) {
                list.add(NightMessage.asLegacy(br));
            }
        }
        return list;
    }

}
