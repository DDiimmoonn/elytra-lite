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
                gliders.remove(id);
                continue;
            }

            // Берём горизонтальную проекцию направления взгляда, чтобы не дергало при просмотре вверх/вниз
            Vector look = p.getLocation().getDirection().clone();
            look.setY(0);
            if (look.lengthSquared() < 1e-6) {
                // если практически нет горизонтального направления — используем ранее сохранённое
                look = new Vector(st.dirX, 0, st.dirZ);
                if (look.lengthSquared() < 1e-6) {
                    // всё ещё ноль — пропускаем
                    continue;
                }
            }
            look.normalize();

            // Сглаживаем направление (чтобы не дергало резко при резком повороте)
            double smooth = 0.25; // 0..1 — чем выше, тем быстрее поворот
            st.dirX = st.dirX * (1.0 - smooth) + look.getX() * smooth;
            st.dirZ = st.dirZ * (1.0 - smooth) + look.getZ() * smooth;

            Vector horizDir = new Vector(st.dirX, 0, st.dirZ);
            if (horizDir.lengthSquared() < 1e-6) continue;
            horizDir.normalize();

            // скорость по горизонтали (базовая * множитель)
            double speed = st.baseSpeed * st.multiplier;

            // ограничение максимальной горизонтальной скорости
            double maxHorizontal = 3.0;
            Vector targetVel = horizDir.multiply(speed);
            if (targetVel.length() > maxHorizontal) {
                targetVel = targetVel.normalize().multiply(maxHorizontal);
            }

            // Вертикальная составляющая: даём небольшой "подталкивающий вниз" эффект, чтобы нельзя было бесконечно держаться в воздухе.
            // Если игрок получает сильную вверх скорость (например фейерверком) — она будет применена, мы лишь добавим небольшую потерю.
            double currentY = p.getVelocity().getY();
            double downwardPull = 0.06; // сила опускания за тик (регулируйте)
            double newY = currentY - downwardPull;

            // Собираем итоговую скорость: горизонтальная из таргета, вертикальная — newY (но не резать слишком сильно, сохраняем сильные позитивные импульсы)
            targetVel.setY(newY);

            // Накладываем небольшое сглаживание на итоговую скорость, чтобы не дёргало
            Vector prev = p.getVelocity();
            double vmix = 0.6; // 0..1, чем выше — сильнее целевая скорость заменяет предыдущую
            Vector finalVel = new Vector(
                    prev.getX() * (1.0 - vmix) + targetVel.getX() * vmix,
                    prev.getY() * (1.0 - vmix) + targetVel.getY() * vmix,
                    prev.getZ() * (1.0 - vmix) + targetVel.getZ() * vmix
            );

            // Ещё ограничение на общую скорость
            double maxTotal = 4.0;
            if (finalVel.length() > maxTotal) {
                finalVel = finalVel.normalize().multiply(maxTotal);
            }

            p.setVelocity(finalVel);
        }
    }
}
