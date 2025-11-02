package com.cahel.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    private static Main instance;
    private AuthManager authManager;
    private AuthListener authListener;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public static final String BRAND = ChatColor.AQUA + "FaithAuth by " + ChatColor.LIGHT_PURPLE + "Milkuza 💫";

    @Override
    public void onEnable() {
        instance = this;

        // Vytvoří složku pluginu + messages.yml
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
            getLogger().info("Created default messages.yml ✅");
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        authManager = new AuthManager(this);
        authListener = new AuthListener(this, authManager);

        getServer().getPluginManager().registerEvents(authListener, this);
        getCommand("register").setExecutor(new RegisterCommand(authManager));
        getCommand("login").setExecutor(new LoginCommand(authManager));

        // 🪄 Brand message in console
        getLogger().info("===================================");
        getLogger().info("        FaithAuth plugin loaded     ");
        getLogger().info("        Made by Milkuza 💫         ");
        getLogger().info("===================================");

        getLogger().info("FaithAuth plugin enabled ✨");
    }

    @Override
    public void onDisable() {
        if (authManager != null) {
            authManager.save();
        }
        getLogger().info("FaithAuth plugin disabled 💨");
    }

    public static Main getInstance() {
        return instance;
    }

    public String getMessage(String key) {
        String prefix = messagesConfig.getString("prefix", "&7[&bFaithAuth&7] ");
        String msg = messagesConfig.getString(key, "&c[Missing message: " + key + "]");
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    public void reloadMessages() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        getLogger().info("Messages reloaded 🔄");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("faithauthhelp") || label.equalsIgnoreCase("faithme")) {
            sender.sendMessage(ChatColor.GRAY + "============== " + ChatColor.AQUA + "FaithAuth" + ChatColor.GRAY + " ==============");
            sender.sendMessage(ChatColor.YELLOW + "🔑 /register <password>" + ChatColor.GRAY + " - creates your account");
            sender.sendMessage(ChatColor.YELLOW + "🔒 /login <password>" + ChatColor.GRAY + " - logs you in");
            sender.sendMessage(ChatColor.GRAY + "--------------------------------------");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Made by Milkuza 💫");
            sender.sendMessage(ChatColor.GRAY + "======================================");
            return true;
        }
        return false;
    }
}
