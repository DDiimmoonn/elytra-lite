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
import java.util.Objects;

public final class ItemUtils {
    // Опционально хранится плагин, но ключ берём из ElytraSpeedPlugin.LEVEL_KEY
    private static Plugin plugin;

    private ItemUtils() {}

    public static void setPlugin(Plugin p) {
        plugin = p;
    }

    // Создать "нашу" элитру с level в PersistentDataContainer и lore (имя без уровня)
    public static ItemStack createElytra(int level) {
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        ItemMeta meta = elytra.getItemMeta();
        if (meta == null) return elytra;

        meta.setDisplayName(ChatColor.AQUA + "Elytra"); // имя без уровня

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

    // Проверка, является ли предмет нашей элитрой (по ключу)
    public static boolean isCustomElytra(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.ELYTRA) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        NamespacedKey key = ElytraSpeedPlugin.LEVEL_KEY;
        if (key == null) return false;
        return meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }

    // Получить уровень элитры (0 если нет)
    public static int getElytraLevel(ItemStack item, Plugin fallback) {
        if (item == null) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;
        NamespacedKey key = ElytraSpeedPlugin.LEVEL_KEY;
        if (key == null && fallback != null) key = new NamespacedKey(fallback, "elytra_level");
        if (key == null) return 0;
        Integer v = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        return v == null ? 0 : v;
    }

    // Установить уровень (и обновить lore)
    public static void setElytraLevel(ItemStack item, int level) {
        if (item == null) return;
        if (item.getType() != Material.ELYTRA) return;
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

    // Формула преобразования уровня в множитель скорости
    public static double levelToMultiplier(int level) {
        // Базовая логика: +10% к горизонтальной скорости за уровень
        // При необходимости измените коэффициент
        return 1.0 + (level * 0.10);
    }

    // Вспомогательный: безопасно установить плагин-ключ, если ElytraSpeedPlugin.LEVEL_KEY ещё не инициализирован
    public static void ensureKey() {
        if (ElytraSpeedPlugin.LEVEL_KEY == null && plugin != null) {
            ElytraSpeedPlugin.LEVEL_KEY = new NamespacedKey(plugin, "elytra_level");
        }
    }
}
