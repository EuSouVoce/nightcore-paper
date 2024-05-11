package su.nightexpress.nightcore.database.connection;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.database.AbstractConnector;
import su.nightexpress.nightcore.database.DatabaseConfig;

public class MySQLConnector extends AbstractConnector {

    public MySQLConnector(@NotNull final NightCorePlugin plugin, @NotNull final DatabaseConfig config) { super(plugin, config); }

    @Override
    protected String getURL(@NotNull final DatabaseConfig config) {
        final String host = config.getHost();
        final String database = config.getDatabase();
        final String options = config.getUrlOptions();

        return "jdbc:mysql://" + host + "/" + database + options;
    }

    @Override
    protected void setupConfig(@NotNull final DatabaseConfig config) {
        this.config.setUsername(config.getUsername());
        this.config.setPassword(config.getPassword());
    }
}
