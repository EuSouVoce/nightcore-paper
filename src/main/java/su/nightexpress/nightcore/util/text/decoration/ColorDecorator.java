package su.nightexpress.nightcore.util.text.decoration;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public class ColorDecorator implements Decorator {

    private final Color color;

    public ColorDecorator(@NotNull final Color color) { this.color = color; }

    @Override
    public void decorate(@NotNull final BaseComponent component) { component.setColor(ChatColor.of(this.color)); }
}
