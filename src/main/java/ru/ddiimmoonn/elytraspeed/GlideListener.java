package ru.ddiimmoonn.elytraspeed;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleGlideEvent;

public class GlideListener implements Listener {
    private final ElytraSpeedPlugin plugin;

    public GlideListener(ElytraSpeedPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerToggleGlide(PlayerToggleGlideEvent event) {
        Player p = event.getPlayer();
        if (event.isGliding()) {
            // Игрок только что начал планировать — добавим в map
            int level = 1;
            try {
                // попытка прочитать уровень элитры в броне (груди)
                level = ItemUtils.getElytraLevel(p.getInventory().getChestplate(), plugin);
            } catch (Exception ignored) {}
            double baseSpeed = 0.95; // базовая скорость (пример)
            double mult = ItemUtils.levelToMultiplier(level);
            GlideState st = new GlideState(baseSpeed, mult, p.getLocation().getDirection().getX(), p.getLocation().getDirection().getZ());
            plugin.gliders.put(p.getUniqueId(), st);
        } else {
            // Остановил планирование — убираем
            plugin.gliders.remove(p.getUniqueId());
        }
    }
}
