package su.nightexpress.nightcore.util.text.tag.decorator;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.chat.BaseComponent;

public interface Decorator {

    void decorate(@NotNull BaseComponent component);
}