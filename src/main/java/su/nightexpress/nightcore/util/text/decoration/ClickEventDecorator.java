package su.nightexpress.nightcore.util.text.decoration;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

public class ClickEventDecorator implements Decorator {

    private final ClickEvent.Action action;
    private final String value;

    public ClickEventDecorator(@NotNull final ClickEvent.Action action, @NotNull final String value) {
        this.action = action;
        this.value = value;
    }

    @NotNull
    public ClickEvent createEvent() { return new ClickEvent(this.action, this.value); }

    @Override
    public void decorate(@NotNull final BaseComponent component) { component.setClickEvent(this.createEvent()); }
}
