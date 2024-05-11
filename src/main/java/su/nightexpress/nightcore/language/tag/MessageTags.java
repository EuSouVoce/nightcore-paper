package su.nightexpress.nightcore.language.tag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.language.tag.impl.NoPrefixTag;
import su.nightexpress.nightcore.language.tag.impl.OutputTag;
import su.nightexpress.nightcore.language.tag.impl.PlaceholderTag;
import su.nightexpress.nightcore.language.tag.impl.SoundTag;
import su.nightexpress.nightcore.util.text.tag.api.Tag;

public class MessageTags {

    private static final Map<String, Tag> TAG_MAP = new HashMap<>();

    public static final NoPrefixTag NO_PREFIX = new NoPrefixTag();
    public static final OutputTag OUTPUT = new OutputTag();
    public static final SoundTag SOUND = new SoundTag();
    public static final PlaceholderTag PLACEHOLDER = new PlaceholderTag();

    static {
        MessageTags.registerTags(MessageTags.NO_PREFIX, MessageTags.OUTPUT, MessageTags.SOUND, MessageTags.PLACEHOLDER);
    }

    public static void registerTags(@NotNull final Tag... tags) {
        for (final Tag tag : tags) {
            MessageTags.registerTag(tag);
        }
    }

    public static void registerTag(@NotNull final Tag tag, @NotNull final String... aliases) {
        MessageTags.TAG_MAP.put(tag.getName(), tag);
        for (final String alias : aliases) {
            MessageTags.TAG_MAP.put(alias, tag);
        }
    }

    @NotNull
    public static Collection<Tag> getTags() { return MessageTags.TAG_MAP.values(); }

    public static Tag getTag(@NotNull final String name) { return MessageTags.TAG_MAP.get(name.toLowerCase()); }
}
