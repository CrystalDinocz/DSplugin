package org.cyril.dsplugin;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Grace {
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
                List<Entity> riposteIndicators = Bukkit.selectEntities(Bukkit.getConsoleSender(), "@e[tag=stanceBroken]");
                for(Entity entity : riposteIndicators) {
                    Particle.DustOptions dust1 = new Particle.DustOptions(Color.fromRGB(255,241,42), 1F);
                    entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0,0.5,0), 2, dust1);
                    Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(255,255,255), 0.5F);
                    entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0,0.5,0), 4, dust2);
                }
            }
        };
        particleRepeat.runTaskTimer(Dsplugin.getInstance(), 0,0);
    }
}
