package ru.ddiimmoonn.elytraspeed;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public final class ItemUtils {
    private static Plugin plugin;

    private ItemUtils() {}

    public static void setPlugin(Plugin p) {
        plugin = p;
    }

    public static void ensureKey() {
        if (ElytraSpeedPlugin.LEVEL_KEY == null && plugin != null) {
            ElytraSpeedPlugin.LEVEL_KEY = new NamespacedKey(plugin, "elytra_level");
        }
    }

    public static ItemStack createElytra(int level) {
        if (level < 1) level = 1;
        if (level > 5) level = 5;
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        ItemMeta meta = elytra.getItemMeta();
        if (meta == null) return elytra;

        meta.setDisplayName(ChatColor.AQUA + "Elytra");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Level: " + ChatColor.GOLD + level);
        lore.add(ChatColor.GRAY + "Custom Elytra by ElytraSpeed");
        meta.setLore(lore);

        NamespacedKey key = ElytraSpeedPlugin.LEVEL_KEY;
        if (key != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, level);
        }

        elytra.setItemMeta(meta);
        return elytra;
    }

    public static boolean isCustomElytra(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.ELYTRA) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        NamespacedKey key = ElytraSpeedPlugin.LEVEL_KEY;
        if (key == null) return false;
        return meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }

    public static int getElytraLevel(ItemStack item, Plugin fallback) {
        if (item == null) return 0;
        if (item.getType() != Material.ELYTRA) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 1; // ванильная элитра — считаем как L1
        NamespacedKey key = ElytraSpeedPlugin.LEVEL_KEY;
        if (key == null && fallback != null) key = new NamespacedKey(fallback, "elytra_level");
        if (key == null) return 1;
        Integer v = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        return v == null ? 1 : Math.max(1, Math.min(v, 5));
    }

    public static void setElytraLevel(ItemStack item, int level) {
        if (item == null) return;
        if (item.getType() != Material.ELYTRA) return;
        if (level < 1) level = 1;
        if (level > 5) level = 5;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        NamespacedKey key = ElytraSpeedPlugin.LEVEL_KEY;
        if (key != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, level);
        }
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Level: " + ChatColor.GOLD + level);
        lore.add(ChatColor.GRAY + "Custom Elytra by ElytraSpeed");
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    // Таблица уровней: L1:-50% (0.5), L2:-30% (0.7), L3:-10% (0.9), L4:+10% (1.1), L5:+30% (1.3)
    public static double levelToMultiplier(int level) {
        if (level <= 0) level = 1;
        if (level > 5) level = 5;
        switch (level) {
            case 1: return 0.50;
            case 2: return 0.70;
            case 3: return 0.90;
            case 4: return 1.10;
            case 5: return 1.30;
            default: return 1.0;
        }
    }
}
