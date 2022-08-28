package me.zxoir.smp.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.zxoir.smp.SMP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/5/2022
 */
public class Database {
    private static final HikariConfig config = new HikariConfig();
    @Getter
    private static HikariDataSource dataSource;

    public Database(String sqlCreateStatement) {
        Bukkit.getLogger().info("Starting DB set up...");
        long start = System.currentTimeMillis();

        if (dataSource == null) {
            FileConfiguration configuration = SMP.getPlugin(SMP.class).getConfig();
            String username = configuration.getString("username");
            String database = configuration.getString("database");
            String password = configuration.getString("password");
            String ip = configuration.getString("ip");
            int port = configuration.getInt("port");

            config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(password);
            config.setConnectionTestQuery("SELECT 1");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            dataSource = new HikariDataSource(config);
        }

        try {

            execute((connection) -> {
                PreparedStatement statement = connection.prepareStatement(sqlCreateStatement);
                statement.executeUpdate();
            }).get();

        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: SDB_SDB.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: SDB_SDB.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        Bukkit.getLogger().info("Completed DB in " + finish + " s");
    }

    @NotNull
    @Contract("_ -> new")
    public static CompletableFuture<Void> execute(ConnectionCallback callback) {
        return CompletableFuture.runAsync(() -> {

            try (Connection conn = dataSource.getConnection()) {
                callback.doInConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new IllegalStateException("Error during execution.", e);
            }

        });
    }

    public interface ConnectionCallback {
        void doInConnection(Connection conn) throws SQLException;
    }
}
