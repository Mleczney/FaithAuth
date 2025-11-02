package com.cahel.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AuthListener implements Listener {

    private final Main plugin;
    private final AuthManager auth;

    public AuthListener(Main plugin, AuthManager auth) {
        this.plugin = plugin;
        this.auth = auth;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (!auth.isRegistered(p.getUniqueId())) {
            p.sendMessage(plugin.getMessage("register_prompt"));
            freezePlayer(p);
            sendAuthTitle(p, "title_not_registered", "subtitle_not_registered");
            return;
        }

        String ip = p.getAddress().getAddress().getHostAddress();
        if (auth.isIpMatch(p.getUniqueId(), ip)) {
            auth.login(p);
            unfreezePlayer(p);
            p.sendMessage(plugin.getMessage("auto_login"));
            sendAuthTitle(p, "title_auto_login", "subtitle_auto_login");
        } else {
            p.sendMessage(plugin.getMessage("ip_mismatch"));
            freezePlayer(p);
            sendAuthTitle(p, "title_need_login", "subtitle_need_login");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        auth.unauth(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!auth.isAuthed(p.getUniqueId())) {
            if (e.getFrom().distanceSquared(e.getTo()) > 0.001) {
                e.setTo(e.getFrom());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!auth.isAuthed(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!auth.isAuthed(e.getPlayer().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!auth.isAuthed(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(plugin.getMessage("frozen_notice"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (!auth.isAuthed(p.getUniqueId())) e.setCancelled(true);
        }
    }

    private void freezePlayer(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false));
    }

    private void unfreezePlayer(Player p) {
        p.removePotionEffect(PotionEffectType.BLINDNESS);
    }

    private void sendAuthTitle(Player p, String titleKey, String subtitleKey) {
        String title = plugin.getMessage(titleKey);
        String subtitle = plugin.getMessage(subtitleKey);
        p.sendTitle(title, subtitle, 10, 60, 10);
    }
}
