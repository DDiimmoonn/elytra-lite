package ru.ddiimmoonn.elytraspeed;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleGlideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

public class GlideListener implements Listener {
    private final ElytraSpeedPlugin plugin;

    public GlideListener(ElytraSpeedPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onToggleGlide(PlayerToggleGlideEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        if (e.isGliding()) {
            // начало парения
            ItemStack chest = p.getInventory().getChestplate();
            if (chest == null || chest.getType() != Material.ELYTRA) {
                return; // нет элитры
            }
            int level = ItemUtils.getElytraLevel(chest, plugin);
            double multiplier = ItemUtils.levelToMultiplier(level);

            // базовая горизонтальная скорость — текущая горизонтальная скорость
            Vector vel = p.getVelocity();
            double base = Math.hypot(vel.getX(), vel.getZ());

            // направление игрока (горизонтально)
            Vector dir = p.getLocation().getDirection().clone();
            dir.setY(0);

            if (base < 0.01) {
                // если скорость почти нулевая, даём небольшую стартовую скорость
                if (dir.length() < 0.0001) {
                    base = 0.2;
                    // если направление тоже нулевое, возьмём направление по положительному Z
                    dir = new Vector(0, 0, 1);
                } else {
                    dir.normalize();
                    base = 0.2;
                }
            } else {
                // есть горизонтальная скорость — используем её направление, если направление игрока нулевое
                if (dir.length() < 0.0001) {
                    dir = new Vector(vel.getX(), 0, vel.getZ());
                    if (dir.length() < 0.0001) {
                        dir = new Vector(0, 0, 1);
                    } else {
                        dir.normalize();
                    }
                } else {
                    dir.normalize();
                }
            }

            plugin.gliders.put(id, new GlideState(base, dir.getX(), dir.getZ(), multiplier));
        } else {
            // конец парения
            plugin.gliders.remove(id);
        }
    }
}
