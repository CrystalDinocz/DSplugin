package org.cyril.dsplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;

public final class Dsplugin extends JavaPlugin {
    HashMap<String, Integer> stamina = new HashMap<String, Integer>();
    public HashMap<String, Integer> getStamina() {
        return stamina;
    }
    private static Dsplugin instance;
    @Override
    public void onEnable() {
        instance = this;
        System.out.println("\nDS Plugin\nON");
        getServer().getPluginManager().registerEvents(new TriggerEvents(), this);
        for(Player player : Bukkit.getOnlinePlayers()) {
            Set<String> tags = player.getScoreboardTags();
            for(String n : tags) {
                player.removeScoreboardTag(n);
            }
        }
    }
    public static Dsplugin getInstance() {
        return instance;
    }
}

