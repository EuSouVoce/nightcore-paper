package su.nightexpress.nightcore.language.tag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.language.tag.impl.NoPrefixTag;
import su.nightexpress.nightcore.language.tag.impl.OutputTag;
import su.nightexpress.nightcore.language.tag.impl.PlaceholderTag;
import su.nightexpress.nightcore.language.tag.impl.SoundTag;

public class MessageTags {

    private static final Map<String, MessageTag> REGISTRY = new HashMap<>();

    public static final NoPrefixTag NO_PREFIX = new NoPrefixTag();
    public static final OutputTag OUTPUT = new OutputTag();
    public static final SoundTag SOUND = new SoundTag();
    public static final PlaceholderTag PLACEHOLDER = new PlaceholderTag();

    static {
        MessageTags.registerTags(MessageTags.NO_PREFIX, MessageTags.OUTPUT, MessageTags.SOUND, MessageTags.PLACEHOLDER);
    }

    public static void registerTags(@NotNull final MessageTag... tags) {
        for (final MessageTag tag : tags) {
            MessageTags.registerTag(tag);
        }
    }

    public static void registerTag(@NotNull final MessageTag tag, @NotNull final String... aliases) {
        MessageTags.REGISTRY.put(tag.getName(), tag);
        for (final String alias : aliases) {
            MessageTags.REGISTRY.put(alias, tag);
        }
    }

    @NotNull
    public static Collection<MessageTag> getTags() { return MessageTags.REGISTRY.values(); }

    public static MessageTag getTag(@NotNull final String name) { return MessageTags.REGISTRY.get(name.toLowerCase()); }
}
