package org.cyril.dsplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

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
        if(stats.get(name + "_endurance") <= 15) {
            int maxStamina = (int) (80 + (25 * ((stats.get(name + "_endurance") - 1) / 14)));
            stats.put(name + "_maxStamina", (float) maxStamina);
        }
        if(stats.get(name + "_endurance") >= 16 && stats.get(name + "_endurance") <= 35) {
            int maxStamina = (int) (105 + (25 * ((stats.get(name + "_endurance") - 15) / 15)));
            stats.put(name + "_maxStamina", (float) maxStamina);
        }
        if(stats.get(name + "_endurance") >= 36) {
            int maxStamina = (int) (130 + (25 * ((stats.get(name + "_endurance") - 30) / 20)));
            stats.put(name + "_maxStamina", (float) maxStamina);
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
            Bukkit.getServer().getScheduler().cancelTask(taskID.get(name + "_lastRepeat"));
//            player.sendMessage("Canceled task " + taskID.get(name + "_lastRepeat"));
        } catch (NullPointerException ignore) {
        }
        repeat.runTaskTimer(Dsplugin.getInstance(), 40, 1);
        showStamina(name);
//        player.sendMessage("Started task " + repeat.getTaskId());
        taskID.put(name + "_lastRepeat", repeat.getTaskId());
    }
    public void poiseRegen(String uuid) {
        HashMap<String, Float> stats = dsInstance.getStats();
        int regenDelay = (int) ((stats.get(uuid + "_maxPoise") / 13) * 20);
        BukkitRunnable repeat = new BukkitRunnable() {
            @Override
            public void run() {
                float poisePerTick = 0.65F;
                try {
                    Boolean livingCheck = Bukkit.getEntity(UUID.fromString(uuid)).isDead();
                } catch (NullPointerException nullPointerException) {
                    Bukkit.broadcast(Component.text("Entity died"));
                    cancel();
                    return;
                }
                if(stats.get(uuid + "_poise") + poisePerTick >= stats.get(uuid + "_maxPoise")) {
                    stats.put(uuid + "_poise", stats.get(uuid + "_maxPoise"));
                    Bukkit.broadcast(Component.text(stats.get(uuid + "_poise") + "/" + stats.get(uuid + "_maxPoise")));
                    cancel();
                } else {
                    stats.put(uuid + "_poise", stats.get(uuid + "_poise") + poisePerTick);
                    Bukkit.broadcast(Component.text(stats.get(uuid + "_poise") + "/" + stats.get(uuid + "_maxPoise")));
                }
            }
        };
        try {
            Bukkit.getScheduler().cancelTask(taskID.get(uuid + "_poiseRegen"));
        } catch (NullPointerException ignore) {
        }
        repeat.runTaskTimer(Dsplugin.getInstance(), regenDelay, 1);
        Bukkit.broadcast(Component.text(stats.get(uuid + "_poise") + "/" + stats.get(uuid + "_maxPoise")));
        taskID.put(uuid + "_poiseRegen", repeat.getTaskId());
    }
}
