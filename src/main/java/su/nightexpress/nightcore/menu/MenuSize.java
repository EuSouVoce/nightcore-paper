package su.nightexpress.nightcore.menu;

public enum MenuSize {
    // @formatter:off
    CHEST_9(9),
    CHEST_18(18),
    CHEST_27(27),
    CHEST_36(36),
    CHEST_45(45),
    CHEST_54(54);
    // @formatter:on
    private final int size;

    MenuSize(final int size) { this.size = size; }

    public int getSize() { return this.size; }
}
