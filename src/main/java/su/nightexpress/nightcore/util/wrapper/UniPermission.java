package su.nightexpress.nightcore.util.wrapper;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UniPermission extends Permission {

    public UniPermission(@NotNull final String name) { this(name, null, null); }

    public UniPermission(@NotNull final String name, @Nullable final PermissionDefault defaultValue) { this(name, null, defaultValue); }

    public UniPermission(@NotNull final String name, @Nullable final String description) { this(name, description, PermissionDefault.OP); }

    public UniPermission(@NotNull final String name, @Nullable final String description, @Nullable final PermissionDefault defaultValue) {
        super(name, description, defaultValue);
    }

    @NotNull
    public UniPermission description(final String... desc) {
        this.setDescription(String.join(" ", desc));
        return this;
    }

    public void addChildren(@NotNull final Permission... childrens) {
        for (final Permission children : childrens) {
            children.addParent(this, true);
        }
    }
}
