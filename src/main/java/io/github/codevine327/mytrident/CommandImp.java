package io.github.codevine327.mytrident;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CommandImp implements CommandExecutor {
    private final MyTrident plugin;

    public CommandImp(MyTrident plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("调皮了嗷，只能由玩家执行该命令。");
            return true;
        }

        UUID owner = player.getUniqueId();
        int lostAmount = DataUtil.getLostTridentAmount(owner);
        if (lostAmount == 0) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("message.no-trident")));
            return true;
        }

        List<ItemStack> lostTridents = DataUtil.getLostTridents(owner);
        lostTridents.forEach(trident -> {
            HashMap<Integer, ItemStack> map = player.getInventory().addItem(trident);
            if (!map.isEmpty()) {
                player.getWorld().dropItem(player.getLocation(), trident);
            }
        });
        player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("message.success")));

        return true;
    }
}
