package de.quasarhafen.otobo.command;

import de.quasarhafen.otobo.OtoboPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

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

        int cooldown = plugin.getConfig().getInt("cooldown-seconds");
        var cd = plugin.getCooldownManager();

        if (cd.isOnCooldown(player.getUniqueId(), cooldown)) {
            long left = cd.getRemaining(player.getUniqueId(), cooldown);
            player.sendMessage(color(plugin.getConfig()
                    .getString("messages.cooldown")
                    .replace("%time%", String.valueOf(left))));
            return true;
        }

        String message = String.join(" ", args);

        plugin.getOtoboClient().createTicket(player.getName(), message);
        cd.setCooldown(player.getUniqueId());

        player.sendMessage(color(plugin.getConfig().getString("messages.prefix")
                + plugin.getConfig().getString("messages.success")));

        // Admin Notify
        String notify = color(plugin.getConfig().getString("messages.notify")
                .replace("%player%", player.getName()));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("otobo.admin")) {
                p.sendMessage(color(plugin.getConfig().getString("messages.prefix") + notify));
            }
        }

        return true;
    }

    // Tab Completion
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return Collections.emptyList();
    }
}