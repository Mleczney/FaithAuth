package com.cahel.plugin;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.*;
import java.util.logging.Level;

public class AuthManager {
    private final Main plugin;
    private final File dataFile;
    private FileConfiguration cfg;

    // aktuálně ověření hráči (UUID string)
    private final Set<UUID> authed = new HashSet<>();

    public AuthManager(Main plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "players.yml");
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't create players.yml", e);
            }
        }
        cfg = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void save() {
        try {
            cfg.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save players.yml", e);
        }
    }

    public boolean isRegistered(UUID uuid) {
        return cfg.contains(uuid.toString());
    }

    public boolean checkPassword(UUID uuid, String password) {
        if (!isRegistered(uuid)) return false;
        String stored = cfg.getString(uuid.toString() + ".password", "");
        String hashed = hash(password);
        return stored.equals(hashed);
    }

    public void register(Player p, String password) {
        String uuid = p.getUniqueId().toString();
        cfg.set(uuid + ".password", hash(password));
        String ip = p.getAddress().getAddress().getHostAddress();
        cfg.set(uuid + ".ip", ip);
        save();
        authed.add(p.getUniqueId());
    }

    public void login(Player p) {
        // při úspěšném loginu aktualizujeme bound IP na aktuální (můžeš změnit)
        String uuid = p.getUniqueId().toString();
        String ip = p.getAddress().getAddress().getHostAddress();
        cfg.set(uuid + ".ip", ip);
        save();
        authed.add(p.getUniqueId());
    }

    public boolean isIpMatch(UUID uuid, String ip) {
        if (!isRegistered(uuid)) return false;
        String stored = cfg.getString(uuid.toString() + ".ip", "");
        return stored.equals(ip);
    }

    public boolean isAuthed(UUID uuid) {
        return authed.contains(uuid);
    }

    public void unauth(UUID uuid) {
        authed.remove(uuid);
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) {
                sb.append(String.format("%02x", x));
            }
            return sb.toString();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Hash error", e);
            return "";
        }
    }
}
