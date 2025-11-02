package com.cahel.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class RegisterCommand implements CommandExecutor {
    private final AuthManager auth;

    public RegisterCommand(AuthManager auth) {
        this.auth = auth;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getInstance().getMessage("only_players"));
            return true;
        }
        Player p = (Player) sender;

        if (args.length != 1) {
            p.sendMessage(Main.getInstance().getMessage("register_usage"));
            return true;
        }

        if (auth.isRegistered(p.getUniqueId())) {
            p.sendMessage(Main.getInstance().getMessage("already_registered"));
            return true;
        }

        auth.register(p, args[0]);
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.sendMessage(Main.getInstance().getMessage("registered_success"));
        return true;
    }
}
