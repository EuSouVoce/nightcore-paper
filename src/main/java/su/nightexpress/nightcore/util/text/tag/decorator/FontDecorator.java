package su.nightexpress.nightcore.util.text.tag.decorator;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.chat.BaseComponent;

public class FontDecorator implements Decorator {

    private final String font;

    public FontDecorator(@NotNull final String font) { this.font = font; }

    @Override
    public void decorate(@NotNull final BaseComponent component) { component.setFont(this.font); }
}
