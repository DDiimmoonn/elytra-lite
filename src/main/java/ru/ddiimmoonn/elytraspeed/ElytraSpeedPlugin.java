package ru.ddiimmoonn.elytraspeed;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ElytraSpeedPlugin extends JavaPlugin {
    public static NamespacedKey LEVEL_KEY;
    final ConcurrentHashMap<UUID, GlideState> gliders = new ConcurrentHashMap<>();
    private GlideScheduler scheduler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Инициализация ключа PDC
        LEVEL_KEY = new NamespacedKey(this, "elytra_level");

        // Инициализация утилит
        ItemUtils.setPlugin(this);
        ItemUtils.ensureKey();

        // Регистрация слушателей
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new GlideListener(this), this);

        // Регистрация команды
        if (getCommand("gelytra") != null) getCommand("gelytra").setExecutor(new GiveCommand(this));
        else getLogger().warning("Command 'gelytra' not defined in plugin.yml");

        // Запуск планировщика
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
