package de.quasarhafen.otobo.command;

import de.quasarhafen.otobo.OtoboPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SupportCommand implements CommandExecutor, TabCompleter, Listener {

    private final OtoboPlugin plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public SupportCommand(OtoboPlugin plugin) {
        this.plugin = plugin;
    }

    private Component colorize(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(colorize("&cNutze: /support <nachricht>"));
            return true;
        }

        int cooldownSeconds = plugin.getConfig().getInt("cooldown-seconds", 30);
        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();

        long lastUse = cooldowns.getOrDefault(uuid, 0L);
        long elapsed = (now - lastUse) / 1000;
        if (elapsed < cooldownSeconds) {
            long remaining = cooldownSeconds - elapsed;
            String cooldownMsg = plugin.getConfig()
                    .getString("messages.cooldown", "&cBitte warte noch %time% Sekunden.")
                    .replace("%time%", String.valueOf(remaining));
            player.sendMessage(colorize(cooldownMsg));
            return true;
        }

        cooldowns.put(uuid, now);

        String message = String.join(" ", args);

        player.sendMessage(colorize("&eTicket wird erstellt..."));

        plugin.getService().createTicket(player, message);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return Collections.emptyList();
    }
}