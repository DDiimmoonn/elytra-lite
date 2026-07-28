package ru.ddiimmoonn.elytraspeed;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ElytraSpeedPlugin extends JavaPlugin {
    public static NamespacedKey LEVEL_KEY;
    // Map игроков, которые в полёте -> состояние (baseSpeed, dirX, dirZ, multiplier)
    final ConcurrentHashMap<UUID, GlideState> gliders = new ConcurrentHashMap<>();
    private GlideScheduler scheduler;

    @Override
    public void onEnable() {
        // загрузка конфигурации (если нет — сохранит дефолтную из resources)
        saveDefaultConfig();
        LEVEL_KEY = new NamespacedKey(this, "elytra_level");

        // инициализация утилит
        ItemUtils.setPlugin(this);

        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        getServer().getPluginManager().registerEvents(new GlideListener(this), this);
        if (getCommand("gelytra") != null) getCommand("gelytra").setExecutor(new GiveCommand(this));

        scheduler = new GlideScheduler(this, gliders);
        scheduler.start();
        getLogger().info("ElytraSpeed enabled");
    }

    @Override
    public void onDisable() {
        if (scheduler != null) scheduler.stop();
        gliders.clear();
        getLogger().info("ElytraSpeed disabled");
    }
}
