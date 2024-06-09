package su.nightexpress.nightcore.language.tag.impl;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.language.message.MessageOptions;
import su.nightexpress.nightcore.language.tag.MessageTag;
import su.nightexpress.nightcore.util.StringUtil;

public class SoundTag extends MessageTag {

    public SoundTag() { super("sound"); }

    @NotNull
    public String enclose(@NotNull final Sound sound) { return this.enclose(sound.name().toLowerCase()); }

    @Override
    public void apply(@NotNull final MessageOptions options, @Nullable final String tagContent) {
        if (tagContent == null)
            return;

        final Sound sound = StringUtil.getEnum(tagContent, Sound.class).orElse(null);
        options.setSound(sound);
    }
}
