package su.nightexpress.nightcore.util.text.decoration;

import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

public class FontDecorator implements Decorator {

    private final String font;

    public FontDecorator(@NotNull final String font) { this.font = font; }

    @Override
    public void decorate(@NotNull final BaseComponent component) { component.setFont(this.font); }
}
