package de.quasarhafen.otobo;

import de.quasarhafen.otobo.command.SupportCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class OtoboPlugin extends JavaPlugin {

    private OtoboService service;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Service initialisieren
        service = new OtoboService(this);

        // Command registrieren
        SupportCommand command = new SupportCommand(this);
        getCommand("support").setExecutor(command);
        getCommand("support").setTabCompleter(command);

        getLogger().info("OtoboPlugin aktiviert.");
    }

    public OtoboService getService() {
        return service;
    }
}