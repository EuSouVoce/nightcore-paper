package su.nightexpress.nightcore.util.text.tag.impl;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import su.nightexpress.nightcore.util.text.decoration.Decorator;
import su.nightexpress.nightcore.util.text.tag.api.Tag;

public class TranslateTag extends Tag implements Decorator {

    public static final String NAME = "translate";

    public TranslateTag() { super(TranslateTag.NAME, new String[] { "tr" }); }

    @Override
    public int getWeight() { return Integer.MAX_VALUE - 10; }

    @Override
    public void decorate(@NotNull final BaseComponent component) {
        if (!(component instanceof final TextComponent textComponent))
            return;

        final String content = textComponent.getText();
        textComponent.setText("");

        final TranslatableComponent translatableComponent = new TranslatableComponent(content);
        component.addExtra(translatableComponent);
    }
}
