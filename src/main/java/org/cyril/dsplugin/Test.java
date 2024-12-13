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
    float baseMaxStamina = 100;
    BossBar staminaBar = Bukkit.createBossBar("§2Stamina", BarColor.GREEN, BarStyle.SOLID);
    float finalMaxStamina = baseMaxStamina + 20;
    Dsplugin dsInstance;
    public Test(Dsplugin dsplugin) {
        dsInstance = dsplugin;
    }
    public void showStamina(String name) {
        HashMap<String, Float> stamina = dsInstance.getStamina();
        Player player = Bukkit.getPlayer(name);
        if(stamina.get(name + "_stamina") < 0) {
            float progress = 0;
            staminaBar.setProgress(progress);
            staminaBar.setVisible(true);
            if(Bukkit.getOnlinePlayers().contains(player)) {
                staminaBar.addPlayer(player);
            }
        } else if (stamina.get(name + "_stamina") > stamina.get(name + "_maxStamina")) {
            float progress = 1;
            staminaBar.setProgress(progress);
            staminaBar.setVisible(true);
            if(Bukkit.getOnlinePlayers().contains(player)) {
                staminaBar.addPlayer(player);
            }
        } else {
            float progress = (float) stamina.get(name + "_stamina") / stamina.get(name + "_maxStamina");
            staminaBar.setProgress(progress);
            staminaBar.setVisible(true);
            if (Bukkit.getOnlinePlayers().contains(player)) {
                staminaBar.addPlayer(player);
            }
        }
    }
    public void setMaxStamina(String name) {
        HashMap<String, Float> stamina = dsInstance.getStamina();
        stamina.put(name + "_maxStamina", finalMaxStamina);
        Bukkit.getPlayer(name).sendMessage(stamina.entrySet().toString());
    }
    public void staminaRegen(String name) {
        HashMap<String, Float> stamina = dsInstance.getStamina();
        Player player = Bukkit.getPlayer(name);
        BukkitRunnable repeat = new BukkitRunnable() {
            @Override
            public void run() {
                if(stamina.get(name + "_stamina") >= stamina.get(name + "_maxStamina")) {
                    stamina.put(name + "_stamina", stamina.get(name + "_maxStamina"));
                    showStamina(name);
                    cancel();
                } else {
                    stamina.put(name + "_stamina", stamina.get(name + "_stamina") + (stamina.get(name + "_maxStamina") / 56));
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
