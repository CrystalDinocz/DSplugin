package org.cyril.dsplugin;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
        Bukkit.getCommandMap().register("flasks", new GiveFlasks("flasks"));
        Bukkit.getCommandMap().register("blacksmith", new SmithingTable("blacksmith"));
        Bukkit.getCommandMap().register("stones", new GiveStones("stones"));
        Bukkit.getCommandMap().register("goldenseed", new GiveSeed("goldenseed"));
        Bukkit.getCommandMap().register("itemdrop", new CreateItemDrop("itemdrop"));
    }
    @Override
    public void onEnable() {
        instance = this;
        commandRegister();
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
        for(Entity entity : Bukkit.selectEntities(Bukkit.getConsoleSender(), "@e[type=!minecraft:armor_stand,type=!minecraft:player]")) {
            if(entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                String uuid = livingEntity.getUniqueId().toString();
                if(!livingEntity.getScoreboardTags().isEmpty()) {
                    for(String tag : livingEntity.getScoreboardTags()) {
                        if(tag.contains("maxPoise_")) {
                            float maxPoise = Float.parseFloat(tag.replace("maxPoise_", ""));
                            stats.put(uuid + "_maxPoise", maxPoise);
                            stats.put(uuid + "_poise", maxPoise);
                        }
                    }
                }
            }
        }
    }
    public static Dsplugin getInstance() {
        return instance;
    }
}