package org.cyril.dsplugin;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Dsplugin extends JavaPlugin {
    HashMap<String, Float> stats = new HashMap<String, Float>();
    public HashMap<String, Float> getStats() {
        return stats;
    }
    private static Dsplugin instance;
    private void commandRegister() {
        Bukkit.getCommandMap().register("test", new TestCommand("test"));
        Bukkit.getCommandMap().register("whistle", new TorrentWhistle("whistle"));
        Bukkit.getCommandMap().register("creategrace", new SummonGrace("creategrace"));
    }
    @Override
    public void onEnable() {
        commandRegister();
        instance = this;
        System.out.println("\nDS Plugin\nON");
        getServer().getPluginManager().registerEvents(new TriggerEvents(), this);
        Bukkit.getBossBars().forEachRemaining(BossBar::removeAll);
        Grace.graceParticles();
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.kick();
        }
        for(World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
        }
    }
    public static Dsplugin getInstance() {
        return instance;
    }
}

