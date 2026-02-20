package de.quasarhafen.otobo.command;

import de.quasarhafen.otobo.OtoboPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SupportCommand implements CommandExecutor, TabCompleter {

    private final OtoboPlugin plugin;

    public SupportCommand(OtoboPlugin plugin) {
        this.plugin = plugin;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            player.sendMessage(color("&cNutze: /support <nachricht>"));
            return true;
        }

        String message = String.join(" ", args);

        player.sendMessage(color("&8[&bSupport&8] &7Ticket wird erstellt..."));

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String ticket = plugin.getService().createTicket(player, message);

            if (ticket != null) {
                player.sendMessage(color("&8[&bSupport&8] &aTicket erstellt: &e#" + ticket));

                // Admin Notify
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("otobo.admin")) {
                        p.sendMessage(color("&8[&bSupport&8] &e"
                                + player.getName() + " hat ein Ticket erstellt (#" + ticket + ")"));
                    }
                }
            } else {
                player.sendMessage(color("&8[&bSupport&8] &cFehler beim Erstellen des Tickets."));
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return Collections.emptyList();
    }
}