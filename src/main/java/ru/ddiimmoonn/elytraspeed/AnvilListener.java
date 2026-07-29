package ru.ddiimmoonn.elytraspeed;

import org.bukkit.event.Listener;

/**
 * Заглушка AnvilListener — чтобы проект собирался.
 * Если у вас были более сложные обработчики наковальни, добавьте их сюда.
 */
public class AnvilListener implements Listener {
    private final ElytraSpeedPlugin plugin;

    public AnvilListener(ElytraSpeedPlugin plugin) {
        this.plugin = plugin;
    }

    // Можно добавить обработчики Событий наковальни по необходимости.
}
