package su.nightexpress.nightcore.util.text.tag.decorator;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public class BaseColorDecorator implements ColorDecorator {

    private final Color color;

    public BaseColorDecorator(@NotNull final Color color) { this.color = color; }

    @Override
    public void decorate(@NotNull final BaseComponent component) { component.setColor(ChatColor.of(this.color)); }
}
