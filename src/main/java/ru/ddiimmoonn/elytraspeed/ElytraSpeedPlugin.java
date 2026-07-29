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

        // Инициализируем ключ для PersistentDataContainer
        LEVEL_KEY = new NamespacedKey(this, "elytra_level");

        // Инициализация утилит
        ItemUtils.setPlugin(this);
        ItemUtils.ensureKey();

        // Регистрируем слушатели
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        getServer().getPluginManager().registerEvents(new GlideListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

        // Регистрируем команду (проверьте plugin.yml, чтобы команда была объявлена)
        if (getCommand("gelytra") != null) getCommand("gelytra").setExecutor(new GiveCommand(this));

        // Запускаем планировщик
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
