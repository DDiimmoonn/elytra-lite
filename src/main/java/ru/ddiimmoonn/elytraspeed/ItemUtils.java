package ru.ddiimmoonn.elytraspeed;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {
    private static ElytraSpeedPlugin pluginInstance;

    public static void setPlugin(ElytraSpeedPlugin plugin) {
        pluginInstance = plugin;
    }

    public static int getElytraLevel(ItemStack item, Plugin plugin) {
        if (item == null) return 0;
        if (item.getType() != Material.ELYTRA) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;
        NamespacedKey key = ElytraSpeedPlugin.LEVEL_KEY;
        Integer v = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        if (v == null) return 1; // ванильная элитра = уровень 1
        return Math.max(1, Math.min(5, v));
    }

    public static ItemStack createElytraWithLevel(int level, Plugin plugin) {
        level = Math.max(1, Math.min(5, level));
        ItemStack ely = new ItemStack(Material.ELYTRA);
        ItemMeta meta = ely.getItemMeta();
        if (meta == null) return ely;
        NamespacedKey key = ElytraSpeedPlugin.LEVEL_KEY;
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, level);
        double mult = levelToMultiplier(level);
        meta.setDisplayName(ChatColor.AQUA + "Элитра [ур. " + level + "]");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Уровень: " + level);
        lore.add(ChatColor.GRAY + "Множитель скорости: " + (int) Math.round(mult * 100) + "%");
        meta.setLore(lore);
        ely.setItemMeta(meta);
        return ely;
    }

    public static double levelToMultiplier(int level) {
        // Attempt to read multipliers from config (list of 5 doubles), fall back to defaults
        double[] defaults = {0.5, 0.7, 0.9, 1.1, 1.3};
        if (pluginInstance != null) {
            try {
                List<?> cfg = pluginInstance.getConfig().getList("multipliers");
                if (cfg != null && cfg.size() >= 5) {
                    double[] cfgVals = new double[5];
                    for (int i = 0; i < 5; i++) {
                        Object o = cfg.get(i);
                        if (o instanceof Number) cfgVals[i] = ((Number) o).doubleValue();
                        else cfgVals[i] = defaults[i];
                    }
                    int idx = Math.max(1, Math.min(5, level)) - 1;
                    return cfgVals[idx];
                }
            } catch (Exception ignored) {
            }
        }
        int cl = Math.max(1, Math.min(5, level));
        return defaults[cl - 1];
    }
}
