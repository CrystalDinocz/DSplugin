package org.cyril.dsplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Set;

public class TriggerEvents implements Listener {
    Dsplugin dsInstance = new Dsplugin();
    Test testInstance = new Test(dsInstance);
    HashMap<String, Float> stamina = dsInstance.getStamina();
    int i = 0;
    int duration = 0;
    @EventHandler
    public void onShift(PlayerToggleSneakEvent event) {
        i++;
        Player player = event.getPlayer();
        BukkitRunnable Recovery = new BukkitRunnable() {
            @Override
            public void run() {
                player.removeScoreboardTag("recovery");
                player.sendMessage("recovered");
            }
        };
        BukkitRunnable RollFrames = new BukkitRunnable() {
            @Override
            public void run() {
                if(duration > 8) {
                    cancel();
                    player.removeScoreboardTag("iframe");
                    Recovery.runTaskLater(Dsplugin.getInstance(), 5);
                    player.clearActivePotionEffects();
                    duration = 0;
                } else {
                    player.setPose(Pose.SWIMMING);
                    player.setSneaking(false);
                    Rolling.Roll(player);
                    duration++;
                }
            }
        };
        if(i == 1 && !player.getScoreboardTags().contains("recovery")) {
            if(player.getLocation().subtract(0,0.3,0).getBlock().isSolid()) {
                stamina.putIfAbsent(player.getName() + "_stamina", 100F);
                if(stamina.get(player.getName() + "_stamina") >= 1) {
                    stamina.put(player.getName() + "_stamina", stamina.get(player.getName() + "_stamina") - 12);
                    testInstance.setMaxStamina(player.getName());
                    testInstance.staminaRegen(player.getName());
                    player.addScoreboardTag("recovery");
                    player.addScoreboardTag("iframe");
                    RollFrames.runTaskTimer(Dsplugin.getInstance(), 0, 1);
                } else {
                    player.sendMessage("Not enough stamina.");
                }
            }
        }
        if(i == 2) {
            i = 0;
        }
    }
    @EventHandler
    public void onEntityHurt(EntityDamageEvent event) {
        try {
            if (event.getEntity().getScoreboardTags().contains("iframe")) {
                event.setCancelled(true);
            }
            if (event.getDamageSource().getCausingEntity().getScoreboardTags().contains("iframe")) {
                event.setCancelled(true);
            }
        } catch(NullPointerException ignore) {
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        stamina.put(player.getName() + "_stamina", 0F);
        testInstance.setMaxStamina(player.getName());
        testInstance.staminaRegen(player.getName());
        Set<String> tags = player.getScoreboardTags();
        for(String n : tags) {
            player.removeScoreboardTag(n);
        }
    }
}
