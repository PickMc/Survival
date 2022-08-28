package me.zxoir.smp.managers;

import me.zxoir.smp.customclasses.Cache;
import me.zxoir.smp.customclasses.Stats;
import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.database.Database;
import me.zxoir.smp.utilities.GlobalCache;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MIT License Copyright (c) 2020/2021 Zxoir
 *
 * @author Zxoir
 * @since 10/20/2020
 */
public class DatabaseManager {

    @NotNull
    public static List<User> getUsers() {
        List<User> users = new CopyOnWriteArrayList<>();
        long start = System.currentTimeMillis();

        try {
            Database.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM User");
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    users.add(dbToData(resultSet));
                }

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: DM_GUs.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: DM_GUs.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        Bukkit.getLogger().info("Fetched DB results in " + finish + " seconds.");

        return users;
    }

    @Nullable
    public static User getUser(UUID uuid) {

        AtomicReference<User> user = new AtomicReference<>(null);
        long start = System.currentTimeMillis();

        try {
            Database.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM User WHERE uuid = ? LIMIT 1");
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                if (!resultSet.next())
                    return;

                user.set(dbToData(resultSet));

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: SDBM_GS.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: SDBM_GS.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        Bukkit.getLogger().info("Fetched DB result in " + finish + " seconds.");

        return user.get();
    }

    @NotNull
    @Contract("_ -> new")
    public static CompletableFuture<Void> saveToDB(User user) {

        return Database.execute(conn -> {
            try {
                long start = System.currentTimeMillis();
                PreparedStatement statement = conn.prepareStatement("INSERT INTO User VALUES(?, ?, ?, ?, ?)");

                statement.setString(1, user.getUuid().toString());
                String date = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa").format(user.getDateJoined());

                statement.setString(2, date);

                statement.setString(3, GlobalCache.getAdapter().toJson(user.getStats(), Stats.class));

                statement.setString(4, GlobalCache.getAdapter().toJson(user.getCache(), Cache.class));

                statement.setString(5, GlobalCache.getAdapter().toJson(user.getPlaytime().toString()));

                statement.execute();

                double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
                Bukkit.getLogger().info("Saved User ('" + user.getUuid() + " => " + Bukkit.getOfflinePlayer(user.getUuid()).getName() + "') to DB in " + finish + " seconds.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @NotNull
    @Contract("_ -> new")
    public static CompletableFuture<Void> deleteFromDatabase(User user) {
        return Database.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement("DELETE FROM User WHERE uuid=?");
            statement.setString(1, user.getUuid().toString());
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Bukkit.getLogger().info("Deleted User ('" + user.getUuid() + " => " + Bukkit.getOfflinePlayer(user.getUuid()).getName() + "') from DB in " + finish + " seconds.");
        });
    }

    @NotNull
    @Contract("_ -> new")
    public static CompletableFuture<Void> updateDatabase(User user) {
        return Database.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(
                    "UPDATE User SET stats = ?, cache = ?, playtime = ? WHERE uuid = ?");

            statement.setString(1, GlobalCache.getAdapter().toJson(user.getStats(), Stats.class));

            statement.setString(2, GlobalCache.getAdapter().toJson(user.getCache(), Cache.class));

            statement.setString(3, user.getPlaytime().toString());

            statement.setString(4, user.getUuid().toString());

            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            Bukkit.getLogger().info("Updated User to Database in " + finish + " seconds.");
        });
    }

    @NotNull
    private static User dbToData(@NotNull ResultSet resultSet) throws SQLException {
        UUID uuid = UUID.fromString(resultSet.getString("uuid"));

        Instant dateJoinedInstant = new Date().toInstant();
        try {
            dateJoinedInstant = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa").parse(resultSet.getString("dateJoined")).toInstant();
        } catch (ParseException e) {
            Bukkit.getLogger().info("FAILED TO PARSE DATE OF DATA UUID " + uuid + "WITH ERROR: " + e.getMessage());
        }

        Date dateJoined = Date.from(dateJoinedInstant);

        Stats stats = GlobalCache.getAdapter().fromJson(resultSet.getString("stats"), Stats.class);

        Cache cache = GlobalCache.getAdapter().fromJson(resultSet.getString("cache"), Cache.class);

        Duration playtime = Duration.parse(resultSet.getString("playtime"));

        return new User(uuid, dateJoined, stats, cache, playtime);
    }

}