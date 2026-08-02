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
                int level = ItemUtils.getElytraLevel(p.getInventory().getChestplate(), plugin);
                if (level <= 0) {
                    return;
                }
                double mult = ItemUtils.levelToMultiplier(level);

                // Инициализируем currentSpeed: берем текущую горизонтальную скорость игрока или малую долю базовой
                double curHor = Math.hypot(p.getVelocity().getX(), p.getVelocity().getZ());
                double fallback = GlideScheduler.BASE_SPEED * 0.18;
                double initialCurrent = Math.max(curHor, fallback);

                double dirX = p.getLocation().getDirection().getX();
                double dirZ = p.getLocation().getDirection().getZ();

                if (Math.abs(dirX) < 1e-6 && Math.abs(dirZ) < 1e-6) {
                    dirX = 0; dirZ = 0;
                }

                st = new GlideState(GlideScheduler.BASE_SPEED, mult, dirX, dirZ, initialCurrent);
                plugin.gliders.put(id, st);
            } else {
                double dirX = p.getLocation().getDirection().getX();
                double dirZ = p.getLocation().getDirection().getZ();
                if (!(Math.abs(dirX) < 1e-6 && Math.abs(dirZ) < 1e-6)) {
                    st.dirX = dirX;
                    st.dirZ = dirZ;
                }
            }
        } else {
            plugin.gliders.remove(id);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.gliders.remove(event.getPlayer().getUniqueId());
    }
}
