package su.nightexpress.nightcore.database.connection;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.database.AbstractConnector;
import su.nightexpress.nightcore.database.DatabaseConfig;

public class SQLiteConnector extends AbstractConnector {

    public SQLiteConnector(@NotNull final NightCorePlugin plugin, @NotNull final DatabaseConfig config) { super(plugin, config); }

    @Override
    protected String getURL(@NotNull final DatabaseConfig config) {
        final String filePath = this.plugin.getDataFolder().getAbsolutePath() + "/" + config.getFilename();
        return "jdbc:sqlite:" + filePath;
    }

    @Override
    protected void setupConfig(@NotNull final DatabaseConfig config) { this.config.setMaximumPoolSize(1); }
}
