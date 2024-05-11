package su.nightexpress.nightcore.util.text.tag.api;

import org.jetbrains.annotations.NotNull;

public abstract class Tag {

    public static final char OPEN_BRACKET = '<';
    public static final char CLOSE_BRACKET = '>';
    public static final char CLOSE_MARK = '/';

    protected final String name;
    protected final String[] aliases;

    public Tag(@NotNull final String name) { this(name, new String[0]); }

    public Tag(@NotNull final String name, @NotNull final String[] aliases) {
        this.name = name.toLowerCase();
        this.aliases = aliases;
    }

    @NotNull
    public static String brackets(@NotNull final String str) { return Tag.OPEN_BRACKET + str + Tag.CLOSE_BRACKET; }

    @NotNull
    public String getName() { return this.name; }

    @NotNull
    public String[] getAliases() { return this.aliases; }

    @NotNull
    public String enclose(@NotNull final String text) { return this.getFullName() + text + this.getClosingName(); }

    public abstract int getWeight();

    @NotNull
    public final String getFullName() { return Tag.brackets(this.getName()); }

    @NotNull
    public final String getClosingName() { return Tag.brackets(Tag.CLOSE_MARK + this.getName()); }

    public boolean conflictsWith(@NotNull final Tag tag) { return tag.getName().equalsIgnoreCase(this.getName()); }

    @Override
    public String toString() { return "Tag{" + "name='" + this.name + '\'' + '}'; }
}
