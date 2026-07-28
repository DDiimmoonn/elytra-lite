package ru.ddiimmoonn.elytraspeed;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlideScheduler {
    private final Plugin plugin;
    private final ConcurrentHashMap<UUID, GlideState> gliders;
    private BukkitTask task;

    public GlideScheduler(Plugin plugin, ConcurrentHashMap<UUID, GlideState> gliders) {
        this.plugin = plugin;
        this.gliders = gliders;
    }

    public void start() {
        // запускаем тикер на 1 тик
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    private void tick() {
        if (gliders.isEmpty()) return;
        Iterator<Map.Entry<UUID, GlideState>> it = gliders.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, GlideState> e = it.next();
            UUID id = e.getKey();
            GlideState st = e.getValue();
            Player p = Bukkit.getPlayer(id);
            if (p == null || !p.isOnline() || !p.isGliding()) {
                it.remove();
                continue;
            }
            // обновляем скорость по направлению игрока, но сохраняем вертикальную компоненту
            Vector dir = p.getLocation().getDirection();
            dir.setY(0);
            if (dir.lengthSquared() < 1e-8) {
                dir = new Vector(st.dirX, 0, st.dirZ);
            } else {
                dir.normalize();
            }
            double targetSpeed = st.baseSpeed * st.multiplier;
            Vector vel = p.getVelocity();
            Vector newVel = new Vector(dir.getX() * targetSpeed, vel.getY(), dir.getZ() * targetSpeed);
            p.setVelocity(newVel);
            // обновим кеш направления
            st.dirX = dir.getX();
            st.dirZ = dir.getZ();
        }
    }
}
