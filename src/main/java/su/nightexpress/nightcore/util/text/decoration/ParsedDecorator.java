package su.nightexpress.nightcore.util.text.decoration;

import org.jetbrains.annotations.NotNull;

public class ParsedDecorator {

    private final Decorator tag;

    private int length;

    public ParsedDecorator(@NotNull final Decorator tag, final int length) {
        this.tag = tag;
        this.length = length;
    }

    @NotNull
    public Decorator getDecorator() { return this.tag; }

    public void setLength(final int length) { this.length = length; }

    public int getLength() { return this.length; }
}
