package ru.ddiimmoonn.elytraspeed;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlideScheduler {
    private final JavaPlugin plugin;
    private final ConcurrentHashMap<UUID, GlideState> gliders;
    private int taskId = -1;

    public static final double BASE_SPEED = 0.6;

    private final double ACCEL_PER_TICK = 0.06;
    private final double GRAVITY_PULL = 0.04;
    private final double DOWN_LOOK_BONUS = 0.55;
    private final double UP_LOOK_PENALTY = 0.10;
    private final double SMOOTH_DIR = 0.18;
    private final double VMIX = 0.6;
    private final double MAX_HORIZONTAL = 4.0;
    private final double MAX_TOTAL = 5.0;
    private final double UPWARD_PRESERVE_THRESHOLD = 0.55;

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
        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, GlideState> e : gliders.entrySet()) {
            UUID id = e.getKey();
            GlideState st = e.getValue();
            Player p = Bukkit.getPlayer(id);
            if (p == null || !p.isOnline() || !p.isGliding()) {
                toRemove.add(id);
                continue;
            }

            Vector prev = p.getVelocity();

            Vector look = p.getLocation().getDirection().clone();
            double lookY = look.getY();
            look.setY(0);
            if (look.lengthSquared() < 1e-6) {
                look = new Vector(st.dirX, 0, st.dirZ);
                if (look.lengthSquared() < 1e-6) continue;
            } else look.normalize();

            st.dirX = st.dirX * (1.0 - SMOOTH_DIR) + look.getX() * SMOOTH_DIR;
            st.dirZ = st.dirZ * (1.0 - SMOOTH_DIR) + look.getZ() * SMOOTH_DIR;

            Vector horizDir = new Vector(st.dirX, 0, st.dirZ);
            if (horizDir.lengthSquared() < 1e-6) continue;
            horizDir.normalize();

            double targetSpeed = BASE_SPEED * st.multiplier;

            if (lookY < 0) {
                double downFactor = -lookY;
                targetSpeed += targetSpeed * (DOWN_LOOK_BONUS * downFactor);
            } else if (lookY > 0) {
                double upFactor = lookY;
                targetSpeed -= targetSpeed * (0.25 * upFactor);
            }

            if (st.currentSpeed < targetSpeed) st.currentSpeed = Math.min(targetSpeed, st.currentSpeed + ACCEL_PER_TICK);
            else if (st.currentSpeed > targetSpeed) st.currentSpeed = Math.max(targetSpeed, st.currentSpeed - ACCEL_PER_TICK);

            Vector targetHor = horizDir.multiply(st.currentSpeed);
            if (targetHor.length() > MAX_HORIZONTAL) targetHor = targetHor.normalize().multiply(MAX_HORIZONTAL);

            double prevY = prev.getY();
            double newY;
            if (prevY > UPWARD_PRESERVE_THRESHOLD) newY = prevY;
            else {
                newY = prevY - GRAVITY_PULL;
                if (lookY > 0.02) {
                    newY -= UP_LOOK_PENALTY * lookY;
                }
            }

            Vector targetVel = new Vector(targetHor.getX(), newY, targetHor.getZ());

            Vector finalVel = new Vector(
                    prev.getX() * (1.0 - VMIX) + targetVel.getX() * VMIX,
                    prev.getY() * (1.0 - VMIX) + targetVel.getY() * VMIX,
                    prev.getZ() * (1.0 - VMIX) + targetVel.getZ() * VMIX
            );

            if (finalVel.length() > MAX_TOTAL) finalVel = finalVel.normalize().multiply(MAX_TOTAL);

            p.setVelocity(finalVel);
        }

        for (UUID id : toRemove) gliders.remove(id);
    }
}
