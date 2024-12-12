package org.cyril.dsplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Test {
    HashMap<String, Integer> taskID = new HashMap<String, Integer>();
    int baseMaxStamina = 100;
    int finalMaxStamina = baseMaxStamina + 20;
    Dsplugin dsInstance;
    public Test(Dsplugin dsplugin) {
        dsInstance = dsplugin;
    }
    public void setMaxStamina(String name) {
        HashMap<String, Integer> stamina = dsInstance.getStamina();
        stamina.put(name + "_maxStamina", finalMaxStamina);
        Bukkit.getPlayer(name).sendMessage(stamina.entrySet().toString());
    }
    public void staminaRegen(String name) {
        HashMap<String, Integer> stamina = dsInstance.getStamina();
        Player player = Bukkit.getPlayer(name);
        BukkitRunnable repeat = new BukkitRunnable() {
            @Override
            public void run() {
                if(stamina.get(name + "_stamina") >= stamina.get(name + "_maxStamina")) {
                    cancel();
                } else {
                    stamina.put(name + "_stamina", stamina.get(name + "_stamina") + 2);
                    player.sendMessage("Stamina: " + stamina.get(name + "_stamina"));
                }
            }
        };
        try {
            Bukkit.getServer().getScheduler().cancelTask(taskID.get("lastRepeat"));
            player.sendMessage("Canceled task " + taskID.get("lastRepeat"));
        } catch (NullPointerException ignore) {
        }
        repeat.runTaskTimer(Dsplugin.getInstance(), 50, 1);
        player.sendMessage("Started task " + repeat.getTaskId());
        taskID.put("lastRepeat", repeat.getTaskId());
    }
}
