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

    public static void setPlugin(Plugin p) {
        plugin = p;
    }

    // Создать "специальную" элитру с уровнем
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

    // Проверить — наша ли это элитра (с нашим ключом)
    public static boolean isCustomElytra(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.ELYTRA) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        NamespacedKey key = ElytraSpeedPlugin.LEVEL_KEY;
        if (key == null) return false;
        return meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }

    // Получить уровень (вернёт 0 если не установлено)
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

    public static void setElytraLevel(ItemStack item, int level) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        NamespacedKey key = ElytraSpeedPlugin.LEVEL_KEY;
        if (key != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, level);
            // обновим lore (имя оставляем без уровня)
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "Level: " + ChatColor.GOLD + level);
            lore.add(ChatColor.GRAY + "Custom Elytra by ElytraSpeed");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    // Преобразование уровня в множитель скорости (подберите формулу)
    public static double levelToMultiplier(int level) {
        // пример: +10% скорости за уровень
        return 1.0 + (level * 0.10);
    }
}
