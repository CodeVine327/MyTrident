package io.github.codevine327.mytrident;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public final class MyTrident extends JavaPlugin {
    private FileConfiguration tridentData;

    @Override
    public void onEnable() {
        DataUtil.plugin = this;
        saveDefaultConfig();
        saveResource("trident-data.yml", false);
        this.tridentData = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "trident-data.yml"));
        Bukkit.getPluginManager().registerEvents(new Listener(this), this);
        getCommand("trident").setExecutor(new CommandImp(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getTridentData() {
        return tridentData;
    }

    public void saveTridentData() {
        try {
            tridentData.save(new File(getDataFolder(), "trident-data.yml"));
        } catch (IOException e) {
            getLogger().severe("三叉戟数据保存失败！");
            getLogger().severe(Arrays.toString(e.getStackTrace()));
        }
    }
}
