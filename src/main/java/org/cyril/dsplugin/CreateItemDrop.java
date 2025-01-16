package org.cyril.dsplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CreateItemDrop extends Command {
    protected CreateItemDrop(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(strings.length == 1) {
                if(strings[0].equals("common") || strings[0].equals("rare") || strings[0].equals("legendary")) {
                    ItemStack drop = player.getInventory().getItemInMainHand();
                    if (drop.isEmpty()) {
                        player.sendMessage("You need to hold an item.");
                    } else {
                        Location location = player.getLocation();
                        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                        armorStand.setArms(true);
                        armorStand.setItem(EquipmentSlot.CHEST, drop);
                        armorStand.addScoreboardTag("itemDrop");
                        armorStand.addScoreboardTag(strings[0]);
                        armorStand.setInvisible(true);
                        armorStand.setSmall(true);
                    }
                } else {
                    player.sendMessage("Wrong command usage. /itemdrop common/rare/legendary");
                }
            } else {
                player.sendMessage("Wrong command usage. /itemdrop common/rare/legendary");
            }
        } else {
            commandSender.sendMessage("Only players can use this command.");
        }
        return true;
    }
}
