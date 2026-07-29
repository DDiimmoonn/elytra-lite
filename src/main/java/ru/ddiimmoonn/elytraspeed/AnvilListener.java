package ru.ddiimmoonn.elytraspeed;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {
    private final ElytraSpeedPlugin plugin;
    private final int MAX_LEVEL = 10; // предел уровня, при необходимости поменяй

    public AnvilListener(ElytraSpeedPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getItem(0); // left
        ItemStack second = event.getInventory().getItem(1); // right

        if (first == null || second == null) return;
        if (!ItemUtils.isCustomElytra(first) && !ItemUtils.isCustomElytra(second)) return;

        // Если один из предметов — наша элитра — формируем результат
        if (first.getType().name().equals("ELYTRA") && second.getType().name().equals("ELYTRA")) {
            int lvl1 = ItemUtils.getElytraLevel(first, plugin);
            int lvl2 = ItemUtils.getElytraLevel(second, plugin);

            // Если оба не наши — ничего не делаем
            if (lvl1 <= 0 && lvl2 <= 0) return;

            int resultLevel = Math.max(lvl1, lvl2); // базовый подход — взять max
            // если обе наши — можно суммировать, либо взять максимум — здесь суммируем, с капом
            if (lvl1 > 0 && lvl2 > 0) {
                resultLevel = Math.min(MAX_LEVEL, lvl1 + lvl2);
            } else if (lvl1 > 0) {
                resultLevel = lvl1;
            } else {
                resultLevel = lvl2;
            }

            ItemStack result = ItemUtils.createElytra(resultLevel);
            event.setResult(result);
        }
    }
}
