
package su.nightexpress.nightcore.util.text.tag.impl;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import su.nightexpress.nightcore.util.text.decoration.Decorator;
import su.nightexpress.nightcore.util.text.tag.api.Tag;

public class ColorTag extends Tag implements Decorator {

    protected final Color color;

    public ColorTag(@NotNull final String name, @NotNull final String hex) { this(name, new String[0], hex); }

    public ColorTag(@NotNull final String name, @NotNull final String[] aliases, @NotNull final String hex) {
        this(name, aliases, Color.decode(hex));
    }

    public ColorTag(@NotNull final Color color) { this(color, new String[0]); }

    public ColorTag(@NotNull final Color color, @NotNull final String[] aliases) {
        this(Integer.toHexString(color.getRGB()).substring(2), aliases, color);
    }

    public ColorTag(@NotNull final String name, @NotNull final Color color) { this(name, new String[0], color); }

    public ColorTag(@NotNull final String name, @NotNull final String[] aliases, @NotNull final Color color) {
        super(name, aliases);
        this.color = color;
    }

    @Override
    public int getWeight() { return 10; }

    @NotNull
    public Color getColor() { return this.color; }

    @Override
    public boolean conflictsWith(@NotNull final Tag tag) { return tag instanceof ColorTag; }

    @Override
    public void decorate(@NotNull final BaseComponent component) { component.setColor(ChatColor.of(this.getColor())); }
}
