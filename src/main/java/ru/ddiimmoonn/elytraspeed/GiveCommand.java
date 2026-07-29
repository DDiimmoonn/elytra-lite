package ru.ddiimmoonn.elytraspeed;

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
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is for players only.");
            return true;
        }
        Player player = (Player) sender;
        int level = 1;
        if (args.length > 0) {
            try {
                level = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException ignored) {}
        }
        ItemStack el = ItemUtils.createElytra(level);
        player.getInventory().addItem(el);
        player.sendMessage("Given custom Elytra (level " + level + ").");
        return true;
    }
}
