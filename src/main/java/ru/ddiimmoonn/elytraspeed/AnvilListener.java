package ru.ddiimmoonn.elytraspeed;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {
    private final ElytraSpeedPlugin plugin;
    private final int MAX_LEVEL = 5;

    public AnvilListener(ElytraSpeedPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);

        if ((first == null || first.getType() != Material.ELYTRA) && (second == null || second.getType() != Material.ELYTRA)) return;

        int lvl1 = 0;
        int lvl2 = 0;

        if (first != null && first.getType() == Material.ELYTRA) {
            lvl1 = ItemUtils.getElytraLevel(first, plugin);
            if (lvl1 == 0) lvl1 = 1;
        }
        if (second != null && second.getType() == Material.ELYTRA) {
            lvl2 = ItemUtils.getElytraLevel(second, plugin);
            if (lvl2 == 0) lvl2 = 1;
        }

        if (lvl1 <= 0 && lvl2 <= 0) return;

        int resultLevel = lvl1 + lvl2;
        if (resultLevel > MAX_LEVEL) resultLevel = MAX_LEVEL;

        ItemStack result = ItemUtils.createElytra(resultLevel);
        event.setResult(result);
    }
}
