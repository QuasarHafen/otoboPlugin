package de.quasarhafen.otobo;

import org.bukkit.plugin.java.JavaPlugin;

public class OtoboPlugin extends JavaPlugin {

    private OtoboService service;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        service = new OtoboService(this);
        getCommand("support").setExecutor(new SupportCommand(this, service));
        getCommand("support").setTabCompleter(new SupportCommand(this, service));
        getLogger().info("OtoboPlugin aktiviert.");
    }

    public OtoboService getService() {
        return service;
    }
}
