package su.nightexpress.nightcore.util.text.tag.decorator;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.text.NightMessage;

public class ShowTextDecorator implements Decorator {

    private final List<String> text;

    public ShowTextDecorator(@NotNull final String... strings) { this(Lists.newList(strings)); }

    public ShowTextDecorator(@NotNull final List<String> text) { this.text = text; }

    @NotNull
    public HoverEvent createEvent() {
        final String fused = String.join(Placeholders.TAG_LINE_BREAK, this.text);
        final Text text = new Text(NightMessage.parse(fused));

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
    }

    @Override
    public void decorate(@NotNull final BaseComponent component) { component.setHoverEvent(this.createEvent()); }
}
