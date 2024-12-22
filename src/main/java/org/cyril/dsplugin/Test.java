package org.cyril.dsplugin;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Test {
    HashMap<String, Integer> taskID = new HashMap<String, Integer>();
    BossBar staminaBar = Bukkit.createBossBar("§2Stamina", BarColor.GREEN, BarStyle.SOLID);
    BossBar FPBar = Bukkit.createBossBar("§9FP", BarColor.BLUE, BarStyle.SOLID);
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
    public void showFP(String name) {
        HashMap<String, Float> stats = dsInstance.getStats();
        Player player = Bukkit.getPlayer(name);
        float progress = (float) stats.get(name + "_FP") / stats.get(name + "_maxFP");
        FPBar.setProgress(progress);
        FPBar.setVisible(true);
        if (Bukkit.getOnlinePlayers().contains(player)) {
            FPBar.addPlayer(player);
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
    public void setMaxFP(String name) {
        HashMap<String, Float> stats = dsInstance.getStats();
        if(stats.get(name + "_mind") <= 15) {
            int maxFP = (int) (50 + (45*((stats.get(name + "_mind") - 1) / 14)));
            stats.put(name + "_maxFP", (float) maxFP);
        }
        if(stats.get(name + "_mind") >= 16 && stats.get(name + "_mind") <= 35) {
            int maxFP = (int) (95 + (105*((stats.get(name + "_mind") - 15) / 20)));
            stats.put(name + "_maxFP", (float) maxFP);
        }
        if(stats.get(name + "_mind") >= 36) {
            int maxFP = (int) (200 + (150 * (1 - Math.pow(1 - ((stats.get(name + "_mind") - 35) / 25), 1.2))));
            stats.put(name + "_maxFP", (float) maxFP);
        }
    }
    public void setMaxHP(String name) {
        HashMap<String, Float> stats = dsInstance.getStats();
        Player player = Bukkit.getPlayer(name);
        if(stats.get(name + "_vigor") <= 25) {
            int scaledHealth = (int) (300 + (500 * Math.pow((stats.get(name + "_vigor") - 1) / 24, 1.5)));
            double maxHealth = (double) scaledHealth / 15;
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
            player.setHealth(maxHealth);
        }
        if(stats.get(name + "_vigor") >= 26 && stats.get(name + "_vigor") <= 40) {
            int scaledHealth = (int) (800 + (650 * Math.pow((stats.get(name + "_vigor") - 25) / 15, 1.1)));
            double maxHealth = (double) scaledHealth / 15;
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
            player.setHealth(maxHealth);
        }
        if(stats.get(name + "_vigor") >= 41 && stats.get(name + "_vigor") <= 50) {
            int scaledHealth = (int) (1450 + (450 * (1 - (Math.pow(1 - ((stats.get(name + "_vigor") - 40) / 20), 1.2)))));
            double maxHealth = (double) scaledHealth / 15;
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
            player.setHealth(maxHealth);
        }
        player.sendMessage(String.valueOf(player.getHealth()));
    }
    public void setRunesNeeded(String name) {
        HashMap<String, Float> stats = dsInstance.getStats();
        double x = (((stats.get(name + "_level") + 81) - 92) * 0.02);
        if(x < 0) {
            x = 0;
        }
        int runesNeeded = (int) (((x + 0.1) * Math.pow(stats.get(name + "_level") + 81, 2)) + 1);
        stats.put(name + "_runesNeeded", (float) runesNeeded);
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
