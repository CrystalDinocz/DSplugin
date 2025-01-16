package org.cyril.dsplugin;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Grace {
    private static void itemParticles(Entity entity, Color color) {
        Particle.DustOptions dust1 = new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1F);
        entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 0.5, 0), 2, dust1);
        Particle.DustOptions dust2 = new Particle.DustOptions(color, 0.8F);
        entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 0.65, 0), 2, dust2);
        Particle.DustOptions dust3 = new Particle.DustOptions(color, 0.65F);
        entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 0.6, 0), 3, dust3);
        Particle.DustOptions dust4 = new Particle.DustOptions(color, 0.6F);
        entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 0.8, 0), 2, dust4);
        Particle.DustOptions dust5 = new Particle.DustOptions(color, 0.85F);
        entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 0.55, 0), 1, dust5);
        Particle.DustOptions dust6 = new Particle.DustOptions(color, 0.4F);
        entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 0.9, 0), 2, dust6);
        Particle.DustOptions dust7 = new Particle.DustOptions(color, 0.35F);
        entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 1, 0), 2, dust7);
        Particle.DustOptions dust10 = new Particle.DustOptions(Color.WHITE, 0.35F);
        entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 0.8, 0), 1, dust10);
        Particle.DustOptions dust8 = new Particle.DustOptions(Color.WHITE, 0.3F);
        entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 0.9, 0), 1, dust8);
        Particle.DustOptions dust9 = new Particle.DustOptions(Color.WHITE, 0.25F);
        entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 1, 0), 1, dust9);
    }
    public static void graceParticles() {
        BukkitRunnable particleRepeat = new BukkitRunnable() {
            @Override
            public void run() {
                List<Entity> graceSites = Bukkit.selectEntities(Bukkit.getConsoleSender(), "@e[tag=grace]");
                for(Entity entity : graceSites) {
                    if(!entity.getNearbyEntities(20,20,20).isEmpty()) {
                        for(Entity entity2 : entity.getNearbyEntities(20,20,20)) {
                            if(entity2 instanceof Player) {
                                Particle.DustOptions dust1 = new Particle.DustOptions(Color.fromRGB(255,241,42), 1F);
                                entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0,0.5,0), 2, dust1);
                                Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(255,255,255), 0.6F);
                                entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0,0.5,0), 4, dust2);
                                Particle.DustOptions dust3 = new Particle.DustOptions(Color.fromRGB(255,241,42), 0.7F);
                                entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0,0.7,0), 2, dust3);
                            }
                        }
                    }
                }
                List<Entity> riposteIndicators = Bukkit.selectEntities(Bukkit.getConsoleSender(), "@e[tag=riposteIndicator]");
                for(Entity entity : riposteIndicators) {
                    Particle.DustOptions dust1 = new Particle.DustOptions(Color.fromRGB(255,241,42), 1F);
                    entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0,0.5,0), 2, dust1);
                    Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(255,255,255), 0.5F);
                    entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0,0.5,0), 4, dust2);
                }
                List<Entity> itemDrops = Bukkit.selectEntities(Bukkit.getConsoleSender(), "@e[tag=itemDrop]");
                for(Entity entity : itemDrops) {
                    Color color = Color.WHITE;
                    if (entity.getScoreboardTags().contains("rare")) {
                        color = Color.fromRGB(150, 34, 185);
                    } else if (entity.getScoreboardTags().contains("legendary")) {
                        color = Color.fromRGB(245, 196, 38);
                    }
                    itemParticles(entity, color);
                }
            }
        };
        particleRepeat.runTaskTimer(Dsplugin.getInstance(), 0,0);
    }
}
