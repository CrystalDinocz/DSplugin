package org.cyril.dsplugin;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Test {
    HashMap<String, Integer> taskID = new HashMap<String, Integer>();
    BossBar staminaBar = Bukkit.createBossBar("§2Stamina", BarColor.GREEN, BarStyle.SOLID);
    Dsplugin dsInstance;
    public Test(Dsplugin dsplugin) {
        dsInstance = dsplugin;
    }
    public void showStamina(String name) {
        HashMap<String, Float> stats = dsInstance.getStats();
        Player player = Bukkit.getPlayer(name);
        if(stats.get(name + "_stamina") < 0) {
            float progress = 0;
            staminaBar.setProgress(progress);
            staminaBar.setVisible(true);
            if(Bukkit.getOnlinePlayers().contains(player)) {
                staminaBar.addPlayer(player);
            }
        } else if (stats.get(name + "_stamina") > stats.get(name + "_maxStamina")) {
            float progress = 1;
            staminaBar.setProgress(progress);
            staminaBar.setVisible(true);
            if(Bukkit.getOnlinePlayers().contains(player)) {
                staminaBar.addPlayer(player);
            }
        } else {
            float progress = (float) stats.get(name + "_stamina") / stats.get(name + "_maxStamina");
            staminaBar.setProgress(progress);
            staminaBar.setVisible(true);
            if (Bukkit.getOnlinePlayers().contains(player)) {
                staminaBar.addPlayer(player);
            }
        }
    }
    public void setMaxStamina(String name) {
        HashMap<String, Float> stats = dsInstance.getStats();
        float maxStamina = 100;
        for(float a = 1; a <= stats.get(name + "_endurance"); a = a + 1) {
            if(a != 1.0) {
                if(a <= 30.0) {
                    if(a % 2 == 0) {
                        maxStamina = maxStamina + 2;
                    } else {
                        maxStamina = maxStamina + 1;
                    }
                } else {
                    maxStamina = maxStamina + 1;
                }
            }
            if(a == stats.get(name + "_endurance")) {
                stats.put(name + "_maxStamina", maxStamina);
            }
        }
    }
    public void staminaRegen(String name) {
        HashMap<String, Float> stats = dsInstance.getStats();
        Player player = Bukkit.getPlayer(name);
        BukkitRunnable repeat = new BukkitRunnable() {
            @Override
            public void run() {
                if(stats.get(name + "_stamina") >= stats.get(name + "_maxStamina")) {
                    stats.put(name + "_stamina", stats.get(name + "_maxStamina"));
                    showStamina(name);
                    cancel();
                } else {
                    stats.put(name + "_stamina", stats.get(name + "_stamina") + (stats.get(name + "_maxStamina") / 56));
                    showStamina(name);
                }
            }
        };
        try {
            Bukkit.getServer().getScheduler().cancelTask(taskID.get("lastRepeat"));
//            player.sendMessage("Canceled task " + taskID.get("lastRepeat"));
        } catch (NullPointerException ignore) {
        }
        repeat.runTaskTimer(Dsplugin.getInstance(), 40, 1);
        showStamina(name);
//        player.sendMessage("Started task " + repeat.getTaskId());
        taskID.put("lastRepeat", repeat.getTaskId());
    }
}
