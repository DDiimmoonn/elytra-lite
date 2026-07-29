package ru.ddiimmoonn.elytraspeed;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlideScheduler {
    private final JavaPlugin plugin;
    private final ConcurrentHashMap<UUID, GlideState> gliders;
    private int taskId = -1;

    public GlideScheduler(JavaPlugin plugin, ConcurrentHashMap<UUID, GlideState> gliders) {
        this.plugin = plugin;
        this.gliders = gliders;
    }

    public void start() {
        if (taskId != -1) return;
        // Run every tick
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L).getTaskId();
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        gliders.clear();
    }

    private void tick() {
        for (Map.Entry<UUID, GlideState> e : gliders.entrySet()) {
            UUID id = e.getKey();
            GlideState st = e.getValue();
            Player p = Bukkit.getPlayer(id);
            if (p == null || !p.isOnline()) {
                gliders.remove(id);
                continue;
            }
            if (!p.isGliding()) {
                // если перестал планировать — убираем
                gliders.remove(id);
                continue;
            }

            // Применяем скоростной эффект, но не меняем состояние планирования
            // Берём текущее направление и умножаем его на множитель
            Vector dir = p.getLocation().getDirection().clone();
            double speed = st.baseSpeed * st.multiplier;

            // Немного сглаживаем — чтобы не создавать читовую тягу, ограничим максимальную скорость
            double max = 3.0; // безопасный верх (при необходимости уменьшить)
            Vector vel = dir.multiply(speed);
            if (vel.length() > max) {
                vel = vel.normalize().multiply(max);
            }

            // Сохраняем вертикальную скорость (чтобы не мешать падению/взлёту слишком сильно)
            double currentY = p.getVelocity().getY();
            vel.setY(currentY);

            p.setVelocity(vel);
        }
    }
}
