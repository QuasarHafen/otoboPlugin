package de.quasarhafen.otobo;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class OtoboService {

    private final OtoboSupport plugin;
    private String sessionID;

    public OtoboService(OtoboSupport plugin) {
        this.plugin = plugin;
        createSession();
    }

    public void createSession() {
        try {
            FileConfiguration cfg = plugin.getConfig();
            URL url = new URL(cfg.getString("otobo.base-url") + "/SessionCreate");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String json = """
            {
              "UserLogin": "%s",
              "Password": "%s"
            }
            """.formatted(
                    cfg.getString("otobo.agent-user"),
                    cfg.getString("otobo.agent-password")
            );

            conn.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            String response = br.readLine();
            sessionID = response.split("\"")[3];

            plugin.getLogger().info("OTOBO Session erstellt.");
        } catch (Exception e) {
            plugin.getLogger().warning("Session fehlgeschlagen!");
        }
    }

    public String createTicket(Player player, String message) {
        try {
            FileConfiguration cfg = plugin.getConfig();
            URL url = new URL(cfg.getString("otobo.base-url") + "/TicketCreate");

            UUID uuid = player.getUniqueId();
            String customerUser = uuid.toString();

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
                "ContentType": "text/plain; charset=utf-8"
              }
            }
            """.formatted(sessionID, player.getName(),
                    cfg.getString("queue"),
                    customerUser,
                    message);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            conn.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            String response = br.readLine();

            if (response.contains("TicketNumber")) {
                return response.split("\"")[3];
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Ticket Fehler.");
        }
        return null;
    }
}
