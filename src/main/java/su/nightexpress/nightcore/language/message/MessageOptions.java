package su.nightexpress.nightcore.language.message;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageOptions {

    private OutputType outputType;
    private boolean hasPrefix;
    private boolean usePlaceholderAPI;
    private Sound sound;
    private int[] titleTimes;

    public MessageOptions() {
        this.setOutputType(OutputType.CHAT);
        this.setHasPrefix(true);
    }

    @NotNull
    public MessageOptions copy() {
        final MessageOptions copy = new MessageOptions();
        copy.outputType = this.outputType;
        copy.hasPrefix = this.hasPrefix;
        copy.usePlaceholderAPI = this.usePlaceholderAPI;
        copy.sound = this.sound;
        copy.titleTimes = this.titleTimes;
        return copy;
    }

    @NotNull
    public OutputType getOutputType() { return this.outputType; }

    public void setOutputType(final OutputType outputType) { this.outputType = outputType; }

    public boolean hasPrefix() { return this.hasPrefix; }

    public void setHasPrefix(final boolean hasPrefix) { this.hasPrefix = hasPrefix; }

    public boolean usePlaceholderAPI() { return this.usePlaceholderAPI; }

    public void setUsePlaceholderAPI(final boolean usePlaceholderAPI) { this.usePlaceholderAPI = usePlaceholderAPI; }

    @Nullable
    public Sound getSound() { return this.sound; }

    public void setSound(final Sound sound) { this.sound = sound; }

    public int[] getTitleTimes() { return this.titleTimes; }

    public void setTitleTimes(final int[] titleTimes) { this.titleTimes = titleTimes; }
}
