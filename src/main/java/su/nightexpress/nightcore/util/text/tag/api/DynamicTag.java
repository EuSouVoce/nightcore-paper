package su.nightexpress.nightcore.util.text.tag.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DynamicTag extends Tag {

    public DynamicTag(@NotNull final String name) { super(name); }

    @NotNull
    protected final String leading(@Nullable String prefix, @NotNull final String content) {
        if (prefix != null)
            prefix += ":";

        final StringBuilder builder = new StringBuilder();
        builder.append(this.getName()).append(":");
        if (prefix != null) {
            builder.append(prefix);
        }
        builder.append("'").append(content).append("'");

        return Tag.brackets(builder.toString());
    }

    @NotNull
    protected final String enclose(@Nullable final String prefix, @NotNull final String content, @NotNull final String text) {
        return this.leading(prefix, content) + text + this.getClosingName();
    }

    /*
     * @Nullable
     * @Deprecated public static String parseQuotedContent(@NotNull String string) {
     * char quote = string.charAt(0); if (quote != '\'' && quote != '"') return
     * null; int indexEnd = -1; for (int index = 0; index < string.length();
     * index++) { if (index == (string.length() - 1) || index == 0) continue; char
     * letter = string.charAt(index); if (letter == '\\') { index++; continue; } if
     * (letter != quote) continue; char next = string.charAt(index + 1); if (next ==
     * Tag.CLOSE_BRACKET) { indexEnd = index; break; } } if (indexEnd == -1) return
     * null; return string.substring(1, indexEnd); }
     */
}
