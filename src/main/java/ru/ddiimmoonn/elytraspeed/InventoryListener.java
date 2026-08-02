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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryListener implements Listener {
    private final JavaPlugin plugin;

    public InventoryListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        ItemStack stack = event.getItem().getItemStack();
        if (stack != null && stack.getType() == Material.ELYTRA) {
            ItemUtils.setElytraLevel(stack, 1);
            event.getItem().setItemStack(stack);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        Bukkit.getScheduler().runTask(plugin, () -> convertAll(p));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        convertAll(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> convertAll(event.getPlayer()), 1L);
    }

    private void convertAll(Player p) {
        PlayerInventory inv = p.getInventory();
        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack it = contents[i];
            if (it != null && it.getType() == Material.ELYTRA && !ItemUtils.isCustomElytra(it)) {
                ItemUtils.setElytraLevel(it, 1);
                inv.setItem(i, it);
            }
        }
        ItemStack chest = inv.getChestplate();
        if (chest != null && chest.getType() == Material.ELYTRA && !ItemUtils.isCustomElytra(chest)) {
            ItemUtils.setElytraLevel(chest, 1);
            inv.setChestplate(chest);
        }
    }
}
