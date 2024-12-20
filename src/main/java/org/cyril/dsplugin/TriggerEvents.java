package org.cyril.dsplugin;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TriggerEvents implements Listener {
    Dsplugin dsInstance = new Dsplugin();
    Test testInstance = new Test(dsInstance);
    HashMap<String, Float> stats = dsInstance.getStats();
    HashMap<String, Location> baseLocation = new HashMap<String, Location>();
    int duration = 0;
    public void uchigatana(Player player) {
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
        swordMeta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(new NamespacedKey(Dsplugin.getInstance(), "attackSpeed"), -2.5, AttributeModifier.Operation.ADD_NUMBER));
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        sword.setItemMeta(swordMeta);
        player.getInventory().setItem(player.getInventory().firstEmpty(), sword);
    }
    public void graceMenu(Player player) {
        List<Component> Lore = new ArrayList<>();
        AttributeModifier dummyAttribute = new AttributeModifier(new NamespacedKey(Dsplugin.getInstance(), "dummy"), 0, AttributeModifier.Operation.ADD_NUMBER);
        Inventory graceInventory = Bukkit.createInventory(null, 27, Component.text("Menu", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        ItemStack graceItem1 = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta graceMeta1 = graceItem1.getItemMeta();
        graceMeta1.displayName(Component.text("Level Up", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC,false));
        graceItem1.setItemMeta(graceMeta1);
        //VIG
        ItemStack graceItem2 = new ItemStack(Material.APPLE);
        ItemMeta graceMeta2 = graceItem2.getItemMeta();
        graceMeta2.displayName(Component.text("Vigor", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC,false));
        if(stats.get(player.getName() + "_vigor") < 50) {
            Lore.add(Component.text(Math.round(stats.get(player.getName() + "_vigor")) + " ➡ " + Math.round(stats.get(player.getName() + "_vigor") + 1), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        } else {
            Lore.add(Component.text("MAX LEVEL", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }
        graceMeta2.lore(Lore);
        Lore.clear();
        graceItem2.setItemMeta(graceMeta2);
        //END
        ItemStack graceItem3 = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta graceMeta3 = graceItem3.getItemMeta();
        graceMeta3.displayName(Component.text("Endurance", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false));
        if(stats.get(player.getName() + "_endurance") < 50) {
            Lore.add(Component.text(Math.round(stats.get(player.getName() + "_endurance")) + " ➡ " + Math.round(stats.get(player.getName() + "_endurance") + 1), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        } else {
            Lore.add(Component.text("MAX LEVEL", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }
        graceMeta3.lore(Lore);
        Lore.clear();
        graceItem3.setItemMeta(graceMeta3);
        //MIND
        ItemStack graceItem4 = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta graceMeta4 = graceItem4.getItemMeta();
        graceMeta4.displayName(Component.text("Mind", NamedTextColor.BLUE).decoration(TextDecoration.ITALIC,false));
        if(stats.get(player.getName() + "_mind") < 50) {
            Lore.add(Component.text(Math.round(stats.get(player.getName() + "_mind")) + " ➡ " + Math.round(stats.get(player.getName() + "_mind") + 1), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        } else {
            Lore.add(Component.text("MAX LEVEL", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }
        graceMeta4.lore(Lore);
        Lore.clear();
        graceItem4.setItemMeta(graceMeta4);
        //STR
        ItemStack graceItem5 = new ItemStack(Material.IRON_AXE);
        ItemMeta graceMeta5 = graceItem5.getItemMeta();
        graceMeta5.displayName(Component.text("Strength", NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC,false));
        if(stats.get(player.getName() + "_strength") < 50) {
            Lore.add(Component.text(Math.round(stats.get(player.getName() + "_strength")) + " ➡ " + Math.round(stats.get(player.getName() + "_strength") + 1), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        } else {
            Lore.add(Component.text("MAX LEVEL", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }
        graceMeta5.lore(Lore);
        Lore.clear();
        graceMeta5.addAttributeModifier(Attribute.LUCK, dummyAttribute);
        graceMeta5.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        graceItem5.setItemMeta(graceMeta5);
        //DEX
        ItemStack graceItem6 = new ItemStack(Material.IRON_SWORD);
        ItemMeta graceMeta6 = graceItem6.getItemMeta();
        graceMeta6.displayName(Component.text("Dexterity", NamedTextColor.RED).decoration(TextDecoration.ITALIC,false));
        if(stats.get(player.getName() + "_dexterity") < 50) {
            Lore.add(Component.text(Math.round(stats.get(player.getName() + "_dexterity")) + " ➡ " + Math.round(stats.get(player.getName() + "_dexterity") + 1), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        } else {
            Lore.add(Component.text("MAX LEVEL", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }
        graceMeta6.lore(Lore);
        Lore.clear();
        graceMeta6.addAttributeModifier(Attribute.LUCK, dummyAttribute);
        graceMeta6.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        graceItem6.setItemMeta(graceMeta6);
        //INT
        ItemStack graceItem7 = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta graceMeta7 = graceItem7.getItemMeta();
        graceMeta7.displayName(Component.text("Intelligence", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC,false));
        if(stats.get(player.getName() + "_intelligence") < 50) {
            Lore.add(Component.text(Math.round(stats.get(player.getName() + "_intelligence")) + " ➡ " + Math.round(stats.get(player.getName() + "_intelligence") + 1), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        } else {
            Lore.add(Component.text("MAX LEVEL", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }
        graceMeta7.lore(Lore);
        Lore.clear();
        graceItem7.setItemMeta(graceMeta7);
        //PLAYER STATS
        ItemStack playerStats = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerStats.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(Component.text("Your Stats", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false));
        if(stats.get(player.getName() + "_level") < 295) {
            Lore.add(Component.text("Level: " + Math.round(stats.get(player.getName() + "_level")) + " ➡ " + Math.round(stats.get(player.getName() + "_level") + 1), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        } else {
            Lore.add(Component.text("Level: " + Math.round(stats.get(player.getName() + "_level")), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        }
        Lore.add(Component.text(""));
        Lore.add(Component.text("Runes Held: " + Math.round(stats.get(player.getName() + "_runesHeld")), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text("Runes Needed: " + Math.round(stats.get(player.getName() + "_runesNeeded")), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(Lore);
        Lore.clear();
        playerStats.setItemMeta(skullMeta);
        //DUMMY
        ItemStack dummyItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta dummyMeta = dummyItem.getItemMeta();
        dummyMeta.displayName(Component.text(""));
        dummyItem.setItemMeta(dummyMeta);
        graceInventory.setItem(4, graceItem1);
        graceInventory.setItem(10, graceItem2);
        graceInventory.setItem(11, graceItem3);
        graceInventory.setItem(12, graceItem4);
        graceInventory.setItem(13, graceItem5);
        graceInventory.setItem(14, graceItem6);
        graceInventory.setItem(15, graceItem7);
        graceInventory.setItem(16, playerStats);
        //GLASS PANES
        graceInventory.setItem(0, dummyItem);
        graceInventory.setItem(1, dummyItem);
        graceInventory.setItem(2, dummyItem);
        graceInventory.setItem(3, dummyItem);
        graceInventory.setItem(5, dummyItem);
        graceInventory.setItem(6, dummyItem);
        graceInventory.setItem(7, dummyItem);
        graceInventory.setItem(8, dummyItem);
        graceInventory.setItem(9, dummyItem);
        graceInventory.setItem(17, dummyItem);
        for(int i = 18; i < 27; i++) {
            graceInventory.setItem(i, dummyItem);
        }
        player.openInventory(graceInventory);
    }
    public void grace(Player player) {
        BukkitTask sitDelay = new BukkitRunnable() {
            @Override
            public void run() {
                Location sitLocation = baseLocation.get(player.getName()).clone();
                sitLocation.subtract(0,1.6,0);
                Entity graceSit = player.getWorld().spawnEntity(sitLocation, EntityType.ARMOR_STAND);
                graceSit.addScoreboardTag(player.getName() + "_sit");
                graceSit.setGravity(false);
                graceSit.setInvisible(true);
                graceSit.addPassenger(player);
            }
        }.runTaskLater(Dsplugin.getInstance(), 0);
        graceMenu(player);
    }
    public void storeValues(Player player) {
        for(String tag : player.getScoreboardTags()) {
            if(tag.contains("rollCount_")) {
                Float lastValue = Float.valueOf(tag.replace("rollCount_", ""));
                stats.put(player.getName() + "_rollCount", lastValue);
            }
            if(tag.contains("endurance_")) {
                Float lastValue = Float.valueOf(tag.replace("endurance_", ""));
                stats.put(player.getName() + "_endurance", lastValue);
            }
            if(tag.contains("vigor_")) {
                Float lastValue = Float.valueOf(tag.replace("vigor_", ""));
                stats.put(player.getName() + "_vigor", lastValue);
            }
            if(tag.contains("strength_")) {
                Float lastValue = Float.valueOf(tag.replace("strength_", ""));
                stats.put(player.getName() + "_strength", lastValue);
            }
            if(tag.contains("dexterity_")) {
                Float lastValue = Float.valueOf(tag.replace("dexterity_", ""));
                stats.put(player.getName() + "_dexterity", lastValue);
            }
            if(tag.contains("mind_")) {
                Float lastValue = Float.valueOf(tag.replace("mind_", ""));
                stats.put(player.getName() + "_mind", lastValue);
            }
            if(tag.contains("intelligence_")) {
                Float lastValue = Float.valueOf(tag.replace("intelligence_", ""));
                stats.put(player.getName() + "_intelligence", lastValue);
            }
            if(tag.contains("runesHeld_")) {
                Float lastValue = Float.valueOf(tag.replace("runesHeld_", ""));
                stats.put(player.getName() + "_runesHeld", lastValue);
            }
            if(tag.contains("runesNeeded_")) {
                Float lastValue = Float.valueOf(tag.replace("runesNeeded_", ""));
                stats.put(player.getName() + "_runesNeeded", lastValue);
            }
            if(tag.contains("level_")) {
                Float lastValue = Float.valueOf(tag.replace("level_", ""));
                stats.put(player.getName() + "_level", lastValue);
            }

        }
        stats.putIfAbsent(player.getName() + "_rollCount", 1F);
        stats.putIfAbsent(player.getName() + "_endurance", 1F);
        stats.putIfAbsent(player.getName() + "_vigor", 1F);
        stats.putIfAbsent(player.getName() + "_strength", 1F);
        stats.putIfAbsent(player.getName() + "_dexterity", 1F);
        stats.putIfAbsent(player.getName() + "_mind", 1F);
        stats.putIfAbsent(player.getName() + "_intelligence", 1F);
        stats.putIfAbsent(player.getName() + "_runesHeld", 0F);
        stats.putIfAbsent(player.getName() + "_level", 1F);
        testInstance.setRunesNeeded(player.getName());
        player.getScoreboardTags().clear();
        player.addScoreboardTag("rollCount_" + stats.get(player.getName() + "_rollCount"));
        player.addScoreboardTag("endurance_" + stats.get(player.getName() + "_endurance"));
        player.addScoreboardTag("vigor_" + stats.get(player.getName() + "_vigor"));
        player.addScoreboardTag("strength_" + stats.get(player.getName() + "_strength"));
        player.addScoreboardTag("dexterity_" + stats.get(player.getName() + "_dexterity"));
        player.addScoreboardTag("mind_" + stats.get(player.getName() + "_mind"));
        player.addScoreboardTag("intelligence_" + stats.get(player.getName() + "_intelligence"));
        player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
        player.addScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
        player.addScoreboardTag("level_" + stats.get(player.getName() + "_level"));
    }
    @EventHandler
    public void onShift(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(event.isSneaking()) {
            BukkitRunnable Recovery = new BukkitRunnable() {
                @Override
                public void run() {
                    player.removeScoreboardTag("recovery");
                }
            };
            BukkitRunnable RollFrames = new BukkitRunnable() {
                @Override
                public void run() {
                    if (duration > 8) {
                        cancel();
                        player.removeScoreboardTag("iframe");
                        Recovery.runTaskLater(Dsplugin.getInstance(), 5);
                        player.clearActivePotionEffects();
                        if(player.getScoreboardTags().contains("rollCount_" + stats.get(player.getName() + "_rollCount"))) {
                            player.removeScoreboardTag("rollCount_" + stats.get(player.getName() + "_rollCount"));
                        }
                        stats.put(player.getName() + "_rollCount", stats.get(player.getName() + "_rollCount") + 1);
                        player.addScoreboardTag("rollCount_" + stats.get(player.getName() + "_rollCount"));
                        duration = 0;
                    } else {
                        player.setPose(Pose.SWIMMING);
                        player.setSneaking(false);
                        Rolling.Roll(player);
                        duration++;
                    }
                }
            };
            if(!player.getScoreboardTags().contains("recovery")) {
                if (player.getLocation().subtract(0, 0.3, 0).getBlock().isSolid()) {
                    if (stats.get(player.getName() + "_stamina") >= 1) {
                        stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 12);
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
        }
    }
    @EventHandler
    public void onPlayerSwing(PlayerArmSwingEvent event) {
        Player player = event.getPlayer();
        if(!player.getScoreboardTags().contains("iframe")) {
            if(stats.get(player.getName() + "_stamina") >= 1) {
                try {
                    if (player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Straight Sword", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                        stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 10);
                    }
                    if (player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Greatsword", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                        stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 15);
                    }
                    if (player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().equals(Component.text("Katana", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))) {
                        stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 12);
                    }
                    if (player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Axe", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                        stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 13);
                    }
                    if (player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Hammer", NamedTextColor.DARK_GRAY))) {
                        stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 13);
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
        if(player.getScoreboardTags().contains("reset")) {
            player.getScoreboardTags().clear();
        }
        storeValues(player);
        testInstance.setMaxFP(player.getName());
        stats.put(player.getName() + "_FP", stats.get(player.getName() + "_maxFP"));
        testInstance.showFP(player.getName());
        stats.put(player.getName() + "_stamina", 0F);
        testInstance.setMaxStamina(player.getName());
        testInstance.staminaRegen(player.getName());
        player.sendMessage(stats.entrySet().toString());
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if(event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            if(event.getRightClicked().getScoreboardTags().contains("uchigatana")) {
                uchigatana(player);
            } else if (event.getRightClicked().getScoreboardTags().contains("grace")) {
                baseLocation.put(player.getName(), player.getLocation());
                grace(player);
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ItemStack graceCheck = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta graceMeta = graceCheck.getItemMeta();
        graceMeta.displayName(Component.text("Level Up", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        graceCheck.setItemMeta(graceMeta);
        if(event.getInventory().contains(graceCheck)) {
            String selector = String.format("@e[tag=%s_sit]", event.getPlayer().getName());
            for(Entity entity : Bukkit.selectEntities(Bukkit.getConsoleSender(), selector)) {
                entity.remove();
            }
            stats.put(player.getName() + "_FP", stats.get(player.getName() + "_maxFP"));
            testInstance.showFP(player.getName());
        }

    }
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        ItemStack graceCheck = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta graceMeta = graceCheck.getItemMeta();
        Player player = (Player) event.getWhoClicked();
        graceMeta.displayName(Component.text("Level Up", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        graceCheck.setItemMeta(graceMeta);
        if(event.getInventory().contains(graceCheck)) {
            event.setCancelled(true);
            try {
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Vigor", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC,false))) {
                    if(stats.get(player.getName() + "_vigor") < 50) {
                        if(stats.get(player.getName() + "_runesHeld") >= stats.get(player.getName() + "_runesNeeded")) {
                            player.removeScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            stats.put(player.getName() + "_runesHeld", stats.get(player.getName() + "_runesHeld") - stats.get(player.getName() + "_runesNeeded"));
                            player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            if (player.getScoreboardTags().contains("vigor_" + stats.get(player.getName() + "_vigor"))) {
                                player.removeScoreboardTag("vigor_" + stats.get(player.getName() + "_vigor"));
                                player.removeScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                                player.removeScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                            }
                            stats.put(player.getName() + "_vigor", stats.get(player.getName() + "_vigor") + 1);
                            player.addScoreboardTag("vigor_" + stats.get(player.getName() + "_vigor"));
                            stats.put(player.getName() + "_level", stats.get(player.getName() + "_level") + 1);
                            player.addScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                            testInstance.setRunesNeeded(player.getName());
                            player.addScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                        } else {
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.sendMessage(Component.text("Vigor is already at the maximum level.", NamedTextColor.RED));
                    }
                }
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Endurance", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false))) {
                    if(stats.get(player.getName() + "_endurance") < 50) {
                        if(stats.get(player.getName() + "_runesHeld") >= stats.get(player.getName() + "_runesNeeded")) {
                            player.removeScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            stats.put(player.getName() + "_runesHeld", stats.get(player.getName() + "_runesHeld") - stats.get(player.getName() + "_runesNeeded"));
                            player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            if (player.getScoreboardTags().contains("endurance_" + stats.get(player.getName() + "_endurance"))) {
                                player.removeScoreboardTag("endurance_" + stats.get(player.getName() + "_endurance"));
                                player.removeScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                                player.removeScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                            }
                            stats.put(player.getName() + "_endurance", stats.get(player.getName() + "_endurance") + 1);
                            player.addScoreboardTag("endurance_" + stats.get(player.getName() + "_endurance"));
                            stats.put(player.getName() + "_level", stats.get(player.getName() + "_level") + 1);
                            player.addScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                            testInstance.setRunesNeeded(player.getName());
                            player.addScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                            testInstance.setMaxStamina(player.getName());
                            testInstance.staminaRegen(player.getName());
                        } else {
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.sendMessage(Component.text("Endurance is already at the maximum level.", NamedTextColor.RED));
                    }
                }
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Mind", NamedTextColor.BLUE).decoration(TextDecoration.ITALIC,false))) {
                    if(stats.get(player.getName() + "_mind") < 50) {
                        if(stats.get(player.getName() + "_runesHeld") >= stats.get(player.getName() + "_runesNeeded")) {
                            player.removeScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            stats.put(player.getName() + "_runesHeld", stats.get(player.getName() + "_runesHeld") - stats.get(player.getName() + "_runesNeeded"));
                            player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            if (player.getScoreboardTags().contains("mind_" + stats.get(player.getName() + "_mind"))) {
                                player.removeScoreboardTag("mind_" + stats.get(player.getName() + "_mind"));
                                player.removeScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                                player.removeScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                            }
                            stats.put(player.getName() + "_mind", stats.get(player.getName() + "_mind") + 1);
                            player.addScoreboardTag("mind_" + stats.get(player.getName() + "_mind"));
                            stats.put(player.getName() + "_level", stats.get(player.getName() + "_level") + 1);
                            player.addScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                            testInstance.setRunesNeeded(player.getName());
                            player.addScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                            testInstance.setMaxFP(player.getName());
                            testInstance.showFP(player.getName());
                        } else {
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.sendMessage(Component.text("Mind is already at the maximum level.", NamedTextColor.RED));
                    }
                }
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Strength", NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC,false))) {
                    if(stats.get(player.getName() + "_strength") < 50) {
                        if(stats.get(player.getName() + "_runesHeld") >= stats.get(player.getName() + "_runesNeeded")) {
                            player.removeScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            stats.put(player.getName() + "_runesHeld", stats.get(player.getName() + "_runesHeld") - stats.get(player.getName() + "_runesNeeded"));
                            player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            if (player.getScoreboardTags().contains("strength_" + stats.get(player.getName() + "_strength"))) {
                                player.removeScoreboardTag("strength_" + stats.get(player.getName() + "_strength"));
                                player.removeScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                                player.removeScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                            }
                            stats.put(player.getName() + "_strength", stats.get(player.getName() + "_strength") + 1);
                            player.addScoreboardTag("strength_" + stats.get(player.getName() + "_strength"));
                            stats.put(player.getName() + "_level", stats.get(player.getName() + "_level") + 1);
                            player.addScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                            testInstance.setRunesNeeded(player.getName());
                            player.addScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                        } else {
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.sendMessage(Component.text("Strength is already at the maximum level.", NamedTextColor.RED));
                    }
                }
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Dexterity", NamedTextColor.RED).decoration(TextDecoration.ITALIC,false))) {
                    if(stats.get(player.getName() + "_dexterity") < 50) {
                        if(stats.get(player.getName() + "_runesHeld") >= stats.get(player.getName() + "_runesNeeded")) {
                            player.removeScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            stats.put(player.getName() + "_runesHeld", stats.get(player.getName() + "_runesHeld") - stats.get(player.getName() + "_runesNeeded"));
                            player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            if (player.getScoreboardTags().contains("dexterity_" + stats.get(player.getName() + "_dexterity"))) {
                                player.removeScoreboardTag("dexterity_" + stats.get(player.getName() + "_dexterity"));
                                player.removeScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                                player.removeScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                            }
                            stats.put(player.getName() + "_dexterity", stats.get(player.getName() + "_dexterity") + 1);
                            player.addScoreboardTag("dexterity_" + stats.get(player.getName() + "_dexterity"));
                            stats.put(player.getName() + "_level", stats.get(player.getName() + "_level") + 1);
                            player.addScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                            testInstance.setRunesNeeded(player.getName());
                            player.addScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                        } else {
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.sendMessage(Component.text("Dexterity is already at the maximum level.", NamedTextColor.RED));
                    }
                }
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Intelligence", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC,false))) {
                    if(stats.get(player.getName() + "_intelligence") < 50) {
                        if(stats.get(player.getName() + "_runesHeld") >= stats.get(player.getName() + "_runesNeeded")) {
                            player.removeScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            stats.put(player.getName() + "_runesHeld", stats.get(player.getName() + "_runesHeld") - stats.get(player.getName() + "_runesNeeded"));
                            player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                            if (player.getScoreboardTags().contains("intelligence_" + stats.get(player.getName() + "_intelligence"))) {
                                player.removeScoreboardTag("intelligence_" + stats.get(player.getName() + "_intelligence"));
                                player.removeScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                                player.removeScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                            }
                            stats.put(player.getName() + "_intelligence", stats.get(player.getName() + "_intelligence") + 1);
                            player.addScoreboardTag("intelligence_" + stats.get(player.getName() + "_intelligence"));
                            stats.put(player.getName() + "_level", stats.get(player.getName() + "_level") + 1);
                            player.addScoreboardTag("level_" + stats.get(player.getName() + "_level"));
                            testInstance.setRunesNeeded(player.getName());
                            player.addScoreboardTag("runesNeeded_" + stats.get(player.getName() + "_runesNeeded"));
                        } else {
                            player.sendMessage(Component.text("You don't have enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.sendMessage(Component.text("Intelligence is already at the maximum level.", NamedTextColor.RED));
                    }
                }
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Your Stats", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false))) {
                    player.sendMessage(event.getCurrentItem().displayName());
                }
                grace(player);
            } catch (NullPointerException ignore) {
            }
        }
    }
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if(stats.get(player.getName() + "_FP") >= 12) {
            stats.put(player.getName() + "_FP", stats.get(player.getName() + "_FP") - 12);
            player.sendMessage("Current FP: " + stats.get(player.getName() + "_FP"));
            testInstance.showFP(player.getName());
        } else {
            player.sendMessage("Not enough FP.");
        }
    }
    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if(event.getDamageSource().getCausingEntity() instanceof Player) {
            Player player = (Player) event.getDamageSource().getCausingEntity();
            if(player.getScoreboardTags().contains("runesHeld_" + stats.get(player.getName() + "_runesHeld"))) {
                player.removeScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
            }
            stats.put(player.getName() + "_runesHeld", stats.get(player.getName() + "_runesHeld") + 1000);
            player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
        }
    }
}
