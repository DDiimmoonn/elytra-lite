package ru.ddiimmoonn.elytraspeed;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;

public class GiveCommand implements CommandExecutor {
    private final Plugin plugin;
    public GiveCommand(Plugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("elytraspeed.give") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Использование: /gelytra <ник> <уровень 1-5>");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден.");
            return true;
        }
        int lvl;
        try {
            lvl = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Неверный уровень.");
            return true;
        }
        if (lvl < 1) lvl = 1;
        if (lvl > 5) lvl = 5;
        target.getInventory().addItem(ItemUtils.createElytraWithLevel(lvl, plugin));
        sender.sendMessage(ChatColor.GREEN + "Выдал элитру уровня " + lvl + " игроку " + target.getName());
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.GRAY + "Готово.");
        }
        return true;
    }
}
