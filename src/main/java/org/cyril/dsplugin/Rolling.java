package org.cyril.dsplugin;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


public class Rolling {
    public static void Roll(Player player) {
        if(!player.getNearbyEntities(0.08,0.1,0.08).isEmpty()) {
            Vector minusVector = player.getLocation().getDirection();
            minusVector.setY(0);
            minusVector.normalize();
            minusVector.multiply(0.01);
            minusVector.setY(-0.5);
            player.setVelocity(minusVector);
            player.sendMessage("a");
        } else {
            Vector pVector = player.getLocation().getDirection();
            pVector.setY(0);
            pVector.normalize();
            pVector.multiply(0.3);
            pVector.setY(-0.5);
            player.setVelocity(pVector);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2, true, false));
    }
}
