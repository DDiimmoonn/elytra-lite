package ru.ddiimmoonn.elytraspeed;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand implements CommandExecutor {
    private final ElytraSpeedPlugin plugin;

    public GiveCommand(ElytraSpeedPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Usage: /gelytra <player>");
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found: " + args[0]);
                return true;
            }
        }

        ItemStack el = ItemUtils.createElytra(1); // всегда L1 по требованию
        target.getInventory().addItem(el);
        sender.sendMessage("Given custom Elytra (level 1) to " + target.getName());
        if (target != sender && target.isOnline()) target.sendMessage("You received a custom Elytra (level 1).");
        return true;
    }
}
