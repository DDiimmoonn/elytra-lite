package ru.ddiimmoonn.elytraspeed;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class GlideListener implements Listener {
    private final ElytraSpeedPlugin plugin;

    public GlideListener(ElytraSpeedPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        boolean isGliding = p.isGliding();

        if (isGliding) {
            GlideState st = plugin.gliders.get(id);
            if (st == null) {
                // игрок только начал планировать — добавляем
                int level = 1;
                try {
                    level = ItemUtils.getElytraLevel(p.getInventory().getChestplate(), plugin);
                } catch (Exception ignored) {}

                double baseSpeed = 0.95; // пример значения, подкорректируйте при необходимости
                double mult = ItemUtils.levelToMultiplier(level);
                st = new GlideState(baseSpeed, mult,
                        p.getLocation().getDirection().getX(),
                        p.getLocation().getDirection().getZ());
                plugin.gliders.put(id, st);
            } else {
                // обновляем направление, пока игрок планирует
                st.dirX = p.getLocation().getDirection().getX();
                st.dirZ = p.getLocation().getDirection().getZ();
            }
        } else {
            // если перестал планировать — удаляем
            plugin.gliders.remove(id);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // очистка на выходе
        plugin.gliders.remove(event.getPlayer().getUniqueId());
    }
}
