package me.zxoir.smp.database;

import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.Team;
import me.zxoir.smp.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class DataFile {
    public FileConfiguration playerscfg;
    public File playersfile;

    // Sets up the configuration yml
    public void setup() {
        SMP mainInstance = SMP.getPlugin(SMP.class);
        if (!mainInstance.getDataFolder().exists()) {
            mainInstance.getDataFolder().mkdir();
        }

        File dataFile = new File(mainInstance.getDataFolder() + File.separator + "Data" + File.separator);
        this.playersfile = new File(dataFile.getPath(), "DataFile.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.mkdirs();
                Bukkit.getLogger().info("the Data folder has been created!");
            } catch (SecurityException e) {
                Bukkit.getLogger().warning("Could not create the Data folder");
            }
        }

        if (!this.playersfile.exists()) {
            try {
                this.playersfile.createNewFile();
                Bukkit.getLogger().info("the DataFile.yml file has been created!");
            } catch (IOException e) {
                Bukkit.getLogger().warning("Could not create the DataFile.yml file");
            }
        }

        this.playerscfg = YamlConfiguration.loadConfiguration(this.playersfile);

        if (playerscfg.isConfigurationSection("Team")) {
            for (String key : playerscfg.getConfigurationSection("Team").getKeys(false)) {
                Bukkit.getLogger().info("LOADING TEAMS:");
                Bukkit.getLogger().info(key);
                String name = playerscfg.getString("Team." + key + ".Name");
                Bukkit.getLogger().info(name);
                UUID leader = UUID.fromString(playerscfg.getString("Team." + key + ".Leader"));
                Bukkit.getLogger().info(leader.toString());
                List<String> members = playerscfg.getStringList("Team." + key + ".Members");
                Bukkit.getLogger().info(members.size() + "");
                String tag = playerscfg.getString("Team." + key + ".Tag");
                String color = playerscfg.getString("Team." + key + ".Color");
                Bukkit.getLogger().info("DONE LOADING TEAMS");
                Team team = new Team(name, tag, color, leader, members);
                TeamManager.getCachedTeams().put(team.getName().toLowerCase(), team);
            }
        }
    }

    // Gets the yml configuration
    public FileConfiguration getConfig() {
        return this.playerscfg;
    }

    // Saves the yml configuration
    public void saveConfig() {
        try {
            this.playerscfg.save(this.playersfile);
        } catch (IOException localIOException) {
            Bukkit.getLogger().warning("Could not save the DataFile.yml file");
        }
    }

    // Reloads the yml configuration
    public void reloadConfig() {
        this.playerscfg = YamlConfiguration.loadConfiguration(this.playersfile);
    }
}
