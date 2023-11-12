package io.github.codevine327.mytrident;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

import java.util.List;
import java.util.UUID;

public class Listener implements org.bukkit.event.Listener {
    private final MyTrident plugin;

    public Listener(MyTrident plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        int lostAmount = DataUtil.getLostTridentAmount(event.getPlayer().getUniqueId());
        if (lostAmount > 0) {
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("message.lost").replaceAll("\\{amount}", String.valueOf(lostAmount))));
        }
    }

    @EventHandler
    private void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        World fromWorld = event.getFrom();
        List<Trident> allTridents = fromWorld.getEntities().stream()
                .filter(e -> e instanceof Trident)
                .map(e -> ((Trident) e))
                .filter(t -> event.getPlayer().getUniqueId().equals(t.getOwnerUniqueId()))
                .toList();

        allTridents.forEach(trident -> {
            DataUtil.setLostTrident(event.getPlayer().getUniqueId(), trident.getItem(), trident.getLocation());
            trident.remove();
        });
    }

    @EventHandler
    private void onPlayerPickupTrident(PlayerPickupArrowEvent event) {
        AbstractArrow arrow = event.getArrow();

        if (!(arrow instanceof Trident trident)) {
            return;
        }

        UUID owner = trident.getOwnerUniqueId();
        if (owner == null) {
            return;
        }

        event.setCancelled(true);
        DataUtil.setLostTrident(owner, trident.getItem(), trident.getLocation());
        trident.remove();
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDropItem(EntityDropItemEvent event) {
        if (event.getEntity().getType() != EntityType.TRIDENT || event.getItemDrop().getItemStack().getType() != Material.TRIDENT) {
            return;
        }

        Trident trident = (Trident) event.getEntity();
        UUID owner = trident.getOwnerUniqueId();

        if (owner == null) {
            return;
        }

        event.setCancelled(true);
        DataUtil.setLostTrident(owner, trident.getItem(), trident.getLocation());
    }

    @EventHandler
    private void onEntityUnload(EntitiesUnloadEvent event) {
        List<Entity> entities = event.getEntities().stream()
                .filter(entity -> entity.getType() == EntityType.TRIDENT)
                .toList();

        entities.forEach(entity -> {
            Trident trident = (Trident) entity;
            UUID owner = trident.getOwnerUniqueId();
            if (owner == null) {
                return;
            }

            // 排除非玩家扔出的三叉戟
            if (!Bukkit.getOfflinePlayer(owner).hasPlayedBefore()) {
                return;
            }

            DataUtil.setLostTrident(owner, trident.getItem(), trident.getLocation());
            trident.remove();
        });
    }
}
