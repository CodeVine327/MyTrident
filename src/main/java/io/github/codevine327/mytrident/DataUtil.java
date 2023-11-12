package io.github.codevine327.mytrident;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataUtil {
    public static MyTrident plugin;

    public static void setLostTrident(UUID owner, ItemStack trident, Location lostLocation) {
        Player player = Bukkit.getPlayer(owner);
        if (player == null) {
            writeToFile(owner, trident, lostLocation);
        } else {
            HashMap<Integer, ItemStack> map = player.getInventory().addItem(trident);
            if (!map.isEmpty()) {
                writeToFile(owner, trident, lostLocation);
            }
        }
    }

    public static int getLostTridentAmount(UUID owner) {
        FileConfiguration data = plugin.getTridentData();

        ConfigurationSection playerData = data.getConfigurationSection(owner.toString());
        return playerData == null ? 0 : playerData.getKeys(false).size();
    }

    public static List<ItemStack> getLostTridents(UUID owner) {
        List<ItemStack> items = new ArrayList<>();
        if (getLostTridentAmount(owner) == 0) {
            return items;
        }

        ConfigurationSection playerData = plugin.getTridentData().getConfigurationSection(owner.toString());
        playerData.getKeys(false).forEach(number -> {
            items.add(playerData.getItemStack(number + ".item"));
            playerData.set(number, null);
        });

        plugin.saveTridentData();
        return items;
    }

    private static void writeToFile(UUID owner, ItemStack trident, Location lostLocation) {
        FileConfiguration data = plugin.getTridentData();
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ConfigurationSection playerData = data.getConfigurationSection(owner.toString());

        // 为null时玩家第一次丢失三叉戟。
        int amount = playerData == null ? 1 : playerData.getKeys(false).size() + 1;
        data.set(owner + "." + amount + ".lost-time", time);
        data.set(owner + "." + amount + ".lost-location", lostLocation);
        data.set(owner + "." + amount + ".item", trident);
        plugin.saveTridentData();
    }
}
