package de.quasarhafen.otobo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class OtoboService {

    private final OtoboPlugin plugin;
    private final HttpClient client;
    private String sessionID;

    public OtoboService(OtoboPlugin plugin) {
        this.plugin = plugin;

        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        createSession();
    }

    private Component colorize(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    public void createSession() {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try {

                FileConfiguration cfg = plugin.getConfig();

                String baseUrl = cfg.getString("otobo.base-url");
                String user = cfg.getString("otobo.agent-user");
                String password = cfg.getString("otobo.agent-password");

                String json = """
                {
                  "UserLogin": "%s",
                  "Password": "%s"
                }
                """.formatted(user, password);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/SessionCreate"))
                        .timeout(Duration.ofSeconds(5))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String body = response.body();
                    this.sessionID = body.split("\"")[3];
                    plugin.getLogger().info("Otobo Session erfolgreich erstellt.");
                } else {
                    plugin.getLogger().warning("Session Erstellung fehlgeschlagen.");
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Fehler bei Session Erstellung:");
                e.printStackTrace();
            }

        });
    }

    public void createTicket(Player player, String message) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try {

                if (sessionID == null) {
                    Bukkit.getScheduler().runTask(plugin,
                            () -> player.sendMessage(colorize("&cSupport System nicht bereit.")));
                    return;
                }

                FileConfiguration cfg = plugin.getConfig();

                String baseUrl = cfg.getString("otobo.base-url");
                String queue = cfg.getString("queue");

                String json = """
                {
                  "SessionID": "%s",
                  "Ticket": {
                    "Title": "Support von %s",
                    "Queue": "%s",
                    "State": "new",
                    "Priority": "3 normal",
                    "CustomerUser": "%s"
                  },
                  "Article": {
                    "Subject": "Minecraft Support",
                    "Body": "%s",
                    "ContentType": "text/plain; charset=utf8"
                  }
                }
                """.formatted(
                        sessionID,
                        player.getName(),
                        queue,
                        player.getUniqueId().toString(),
                        message.replace("\"", "")
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/TicketCreate"))
                        .timeout(Duration.ofSeconds(5))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 &&
                        response.body().contains("TicketNumber")) {

                    String body = response.body();
                    String ticketNumber = body.split("\"")[3];

                    String successMsg = cfg.getString("messages.success",
                                    "&aDein Ticket wurde erstellt. &7(#%ticket%)")
                            .replace("%ticket%", ticketNumber);

                    String notifyMsg = cfg.getString("messages.notify",
                                    "&eNeues Ticket von &f%player% &8(#%ticket%)")
                            .replace("%player%", player.getName())
                            .replace("%ticket%", ticketNumber);

                    Bukkit.getScheduler().runTask(plugin, () -> {

                        player.sendMessage(colorize(successMsg));

                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.hasPermission("otobo.admin")) {
                                p.sendMessage(colorize(notifyMsg));
                            }
                        }

                    });

                } else {

                    Bukkit.getScheduler().runTask(plugin,
                            () -> player.sendMessage(colorize("&cTicket konnte nicht erstellt werden.")));

                    plugin.getLogger().warning("Otobo Antwort: " + response.body());
                }

            } catch (Exception e) {

                plugin.getLogger().warning("Ticket Fehler:");
                e.printStackTrace();

                Bukkit.getScheduler().runTask(plugin,
                        () -> player.sendMessage(colorize("&cFehler beim Support-System.")));
            }

        });
    }
}