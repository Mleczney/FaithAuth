package com.cahel.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class LoginCommand implements CommandExecutor {
    private final AuthManager auth;

    public LoginCommand(AuthManager auth) {
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
            p.sendMessage(Main.getInstance().getMessage("login_usage"));
            return true;
        }

        if (!auth.isRegistered(p.getUniqueId())) {
            p.sendMessage(Main.getInstance().getMessage("not_registered"));
            return true;
        }

        boolean ok = auth.checkPassword(p.getUniqueId(), args[0]);
        if (!ok) {
            p.sendMessage(Main.getInstance().getMessage("wrong_password"));
            return true;
        }

        auth.login(p);
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.sendMessage(Main.getInstance().getMessage("login_success"));
        return true;
    }
}
