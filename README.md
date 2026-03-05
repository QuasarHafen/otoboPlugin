# OtoboSupport Minecraft Plugin

Ein Minecraft Plugin für **Purpur / Paper / Spigot**, das Spielern ermöglicht, direkt im Spiel **Support-Tickets in OTOBO** zu erstellen.

Das Plugin nutzt die **OTOBO GenericInterface REST API**, um automatisch Tickets zu erstellen.

---

# Features

* Support Tickets direkt aus Minecraft erstellen
* Integration mit der OTOBO REST API
* Spieler werden automatisch als Kunden identifiziert (UUID)
* Cooldown System gegen Spam
* Admin-Benachrichtigung im Chat
* konfigurierbare Nachrichten
* einfache YAML Konfiguration
* Tab Completion
* kompatibel mit **Purpur / Paper / Spigot**

---

# Voraussetzungen

Server:

* Minecraft **1.21+**
* Paper / Purpur / Spigot

OTOBO:

* OTOBO **10 oder 11**
* aktivierter **GenericInterface Webservice**
* TicketCreate Operation aktiviert

Java:

* **Java 21**

---

# Installation

## 1. Plugin bauen

Im Projektordner:

```bash
mvn clean package
```

Die fertige Datei liegt anschließend hier:

```
target/OtoboSupport-1.0.jar
```

---

## 2. Plugin installieren

Die JAR Datei in den Minecraft Plugins Ordner kopieren:

```
/plugins/OtoboSupport-1.0.jar
```

Server starten oder neu laden.

---

# Konfiguration

Nach dem ersten Start wird automatisch eine **config.yml** erstellt.

Pfad:

```
plugins/OtoboSupport/config.yml
```

Beispiel:

```yaml
otobo:
  base-url: "http://192.168.178.50/otobo/nph-genericinterface.pl/Webservice/MinecraftAPI"
  agent-user: "Otobo"
  agent-password: "AFGJM123"

queue: "Raw"

cooldown-seconds: 60

messages:
  prefix: "&6[Support] "
  success: "&aDein Ticket wurde erstellt."
  cooldown: "&cBitte warte noch %time% Sekunden."
  notify: "&eNeues Ticket von %player%"
```

---

# Konfigurationsoptionen

| Option           | Beschreibung             |
| ---------------- | ------------------------ |
| base-url         | URL zum OTOBO Webservice |
| agent-user       | OTOBO Agent Benutzer     |
| agent-password   | Passwort des Agents      |
| queue            | Queue für neue Tickets   |
| cooldown-seconds | Zeit zwischen Tickets    |
| messages         | Chat Nachrichten         |

---

# Befehl

```
/support <nachricht>
```

Beispiel:

```
/support Ich stecke in einem Block fest
```

Das Plugin erstellt automatisch ein Ticket.

---

# Funktionsweise

1. Spieler nutzt `/support`
2. Plugin sendet Anfrage an OTOBO
3. OTOBO erstellt Ticket
4. Ticketnummer wird gespeichert
5. Admins werden benachrichtigt

---

# Ticketstruktur

Beispiel Ticket:

```
Title: Support von PlayerName
Queue: Raw
State: new
Priority: 3 normal
CustomerUser: UUID
```

Artikel:

```
Subject: Minecraft Support
Body: Nachricht des Spielers
```

---

# Admin Benachrichtigung

Admins mit Permission:

```
otobo.admin
```

sehen:

```
[Support] Neues Ticket von PlayerName
```

---

# Projektstruktur

```
OtoboSupport
│
├─ src
│  └─ main
│     └─ java
│        └─ de/quasarhafen/otobo
│           ├─ OtoboPlugin.java
│           ├─ OtoboService.java
│           └─ command
│              └─ SupportCommand.java
│
├─ resources
│  ├─ plugin.yml
│  └─ config.yml
│
└─ pom.xml
```

---

# API Kommunikation

Das Plugin nutzt die OTOBO REST API.

Session erstellen:

```
POST /SessionCreate
```

Ticket erstellen:

```
POST /TicketCreate
```

---

# Sicherheit

Empfohlen:

* eigenen Agent Benutzer erstellen
* nur API Rechte vergeben
* HTTPS nutzen

---

# Kompatibilität

Getestet mit:

* Purpur 1.21
* Paper 1.21
* OTOBO 10
* OTOBO 11

---

# Lizenz

MIT License

---

# Autor
QuasarHafen
