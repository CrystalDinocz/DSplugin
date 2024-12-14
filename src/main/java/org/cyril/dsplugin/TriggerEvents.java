package org.cyril.dsplugin;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
    public void onPlayerSwing(PlayerArmSwingEvent event) {
        Player player = event.getPlayer();
        if(!player.getScoreboardTags().contains("iframe")) {
            if(stamina.get(player.getName() + "_stamina") >= 1) {
                try {
                    if (player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Straight Sword", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                        stamina.put(player.getName() + "_stamina", stamina.get(player.getName() + "_stamina") - 10);
                        player.sendMessage("Straight Sword");
                    }
                    if (player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Greatsword", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                        stamina.put(player.getName() + "_stamina", stamina.get(player.getName() + "_stamina") - 15);
                        player.sendMessage("Greatsword");
                    }
                    if (player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().equals(Component.text("Katana", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))) {
                        stamina.put(player.getName() + "_stamina", stamina.get(player.getName() + "_stamina") - 12);
                        player.sendMessage("Katana");
                    }
                    if (player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Axe", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                        stamina.put(player.getName() + "_stamina", stamina.get(player.getName() + "_stamina") - 13);
                        player.sendMessage("Axe");
                    }
                    if (player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Hammer", NamedTextColor.DARK_GRAY))) {
                        stamina.put(player.getName() + "_stamina", stamina.get(player.getName() + "_stamina") - 13);
                        player.sendMessage("Hammer");
                    }
                    testInstance.setMaxStamina(player.getName());
                    testInstance.staminaRegen(player.getName());
                } catch (NullPointerException ignore) {
                }
            }
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
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if(event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            ItemStack sword = new ItemStack(Material.IRON_SWORD);
            ItemMeta swordMeta = sword.getItemMeta();
            List<Component> swordLore = new ArrayList<>();
            swordLore.add(Component.text("A katana with a long single-edged curved blade.", NamedTextColor.WHITE, TextDecoration.ITALIC));
            swordLore.add(Component.text("A unique weapon wielded by the samurai from the Land of Reeds.", NamedTextColor.WHITE, TextDecoration.ITALIC));
            swordLore.add(Component.text("The blade, with its undulating design, boasts extraordinary sharpness, and its slash attacks cause blood loss.", NamedTextColor.WHITE, TextDecoration.ITALIC));
            swordLore.add(Component.text(" "));
            swordLore.addLast(Component.text("Katana", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
            swordMeta.lore(swordLore);
            swordMeta.displayName(Component.text("Uchigatana", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            swordMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(new NamespacedKey(Dsplugin.getInstance(), "attackSpeed"), -2.5, AttributeModifier.Operation.ADD_NUMBER));
            swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            sword.setItemMeta(swordMeta);
            player.getInventory().setItem(player.getInventory().firstEmpty(), sword);
        }
    }
}
