package me.zxoir.smp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import lombok.Getter;
import me.zxoir.smp.commands.*;
import me.zxoir.smp.customclasses.ChatEvents;
import me.zxoir.smp.customclasses.LuckyBlock;
import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.customclasses.Warp;
import me.zxoir.smp.database.DataFile;
import me.zxoir.smp.database.Database;
import me.zxoir.smp.listeners.*;
import me.zxoir.smp.managers.ConfigManager;
import me.zxoir.smp.managers.UserManager;
import me.zxoir.smp.tabcompleters.*;
import me.zxoir.smp.utilities.BasicUtilities;
import me.zxoir.smp.utilities.CustomPlaceholders;
import me.zxoir.smp.utilities.GlobalCache;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;


public final class SMP extends JavaPlugin {
    @Getter
    private static DataFile dataFile;
    @Getter
    private static final CopyOnWriteArraySet<Material> rareItems = new CopyOnWriteArraySet<>();
    @Getter
    private static final CopyOnWriteArrayList<Material> craftableItems = new CopyOnWriteArrayList<>();
    @Getter
    private static CoreProtectAPI coreProtectAPI;
    @Getter
    private static MultiverseCore multiverseCore;

    @Override
    public void onEnable() {
        getLogger().info("======================================================================");
        long startTime = System.currentTimeMillis();
        getLogger().info("Initializing plugin setup...");

        dataFile = new DataFile();
        dataFile.setup();
        ConfigManager.setup();
        saveDefaultConfig();

        if (getCoreProtect() == null) {
            getLogger().warning("Couldn't find CoreProtect, shutting down the plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().warning("Couldn't find PlaceholderAPI, shutting down the plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Multiverse-Core")) {
            getLogger().warning("Couldn't find Multiverse-Core, shutting down the plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        long start = System.currentTimeMillis();
        getLogger().info("Registering Placeholders");

        new CustomPlaceholders().register();
        coreProtectAPI = getCoreProtect();
        multiverseCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");

        getLogger().info("Registered Placeholders (" + (System.currentTimeMillis() - start) + "ms)");

        new Database("CREATE TABLE IF NOT EXISTS User(" +
                "uuid VARCHAR(36) PRIMARY KEY NOT NULL," +
                "dateJoined TEXT NOT NULL," +
                "stats LONGTEXT NOT NULL," +
                "cache LONGTEXT NOT NULL," +
                "playtime TEXT NOT NULL" +
                ");");

        start = System.currentTimeMillis();
        getLogger().info("Caching Users");
        UserManager.cacheUsers();
        getLogger().info("Cached Users (" + (System.currentTimeMillis() - start) + "ms)");

        start = System.currentTimeMillis();
        getLogger().info("Setting up data folders and Config Manager...");

        ScoreboardListener.init();
        ChatEvents.init();
        LuckyBlock.init();

        getLogger().info("Successfully setup data folders and Config Manager (" + (System.currentTimeMillis() - start) + "ms)");
        start = System.currentTimeMillis();

        getLogger().info("Registering commands and listeners...");

        registerCommands();
        registerTabCompleters();
        registerListeners();
        getLogger().info("Registered commands and listeners (" + (System.currentTimeMillis() - start) + "ms)");

        reloadCheck();
        loadCache();

        getLogger().info("Plugin loaded and initialized successfully. Took " + (System.currentTimeMillis() - startTime) + "ms");
        getLogger().info("======================================================================");
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(Player::resetTitle);

        for (Block block : LuckyBlock.getPlacedLuckyBlocks().keySet()) {
            block.setType(Material.AIR);
            LuckyBlock.getPlacedLuckyBlocks().remove(block);
            LuckyBlock.activateLuckyBlock(null, LuckyBlock.getRandomLuckyBlockType(), block.getLocation());
        }

        Database.getDataSource().close();
    }

    @Nullable
    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 9) {
            return null;
        }

        return CoreProtect;
    }

    private void reloadCheck() {
        if (!getServer().getOnlinePlayers().isEmpty()) {
            for (Player player : getServer().getOnlinePlayers()) {
                if (UserManager.getUsers().containsKey(player.getUniqueId()))
                    return;

                User user = new User(player.getUniqueId());
                UserManager.cacheUser(user);
                user.getCache().setSessionStart(Instant.now());

                BasicUtilities.runTaskSync(() -> ScoreboardListener.setScoreboard(player));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadCache() {
        Bukkit.getLogger().info(dataFile.getConfig().get("FirstJoinKit", ItemStack[].class).getClass().getName());
        if (dataFile.getConfig().getString("FirstJoinKit") != null) {
            List<ItemStack> itemStacks = (List<ItemStack>) dataFile.getConfig().get("FirstJoinKit");
            if (itemStacks != null)
                FirstJoinKitListener.setKitItems(itemStacks.toArray(new ItemStack[0]));
        }

        if (dataFile.getConfig().getString("LuckyBlockItem") != null)
            LuckyBlock.setLuckyBlock(dataFile.getConfig().getItemStack("LuckyBlockItem"));

        if (dataFile.getConfig().getString("Spawn") != null) {
            Location location = dataFile.getConfig().getLocation("Spawn");
            GlobalCache.setSpawnWarp(new Warp(location));
        }

    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("smp")).setExecutor(new MainCommand());
        Objects.requireNonNull(getCommand("spawn")).setExecutor(new SpawnCommand());
        Objects.requireNonNull(getCommand("back")).setExecutor(new BackCommand());
        Objects.requireNonNull(getCommand("chatevents")).setExecutor(new ChatEventsCommand());
        Objects.requireNonNull(getCommand("luckyblock")).setExecutor(new LuckyBlockCommand());
        Objects.requireNonNull(getCommand("afk")).setExecutor(new AfkCommand());
    }

    private void registerTabCompleters() {
        Objects.requireNonNull(getCommand("smp")).setTabCompleter(new MainCompleter());
        Objects.requireNonNull(getCommand("spawn")).setTabCompleter(new SpawnCompleter());
        Objects.requireNonNull(getCommand("back")).setTabCompleter(new BackCompleter());
        Objects.requireNonNull(getCommand("chatevents")).setTabCompleter(new ChatEventsCompleter());
        Objects.requireNonNull(getCommand("luckyblock")).setTabCompleter(new LuckyBlockCompleter());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AfkListener(), this);
        getServer().getPluginManager().registerEvents(new StatsListener(), this);
        getServer().getPluginManager().registerEvents(new ExperienceListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new UserListener(), this);
        getServer().getPluginManager().registerEvents(new PlaytimeListener(), this);
        getServer().getPluginManager().registerEvents(new FirstJoinKitListener(), this);
        getServer().getPluginManager().registerEvents(new WarpListener(), this);
        getServer().getPluginManager().registerEvents(new BackListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        getServer().getPluginManager().registerEvents(new ChatEventsListener(), this);
        getServer().getPluginManager().registerEvents(new LuckyBlockListener(), this);
        getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        getServer().getPluginManager().registerEvents(new VoteListener(), this);
    }
}