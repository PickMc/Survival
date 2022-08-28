package me.zxoir.smp.database;

import me.zxoir.smp.SMP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
