package su.nightexpress.nightcore.util.text.decoration;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class GradientDecorator implements Decorator {

    private final Color color;
    private final Color colorEnd;

    public GradientDecorator(@NotNull final Color color, @NotNull final Color colorEnd) {
        this.color = color;
        this.colorEnd = colorEnd;
    }

    public boolean isSimilar(@NotNull final GradientDecorator other) {
        return this.getColor().equals(other.getColor()) && this.getColorEnd().equals(other.getColorEnd());
    }

    public Color getColor() { return this.color; }

    @NotNull
    public Color getColorEnd() { return this.colorEnd; }

    public Color[] createGradient(final int length) {
        final Color[] colors = new Color[length];
        for (int index = 0; index < length; index++) {
            final double percent = (double) index / (double) length;

            final int red = (int) (this.color.getRed() + percent * (this.colorEnd.getRed() - this.color.getRed()));
            final int green = (int) (this.color.getGreen() + percent * (this.colorEnd.getGreen() - this.color.getGreen()));
            final int blue = (int) (this.color.getBlue() + percent * (this.colorEnd.getBlue() - this.color.getBlue()));

            final java.awt.Color color = new java.awt.Color(red, green, blue);
            colors[index] = color;
        }
        return colors;
    }

    /*
     * @NotNull public List<Pair<String, Color>> gradient(@NotNull String string) {
     * List<Pair<String, Color>> list = new ArrayList<>(); Color[] colors =
     * createGradient(string.length()); char[] characters = string.toCharArray();
     * for (int index = 0; index < characters.length; index++) {
     * list.add(Pair.of(String.valueOf(characters[index]), colors[index])); } return
     * list; }
     */

    @Override
    public void decorate(@NotNull final BaseComponent component) {
        if (!(component instanceof final TextComponent textComponent))
            return;

        final String text = textComponent.getText();

        final Color[] colors = this.createGradient(text.length());
        final char[] characters = text.toCharArray();

        for (int index = 0; index < characters.length; index++) {
            final TextComponent extra = new TextComponent(String.valueOf(characters[index]));
            extra.copyFormatting(textComponent);
            extra.setColor(ChatColor.of(colors[index]));
            textComponent.addExtra(extra);
        }
        textComponent.setText("");
    }
}
