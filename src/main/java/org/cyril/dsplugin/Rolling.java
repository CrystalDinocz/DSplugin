package org.cyril.dsplugin;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Rolling {
    public static void Roll(Player player) {
        Vector pVector = player.getLocation().getDirection();
        pVector.setY(0);
        pVector.normalize();
        pVector.multiply(0.15);
        pVector.setY(-0.5);
        player.setVelocity(pVector);
    }
}
