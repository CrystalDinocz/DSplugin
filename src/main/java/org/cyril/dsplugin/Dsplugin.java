package org.cyril.dsplugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Dsplugin extends JavaPlugin {
    HashMap<String, Float> stamina = new HashMap<String, Float>();
    public HashMap<String, Float> getStamina() {
        return stamina;
    }
    private static Dsplugin instance;
    @Override
    public void onEnable() {
        instance = this;
        System.out.println("\nDS Plugin\nON");
        getServer().getPluginManager().registerEvents(new TriggerEvents(), this);
        Bukkit.getBossBars().forEachRemaining(BossBar::removeAll);
        Grace.graceParticles();
        for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            Player player = offlinePlayer.getPlayer();
            if(player.isOnline()) {
                player.kick();
            }
        }
    }
    public static Dsplugin getInstance() {
        return instance;
    }
}

