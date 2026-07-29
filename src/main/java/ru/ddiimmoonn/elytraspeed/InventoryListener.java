package ru.ddiimmoonn.elytraspeed;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryListener implements Listener {
    private final JavaPlugin plugin;

    public InventoryListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Подбор с земли
    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        ItemStack stack = event.getItem().getItemStack();
        if (stack != null && stack.getType() == Material.ELYTRA) {
            // сразу пометим как lvl1
            ItemUtils.setElytraLevel(stack, 1);
            event.getItem().setItemStack(stack);
        }
    }

    // Клик в инвентаре (shift/перетаскивание и т.д.) — проверить инвентарь через tick
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        // Отложим на один тик, чтобы операция уже завершилась
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (ItemStack it : p.getInventory().getContents()) {
                if (it != null && it.getType() == Material.ELYTRA && !ItemUtils.isCustomElytra(it)) {
                    ItemUtils.setElytraLevel(it, 1);
                }
            }
        });
    }

    // При входе/респавне — конвертируем все ELYTRA в lvl1
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        convertAll(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> convertAll(event.getPlayer()), 1L);
    }

    private void convertAll(Player p) {
        for (ItemStack it : p.getInventory().getContents()) {
            if (it != null && it.getType() == Material.ELYTRA && !ItemUtils.isCustomElytra(it)) {
                ItemUtils.setElytraLevel(it, 1);
            }
        }
    }
}
