package de.quasarhafen.otobo.command;

import de.quasarhafen.otobo.OtoboPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SupportCommand implements CommandExecutor, TabCompleter {

    private final OtoboPlugin plugin;

    public SupportCommand(OtoboPlugin plugin) {
        this.plugin = plugin;
    }

    private String color(String s) {
        return MiniMessage.miniMessage().serialize(MiniMessage.miniMessage().deserialize(s));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(color("&cNutze: /support <nachricht>"));
            return true;
        }

        String message = String.join(" ", args);

        player.sendMessage(color("&eTicket wird erstellt..."));

        plugin.getService().createTicket(player, message);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return Collections.emptyList();
    }
}