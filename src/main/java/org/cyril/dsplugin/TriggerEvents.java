package org.cyril.dsplugin;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.*;

public class TriggerEvents implements Listener {
    Test testInstance = new Test(Dsplugin.getInstance());
    HashMap<String, Float> stats = Dsplugin.getInstance().getStats();
    HashMap<String, Location> baseLocation = new HashMap<String, Location>();
    HashMap<String, String> gracesDiscovered = new HashMap<String, String>();
    HashMap<String, Integer> taskID = new HashMap<String, Integer>();
    int duration = 0;
    public void summonTorrent(Player player) {
        Location pLocation = player.getLocation();
        Horse torrent = (Horse) player.getWorld().spawnEntity(pLocation, EntityType.HORSE);
        torrent.setAdult();
        torrent.setTamed(true);
        torrent.setAI(false);
        torrent.setColor(Horse.Color.GRAY);
        torrent.setStyle(Horse.Style.WHITE);
        torrent.setJumpStrength(0);
        torrent.getAttribute(Attribute.SAFE_FALL_DISTANCE).setBaseValue(8);
        torrent.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER).setBaseValue(3);
        torrent.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
        torrent.getAttribute(Attribute.MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue()/2);
        torrent.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        torrent.addScoreboardTag(player.getName() + "_torrent");
        player.addScoreboardTag("onTorrent");
        torrent.addPassenger(player);
        BukkitTask horseMount = new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(player, Sound.ENTITY_HORSE_SADDLE, 100, 1);
                player.playSound(player, Sound.ENTITY_HORSE_AMBIENT, 100, 1);
                player.addScoreboardTag("canHorseJump");
                player.addScoreboardTag("canHorseDismount");
            }
        }.runTaskLater(Dsplugin.getInstance(), 5);
    }
    public void torrentJump(Player player) {
        Horse torrent = (Horse) player.getVehicle();
        Vector pVector = player.getLocation().getDirection();
        if(player.getScoreboardTags().contains("canDoubleJump")) {
            player.removeScoreboardTag("canDoubleJump");
            pVector.setY(0);
            pVector.normalize();
            pVector.multiply(0.8);
            pVector.setY(0.6);
            torrent.setVelocity(pVector);
            player.playSound(player, Sound.ENTITY_HORSE_LAND, 0.3F, 1);
        } else {
            player.removeScoreboardTag("canHorseDismount");
            player.removeScoreboardTag("canHorseJump");
            pVector.setY(0);
            pVector.normalize();
            pVector.multiply(0.5);
            pVector.setY(0.6);
            torrent.setVelocity(pVector);
            player.playSound(player, Sound.ENTITY_HORSE_JUMP, 0.3F, 1);
            player.addScoreboardTag("canDoubleJump");
            BukkitTask horseLand = new BukkitRunnable() {
                @Override
                public void run() {
                    if(torrent.isOnGround()) {
                        player.addScoreboardTag("canHorseDismount");
                        player.addScoreboardTag("canHorseJump");
                        player.removeScoreboardTag("canDoubleJump");
                        cancel();
                    }
                }
            }.runTaskTimer(Dsplugin.getInstance(), 3,0);
        }
    }
    public void potionDrink(Player player, String potion, ItemStack itemStack) {
        float flaskPotency = stats.get(player.getName() + "_flaskPotency");
        if(!player.getScoreboardTags().contains("drinking")) {
            final int[] timer = {0};
            Location location = player.getLocation();
            BukkitRunnable drinking = new BukkitRunnable() {
                @Override
                public void run() {
                    if(timer[0] < 30) {
                        if (timer[0] % 6 == 0) {
                            player.playSound(player, Sound.ENTITY_GENERIC_DRINK, 1, 1);
                        }
                        timer[0]++;
                    } else {
                        player.playSound(player, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 2);
                        player.removeScoreboardTag("drinking");
                        if(potion.equals("crimson")) {
                            double currHP = player.getHealth();
                            double maxHP = player.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                            double flaskHP = getFlaskHP(flaskPotency);
                            player.sendMessage(String.valueOf(flaskHP));
                            if(maxHP - currHP > flaskHP) {
                                player.setHealth(currHP + flaskHP);
                                player.sendMessage("Healed " + flaskHP);
                            } else {
                                player.setHealth(maxHP);
                                player.sendMessage("Healed " + (maxHP - currHP));
                            }
                            setFlaskCharges(itemStack, stats.get(player.getName() + "_crimsonFlasks").intValue(), "HP", 1);
                        }
                        if(potion.equals("cerulean")) {
                            float currFP = stats.get(player.getName() + "_FP");
                            float maxFP = stats.get(player.getName() + "_maxFP");
                            float flaskFP = getFlaskFP(flaskPotency);
                            player.sendMessage(String.valueOf(flaskFP));
                            if(maxFP - currFP > flaskFP) {
                                stats.put(player.getName() + "_FP", currFP + flaskFP);
                                player.sendMessage("Restored " + flaskFP);
                            } else {
                                stats.put(player.getName() + "_FP", maxFP);
                                player.sendMessage("Restored " + (maxFP - currFP));
                            }
                            setFlaskCharges(itemStack, stats.get(player.getName() + "_ceruleanFlasks").intValue(), "HP", 1);
                            testInstance.showFP(player.getName());
                        }
                        cancel();
                    }
                }
            };
            player.addScoreboardTag("drinking");
            int flaskCharges = 0;
            for(Component line : itemStack.getItemMeta().lore()) {
                String string = ((TextComponent) line).content();
                try {
                    flaskCharges = Integer.parseInt(string);
                } catch (NumberFormatException ignore) {
                }
            }
            if(flaskCharges != 0) {
                PotionEffect effect = new PotionEffect(PotionEffectType.SLOWNESS, 30, 4, true, true, false);
                player.addPotionEffect(effect);
                drinking.runTaskTimer(Dsplugin.getInstance(), 0, 0);
                taskID.put(player.getName() + "_lastTask", drinking.getTaskId());
            } else {
                PotionEffect effect = new PotionEffect(PotionEffectType.SLOWNESS, 20, 4, true, true, false);
                player.addPotionEffect(effect);
                BukkitTask delay = new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.removeScoreboardTag("drinking");
                        player.sendMessage("This flask is empty.");
                        player.playSound(player, Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, 1, 1.2F);
                    }
                }.runTaskLater(Dsplugin.getInstance(), 20);
            }
        }
    }
    public void uchigatana(Player player) {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        List<Component> swordLore = new ArrayList<>();
        swordLore.add(Component.text("⚔ Attack Power", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Physical 115+  18", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Magic    0", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Fire     0", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        swordLore.add(Component.text(" "));
        swordLore.add(Component.text("💪 Attribute Scaling", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Str D     Dex D", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Int -", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        swordLore.add(Component.text(" "));
        swordLore.add(Component.text("👕 Passive Effects", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Causes blood loss buildup (45)", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("-", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("-", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        swordLore.add(Component.text(" "));
        swordLore.add(Component.text("Katana", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
        swordMeta.lore(swordLore);
        swordMeta.setUnbreakable(true);
        swordMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        swordMeta.displayName(Component.text("Uchigatana", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        swordMeta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(new NamespacedKey(Dsplugin.getInstance(), "attackSpeed"), -2.5, AttributeModifier.Operation.ADD_NUMBER));
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        sword.setItemMeta(swordMeta);
        player.getInventory().setItem(player.getInventory().firstEmpty(), sword);
        setItemLore(player, player.getInventory());
    }
    public void mainMenu(Player player) {
        Inventory graceInventory = Bukkit.createInventory(null, 9, Component.text("Site of Grace", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        ItemStack graceItem1 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta graceMeta1 = graceItem1.getItemMeta();
        graceMeta1.displayName(Component.text("Travel", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC,false));
        graceItem1.setItemMeta(graceMeta1);
        ItemStack graceItem2 = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta graceMeta2 = graceItem1.getItemMeta();
        graceMeta2.displayName(Component.text("Level Up", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC,false));
        graceItem2.setItemMeta(graceMeta2);
        ItemStack graceItem3 = new ItemStack(Material.POTION);
        PotionMeta graceMeta3 = ((PotionMeta) graceItem3.getItemMeta());
        graceMeta3.setColor(Color.RED);
        graceMeta3.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        graceMeta3.displayName(Component.text("Sacred Flasks", NamedTextColor.RED).decoration(TextDecoration.ITALIC,false));
        graceItem3.setItemMeta(graceMeta3);
        graceInventory.setItem(2, graceItem1);
        graceInventory.setItem(4, graceItem2);
        graceInventory.setItem(6, graceItem3);
        player.openInventory(graceInventory);
    }
    public void travelMenu(Player player) {
        int itemSlot = 0;
        List<Entity> graceSites = Bukkit.selectEntities(Bukkit.getConsoleSender(), "@e[tag=grace]");
        Inventory travelInventory = Bukkit.createInventory(null, 27, Component.text("Travel", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false));
        for(int topSlot = 0; topSlot <= 8; topSlot++) {
            if(topSlot == 4) {
                ItemStack graceItem = new ItemStack(Material.ENDER_PEARL);
                ItemMeta graceMeta = graceItem.getItemMeta();
                graceMeta.displayName(Component.text("Travel", NamedTextColor.DARK_AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
                graceItem.setItemMeta(graceMeta);
                travelInventory.setItem(topSlot, graceItem);
            } else {
                ItemStack dummyItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta dummyMeta = dummyItem.getItemMeta();
                dummyMeta.displayName(Component.text(""));
                dummyItem.setItemMeta(dummyMeta);
                travelInventory.setItem(topSlot, dummyItem);
            }
        }
        if(!graceSites.isEmpty()) {
            for(Entity grace : graceSites) {
                String[] stringList = grace.getName().split(" ");
                if(player.getScoreboardTags().contains("discovered_" + String.join("_", stringList))) {
                    List<Component> Lore = new ArrayList<>();
                    ItemStack graceItem = new ItemStack(Material.GOLD_NUGGET);
                    ItemMeta graceMeta = graceItem.getItemMeta();
                    graceMeta.displayName(Component.text(grace.getName(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                    Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                            .append(Component.keybind("key.attack", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                            .append(Component.text(" to travel to this Site of Grace.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
                    graceMeta.lore(Lore);
                    graceItem.setItemMeta(graceMeta);
                    travelInventory.setItem(itemSlot + 9, graceItem);
                    itemSlot++;
                }
            }
        }
        player.openInventory(travelInventory);
    }
    public void levelMenu(Player player) {
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
        Lore.add(Component.text("Runes Held: 💮 " + Math.round(stats.get(player.getName() + "_runesHeld")), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text("Runes Needed: 💮 " + Math.round(stats.get(player.getName() + "_runesNeeded")), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text(""));
        Lore.add(Component.text("HP: " + ((float) player.getAttribute(Attribute.MAX_HEALTH).getBaseValue()), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text("FP: " + stats.get(player.getName() + "_maxFP"), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text("Stamina: " + stats.get(player.getName() + "_maxStamina"), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text(""));
        TextComponent loreHead = Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                .append(Component.keybind("key.drop", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .append(Component.text(" to share your stats in chat.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));
        Lore.add(loreHead);
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
    public void flaskMenu(Player player) {
        List<Component> Lore = new ArrayList<>();
        float crimsonFlasks = stats.get(player.getName() + "_crimsonFlasks");
        float ceruleanFlasks = stats.get(player.getName() + "_ceruleanFlasks");
        int totalFlasks = (int) (crimsonFlasks + ceruleanFlasks);
        Inventory flaskGUI = Bukkit.createInventory(null, 27, Component.text("Flask Menu", NamedTextColor.RED));
        ItemStack itemStack1 = new ItemStack(new ItemStack(Material.LIME_WOOL));
        ItemMeta itemMeta1 = itemStack1.getItemMeta();
        itemMeta1.displayName(Component.text("Add charge to flask", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        if(totalFlasks >= 14) {
            Lore.add(Component.text("You already hold the maximum amount of flask charges", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        } else {
            Lore.add(Component.text("Cost", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            int flaskCost;
            if (totalFlasks <= 5) {
                flaskCost = 1;
            } else if (totalFlasks <= 7) {
                flaskCost = 2;
            } else if (totalFlasks <= 9) {
                flaskCost = 3;
            } else if (totalFlasks <= 11) {
                flaskCost = 4;
            } else {
                flaskCost = 5;
            }
            Lore.add(Component.text("Golden Seed  " + flaskCost, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            Lore.add(Component.text(""));
            Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                    .append(Component.keybind("key.attack", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .append(Component.text(" to add charge to flask.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        }
        itemMeta1.lore(Lore);
        Lore.clear();
        itemStack1.setItemMeta(itemMeta1);
        ItemStack itemStack2 = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta itemMeta2 = itemStack2.getItemMeta();
        itemMeta2.displayName(Component.text("Increase amount replenished by flasks", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        if(stats.get(player.getName() + "_flaskPotency") < 12) {
            Lore.add(Component.text("Cost", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            Lore.add(Component.text("Sacred Tear  1", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            Lore.add(Component.text(""));
            Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                    .append(Component.keybind("key.attack", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .append(Component.text(" to increase the amount replenished by flasks.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        } else {
            Lore.add(Component.text("The flaks potency is already increased to the utmost.", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }
        itemMeta2.lore(Lore);
        Lore.clear();
        itemStack2.setItemMeta(itemMeta2);
        ItemStack itemStack3 = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta itemMeta3 = itemStack3.getItemMeta();
        itemMeta3.displayName(Component.text("Allocate flask charges", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false));
        itemStack3.setItemMeta(itemMeta3);
        ItemStack itemStack4 = new ItemStack(Material.REDSTONE);
        ItemMeta itemMeta4 = itemStack4.getItemMeta();
        itemMeta4.displayName(Component.text("Flasks", NamedTextColor.RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        itemStack4.setItemMeta(itemMeta4);
        ItemStack dummy = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta dummyMeta = dummy.getItemMeta();
        dummyMeta.displayName(Component.text(""));
        dummy.setItemMeta(dummyMeta);
        flaskGUI.setItem(10, itemStack1);
        flaskGUI.setItem(13, itemStack2);
        flaskGUI.setItem(16, itemStack3);
        flaskGUI.setItem(4, itemStack4);
        for(int i = 0; i < 27; i++) {
            if(i != 10 && i != 13 && i != 16 && i != 4) {
                flaskGUI.setItem(i, dummy);
            }
        }
        player.openInventory(flaskGUI);
    }
    public void allocateMenu(Player player) {
        List<Component> Lore = new ArrayList<>();
        float crimsonFlasks = stats.get(player.getName() + "_crimsonFlasks");
        float ceruleanFlasks = stats.get(player.getName() + "_ceruleanFlasks");
        Inventory allocateGUI = Bukkit.createInventory(null, 27, Component.text("Please allocate your flask uses", NamedTextColor.YELLOW));
        ItemStack itemStack = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text("Allocate flask charges", NamedTextColor.DARK_AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        itemStack.setItemMeta(itemMeta);
        ItemStack itemStack1 = new ItemStack(Material.POTION);
        PotionMeta crimsonMeta = (PotionMeta) itemStack1.getItemMeta();
        crimsonMeta.setColor(Color.RED);
        crimsonMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        crimsonMeta.displayName(Component.text("Flask of Crimson Tears ", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false)
                .append(Component.text((int) crimsonFlasks, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                .append(Component.keybind("key.attack", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .append(Component.text(" to allocate 1 charge.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        crimsonMeta.lore(Lore);
        Lore.clear();
        crimsonMeta.setMaxStackSize(64);
        itemStack1.setItemMeta(crimsonMeta);
        if(crimsonFlasks != 0) {
            itemStack1.setAmount((int) crimsonFlasks);
        } else {
            itemStack1.setAmount(1);
        }
        ItemStack itemStack2 = new ItemStack(Material.POTION);
        PotionMeta ceruleanMeta = (PotionMeta) itemStack2.getItemMeta();
        ceruleanMeta.setColor(Color.BLUE);
        ceruleanMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        ceruleanMeta.displayName(Component.text("Flask of Cerulean Tears ", NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)
                .append(Component.text((int) ceruleanFlasks, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
        Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                .append(Component.keybind("key.attack", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .append(Component.text(" to allocate 1 charge.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        ceruleanMeta.lore(Lore);
        Lore.clear();
        ceruleanMeta.setMaxStackSize(64);
        itemStack2.setItemMeta(ceruleanMeta);
        if(ceruleanFlasks != 0) {
            itemStack2.setAmount((int) ceruleanFlasks);
        } else {
            itemStack2.setAmount(1);
        }
        ItemStack dummy = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta dummyMeta = dummy.getItemMeta();
        dummyMeta.displayName(Component.text(""));
        dummy.setItemMeta(dummyMeta);
        allocateGUI.setItem(4, itemStack);
        allocateGUI.setItem(10, itemStack1);
        allocateGUI.setItem(16, itemStack2);
        for(int i = 0; i < 27; i++) {
            if(i != 4 && i != 10 && i != 16) {
                allocateGUI.setItem(i, dummy);
            }
        }
        player.openInventory(allocateGUI);
    }
    public static void upgradeMenu(Player player) {
        Inventory upgradeGUI = Bukkit.createInventory(null, 27, Component.text("🔨 Smithing Table", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        List<Component> Lore = new ArrayList<>();
        AttributeModifier dummy = new AttributeModifier(new NamespacedKey(Dsplugin.getInstance(), "luck"), 1, AttributeModifier.Operation.ADD_NUMBER);
        ItemStack item1 = new ItemStack(Material.ANVIL);
        ItemMeta itemMeta1 = item1.getItemMeta();
        itemMeta1.displayName(Component.text("Smithing Table", NamedTextColor.GRAY, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        item1.setItemMeta(itemMeta1);
        ItemStack item2 = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta2 = item2.getItemMeta();
        itemMeta2.displayName(Component.text("Empty", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false)
                .append(Component.keybind("key.attack", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .append(Component.text(" on your armament to put it on the smithing table.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        itemMeta2.lore(Lore);
        item2.setItemMeta(itemMeta2);
        Lore.clear();
        ItemStack item3 = new ItemStack(Material.MACE);
        ItemMeta itemMeta3 = item3.getItemMeta();
        itemMeta3.displayName(Component.text("Upgrade Armament", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text("Required Items:", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text("-", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text(" "));
        Lore.add(Component.text("Cost:", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text("-", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text(""));
        Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false)
                .append(Component.keybind("key.attack", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .append(Component.text(" on this item to upgrade your armament.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        itemMeta3.lore(Lore);
        itemMeta3.addAttributeModifier(Attribute.LUCK, dummy);
        itemMeta3.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item3.setItemMeta(itemMeta3);
        ItemStack filler1 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack filler2 = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler1.getItemMeta();
        fillerMeta.displayName(Component.text(""));
        filler1.setItemMeta(fillerMeta);
        filler2.setItemMeta(fillerMeta);
        upgradeGUI.setItem(4, item1);
        upgradeGUI.setItem(10, item2);
        upgradeGUI.setItem(16, item2);
        upgradeGUI.setItem(13, item3);
        for(int i = 0; i < 27; i++) {
            if(i < 9 && i != 4) {
                upgradeGUI.setItem(i, filler1);
            }
            if(i >= 18) {
                upgradeGUI.setItem(i, filler1);
            }
        }
        upgradeGUI.setItem(9, filler1);
        upgradeGUI.setItem(17, filler1);
        upgradeGUI.setItem(11, filler2);
        upgradeGUI.setItem(12, filler2);
        upgradeGUI.setItem(14, filler2);
        upgradeGUI.setItem(15, filler2);
        player.openInventory(upgradeGUI);
    }
    public void grace(Player player) {
        if(player.getScoreboardTags().contains("deadTorrent")) {
            player.removeScoreboardTag("deadTorrent");
        }
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        stats.put(player.getName() + "_FP", stats.get(player.getName() + "_maxFP"));
        alterFlaskMeta(player);
        testInstance.showFP(player.getName());
        BukkitTask sitDelay = new BukkitRunnable() {
            @Override
            public void run() {
                Location sitLocation = baseLocation.get(player.getName()).clone();
                sitLocation.subtract(0,1.9,0);
                Entity graceSit = player.getWorld().spawnEntity(sitLocation, EntityType.ARMOR_STAND);
                graceSit.addScoreboardTag(player.getName() + "_sit");
                graceSit.setGravity(false);
                graceSit.setInvisible(true);
                graceSit.addPassenger(player);
            }
        }.runTaskLater(Dsplugin.getInstance(), 0);
    }
    public void graceTravel(Player player, InventoryClickEvent event) {
        ItemStack graceItem = event.getCurrentItem();
        PlainTextComponentSerializer plainText  = PlainTextComponentSerializer.plainText();
        String graceName = plainText.serialize(graceItem.getItemMeta().displayName());
        player.sendMessage(graceName);
        String selector = String.format("@e[tag=grace,name=\"%s\"]", graceName);
        Entity grace = Bukkit.selectEntities(Bukkit.getConsoleSender(), selector).getFirst();
        Location graceLocation = grace.getLocation();
        graceLocation.add(graceLocation.getDirection().setY(0).normalize().multiply(-1.5));
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1,0);
        player.teleport(graceLocation);
    }
    public void graceDiscovered(Player player) {
        Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(700));
        Title title = Title.title(Component.text("LOST GRACE DISCOVERED", NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false), Component.empty(), times);
        player.showTitle(title);
        player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1F, 0.4F);
    }
    public void setItemLore(Player player, Inventory inventory) {
        for(ItemStack item : inventory) {
            try {
                if (item.getItemMeta().lore().getLast().equals(Component.text("Straight Sword", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))) {

                }
                if (item.getItemMeta().lore().getLast().equals(Component.text("Great Sword", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))) {

                }
                if (item.getItemMeta().lore().getLast().equals(Component.text("Katana", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))) {
                    if(((TextComponent) item.getItemMeta().displayName()).content().contains("Uchigatana")) {
                        //DMG Calculation
                        String name = ((TextComponent) item.getItemMeta().displayName()).content();
                        int upgrade = 0;
                        name = name.replace("Uchigatana", "");
                        if(!name.isEmpty()) {
                            if(name.contains(" +")) {
                                name = name.replace(" +", "");
                                upgrade = Integer.parseInt(name);
                            }
                        }
                        int basePhy = 115;
                        if(upgrade % 3 == 1) {
                            basePhy = 121 + (20 * ((upgrade - 1) / 3));
                        }
                        if(upgrade % 3 == 2) {
                            basePhy = 128 + (20 * ((upgrade - 2) / 3));
                        }
                        if(upgrade % 3 == 0) {
                            basePhy = 115 + (20 * (upgrade / 3));
                        }
                        float dexAffinity = 0.55F + (0.01F * upgrade);
                        String dexSymbol = "-";
                        if(dexAffinity < 0.6)  {
                            dexSymbol = dexSymbol.replace("-", "D");
                        } else {
                            dexSymbol = dexSymbol.replace("-", "C");
                        }
                        float dexLevel = stats.get(player.getName() + "_dexterity") + 9;
                        float dexScaling = getDexScaling(dexLevel);
                        float dexDamage = dexAffinity * (basePhy * (dexScaling / 100));
                        float strAffinity = 0.3F + (0.006F * upgrade);
                        String strSymbol = "D";
                        float strLevel = stats.get(player.getName() + "_strength") + 9;
                        float strScaling = getStrScaling(strLevel);
                        float strDamage = strAffinity * (basePhy * (strScaling / 100));
                        int scalingDamage = Math.round(dexDamage + strDamage);
                        //Item Lore
                        List<Component> Lore = new ArrayList<>();
                        Lore.add(Component.text("⚔ Attack Power", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                        Lore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("Physical " + basePhy + "+  " + scalingDamage, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
                        Lore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("Magic    0", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
                        Lore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("Fire     0", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
                        Lore.add(Component.text(" "));
                        Lore.add(Component.text("💪 Attribute Scaling", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                        Lore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("Str " + strSymbol + "     Dex " + dexSymbol, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
                        Lore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("Int -", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
                        Lore.add(Component.text(" "));
                        Lore.add(Component.text("👕 Passive Effects", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                        Lore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("Causes blood loss buildup (45)", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
                        Lore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("-", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
                        Lore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("-", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
                        Lore.add(Component.text(" "));
                        Lore.add(Component.text("Katana", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.lore(Lore);
                        item.setItemMeta(itemMeta);
                    }
                }
                if (item.getItemMeta().lore().getLast().equals(Component.text("Axe", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))) {

                }
                if (item.getItemMeta().lore().getLast().equals(Component.text("Hammer", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))) {

                }
            } catch (NullPointerException ignore) {
            }
        }
    }
    public void setFlaskCharges(ItemStack itemStack, Integer flaskAmount, String stat, Integer reduction) {
        List<Component> Lore = new ArrayList<>();
        Lore.add(Component.text("Rest at a site of grace to replenish.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text(""));
        Lore.add(Component.text("Charges left", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        if(reduction == 0) {
            Lore.add(Component.text(flaskAmount, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        } else {
            int flaskCharges = 0;
            for(Component line : itemStack.getItemMeta().lore()) {
                String string = ((TextComponent) line).content();
                try {
                    flaskCharges = Integer.parseInt(string);
                } catch (NumberFormatException ignore) {
                }
            }
            flaskCharges = flaskCharges - 1;
            Lore.add(Component.text(flaskCharges, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        }
        Lore.add(Component.text(""));
        Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                .append(Component.keybind("key.use", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .append(Component.text(" to restore " + stat, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false)));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(Lore);
        itemStack.setItemMeta(itemMeta);
    }
    public void alterFlaskMeta(Player player) {
        float flaskPotency = stats.get(player.getName() + "_flaskPotency");
        for(ItemStack itemStack : player.getInventory().getContents()) {
            try {
                String name = ((TextComponent) itemStack.getItemMeta().displayName()).content();
                if(name.contains("Flask of Crimson Tears")) {
                    int flaskAmount = stats.get(player.getName() + "_crimsonFlasks").intValue();
                    setFlaskCharges(itemStack, flaskAmount, "HP", 0);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (flaskPotency == 0) {
                        itemMeta.displayName(Component.text("Flask of Crimson Tears", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false));
                    } else {
                        itemMeta.displayName(Component.text("Flask of Crimson Tears ", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("+" + (int) flaskPotency, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
                    }
                    itemStack.setItemMeta(itemMeta);
                }
                if(name.contains("Flask of Cerulean Tears")) {
                    int flaskAmount = stats.get(player.getName() + "_ceruleanFlasks").intValue();
                    setFlaskCharges(itemStack, flaskAmount, "MP", 0);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (flaskPotency == 0) {
                        itemMeta.displayName(Component.text("Flask of Cerulean Tears", NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false));
                    } else {
                        itemMeta.displayName(Component.text("Flask of Cerulean Tears ", NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("+" + (int) flaskPotency, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
                    }
                    itemStack.setItemMeta(itemMeta);
                }
            } catch (NullPointerException ignore) {
            }
        }
    }
    private static float getDexScaling(float dexLevel) {
        float dexScaling = 0;
        if(dexLevel >= 10 && dexLevel <= 20) {
            dexScaling = 15 + ((dexLevel - 10) * 2);
        }
        if(dexLevel > 20 && dexLevel <= 30) {
            dexScaling = (float) (35 + ((dexLevel - 20) * 1.15));
        }
        if(dexLevel > 30 && dexLevel <= 40) {
            dexScaling = (float) (46.5 + ((dexLevel - 30) * 1.1));
        }
        if(dexLevel > 40 && dexLevel <= 50) {
            dexScaling = (float) (57.5 + (dexLevel - 40));
        }
        if(dexLevel > 50 && dexLevel <= 60) {
            dexScaling = (float) (67.5 + ((dexLevel - 50) * 0.8));
        }
        return dexScaling;
    }
    private static float getStrScaling(float strLevel) {
        float strScaling = 0;
        if(strLevel >= 10 && strLevel <= 20) {
            strScaling = 15 + ((strLevel - 10) * 2);
        }
        if(strLevel > 20 && strLevel <= 30) {
            strScaling = (float) (35 + ((strLevel - 20) * 1.15));
        }
        if(strLevel > 30 && strLevel <= 40) {
            strScaling = (float) (46.5 + ((strLevel - 30) * 1.1));
        }
        if(strLevel > 40 && strLevel <= 50) {
            strScaling = (float) (57.5 + (strLevel - 40));
        }
        if(strLevel > 50 && strLevel <= 60) {
            strScaling = (float) (67.5 + ((strLevel - 50) * 0.8));
        }
        return strScaling;
    }
    private static double getFlaskHP(float flaskPotency) {
        double flaskHP = 0;
        if(flaskPotency == 0) {
            flaskHP = (double) 250 / 15;
        }
        if(flaskPotency == 1) {
            flaskHP = (double) 345 / 15;
        }
        if(flaskPotency == 2) {
            flaskHP = (double) 430 / 15;
        }
        if(flaskPotency == 3) {
            flaskHP = (double) 505 / 15;
        }
        if(flaskPotency == 4) {
            flaskHP = (double) 570 / 15;
        }
        if(flaskPotency == 5) {
            flaskHP = (double) 625 / 15;
        }
        if(flaskPotency == 6) {
            flaskHP = (double) 670 / 15;
        }
        if(flaskPotency == 7) {
            flaskHP = (double) 700 / 15;
        }
        if(flaskPotency == 8) {
            flaskHP = (double) 730 / 15;
        }
        if(flaskPotency == 9) {
            flaskHP = (double) 755 / 15;
        }
        if(flaskPotency == 10) {
            flaskHP = (double) 780 / 15;
        }
        if(flaskPotency == 11) {
            flaskHP = (double) 795 / 15;
        }
        if(flaskPotency == 12) {
            flaskHP = (double) 810 / 15;
        }
        return flaskHP;
    }
    private static float getFlaskFP(float flaskPotency) {
        float flaskFP = 0;
        if(flaskPotency == 0) {
            flaskFP = 80;
        }
        if(flaskPotency == 1) {
            flaskFP = 95;
        }
        if(flaskPotency == 2) {
            flaskFP = 110;
        }
        if(flaskPotency == 3) {
            flaskFP = 125;
        }
        if(flaskPotency == 4) {
            flaskFP = 140;
        }
        if(flaskPotency == 5) {
            flaskFP = 150;
        }
        if(flaskPotency == 6) {
            flaskFP = 160;
        }
        if(flaskPotency == 7) {
            flaskFP = 170;
        }
        if(flaskPotency == 8) {
            flaskFP = 180;
        }
        if(flaskPotency == 9) {
            flaskFP = 190;
        }
        if(flaskPotency == 10) {
            flaskFP = 200;
        }
        if(flaskPotency == 11) {
            flaskFP = 210;
        }
        if(flaskPotency == 12) {
            flaskFP = 220;
        }
        return flaskFP;
    }
    public void addRunes(Player player) {
        float runesHeld = stats.get(player.getName() + "_runesHeld");
        float runesGiven = 10000;
        if(player.getScoreboardTags().contains("runesHeld_" + runesHeld)) {
            player.removeScoreboardTag("runesHeld_" + runesHeld);
        }
        stats.put(player.getName() + "_runesHeld", runesHeld + runesGiven);
        player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
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
            if(tag.contains("crimsonFlasks_")) {
                Float lastValue = Float.valueOf(tag.replace("crimsonFlasks_", ""));
                stats.put(player.getName() + "_crimsonFlasks", lastValue);
            }
            if(tag.contains("ceruleanFlasks_")) {
                Float lastValue = Float.valueOf(tag.replace("ceruleanFlasks_", ""));
                stats.put(player.getName() + "_ceruleanFlasks", lastValue);
            }
            if(tag.contains("flaskPotency_")) {
                Float lastValue = Float.valueOf(tag.replace("flaskPotency_", ""));
                stats.put(player.getName() + "_flaskPotency", lastValue);
            }
            if(tag.contains("discovered_")) {
                gracesDiscovered.put(tag, player.getName());
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
        stats.putIfAbsent(player.getName() + "_crimsonFlasks", 3F);
        stats.putIfAbsent(player.getName() + "_ceruleanFlasks", 1F);
        stats.putIfAbsent(player.getName() + "_flaskPotency", 0F);
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
        player.addScoreboardTag("crimsonFlasks_" + stats.get(player.getName() + "_crimsonFlasks"));
        player.addScoreboardTag("ceruleanFlasks_" + stats.get(player.getName() + "_ceruleanFlasks"));
        player.addScoreboardTag("flaskPotency_" + stats.get(player.getName() + "_flaskPotency"));
        for(String string : gracesDiscovered.keySet()) {
            if(gracesDiscovered.get(string).equals(player.getName())) {
                player.addScoreboardTag(string);
            }
        }
    }
    public void stanceBreak(LivingEntity entity) {
        String uuid = entity.getUniqueId().toString();
        entity.addScoreboardTag("stanceBroken");
        entity.setAI(false);
        Location riposteLocation = entity.getEyeLocation();
        riposteLocation.setY((entity.getLocation().getY() + riposteLocation.getY()) / 2);
        riposteLocation.subtract(0,0.5,0);
        Vector direction = entity.getLocation().getDirection();
        direction.setY(0);
        direction.normalize();
        direction.multiply(0.9);
        riposteLocation.add(direction);
        ArmorStand riposte = (ArmorStand) entity.getWorld().spawnEntity(riposteLocation, EntityType.ARMOR_STAND);
        riposte.setInvisible(true);
        riposte.setSmall(true);
        riposte.setGravity(false);
        riposte.addScoreboardTag("riposteIndicator");
        riposte.addScoreboardTag("riposte_" + uuid);
        BukkitTask stanceRegain = new BukkitRunnable() {
            @Override
            public void run() {
                if(entity.getScoreboardTags().contains("stanceBroken")) {
                    entity.setAI(true);
                    entity.removeScoreboardTag("stanceBroken");
                    riposte.remove();
                    Bukkit.broadcast(Component.text("Stance Regained"));
                }
            }
        }.runTaskLater(Dsplugin.getInstance(), 120);
    }
    public void riposteAttack(Entity riposte, Player player, String tag, Double baseDamage) {
        String uuid = tag.replace("riposte_", "");
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(UUID.fromString(uuid));
        if(entity.getScoreboardTags().contains("stanceBroken")) {
            player.addScoreboardTag("dying");
            entity.addScoreboardTag("iframe");
            entity.removeScoreboardTag("stanceBroken");
            riposte.remove();
            final int[] timer = {0};
            double critDamage = baseDamage * 1.8;
            Location riposteLocation = entity.getLocation();
            riposteLocation.setX(entity.getEyeLocation().getX());
            riposteLocation.setZ(entity.getEyeLocation().getZ());
            Vector entityDirection = entity.getLocation().getDirection();
            entityDirection.setY(0);
            entityDirection.normalize();
            riposteLocation.add(entityDirection);
            riposteLocation.setDirection(entityDirection.rotateAroundY(Math.PI));
            player.sendMessage(entity.getHealth() + "/" + entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
            Collection<Player> collection = new ArrayList<>(Bukkit.getOnlinePlayers());
            BukkitTask riposteAnimation = new BukkitRunnable() {
                @Override
                public void run() {
                    if(timer[0] == 15) {
                        player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1F, 1F);
                        player.playSound(player, Sound.ENTITY_WITHER_BREAK_BLOCK, 1F, 1.5F);
                        player.swingMainHand();
                        entity.setHealth(entity.getHealth() - critDamage);
                        entity.broadcastHurtAnimation(collection);
                        player.sendMessage("Riposte 1 " + critDamage);
                    }
                    if(timer[0] == 45) {
                        player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1F, 0.5F);
                        player.playSound(player, Sound.ENTITY_WITHER_BREAK_BLOCK, 1F, 1F);
                        player.swingMainHand();
                        entity.setHealth(entity.getHealth() - Math.round(critDamage / 4));
                        entity.broadcastHurtAnimation(collection);
                        player.sendMessage("Riposte 2 " + Math.round(critDamage / 4));
                        entity.setGravity(true);
                        entity.setAI(true);
                        entity.setVelocity(player.getLocation().getDirection().multiply(0.8));
                    }
                    if(timer[0] == 60) {
                        player.removeScoreboardTag("dying");
                        entity.removeScoreboardTag("iframe");
                        player.sendMessage(entity.getHealth() + "/" + entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
                        cancel();
                    }
                    timer[0]++;
                    player.teleport(riposteLocation);
                }
            }.runTaskTimer(Dsplugin.getInstance(),0,0);
        }
    }
    @EventHandler
    public void onShift(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(player.getScoreboardTags().contains("dying")) {
            event.setCancelled(true);
            return;
        }
        if(event.isSneaking()) {
            float rollMultiplier;
            String rollDirection;
            if(player.getCurrentInput().isRight()) {
                player.sendMessage("Right");
                rollMultiplier = -1;
                rollDirection = "right";
            } else if (player.getCurrentInput().isLeft()) {
                player.sendMessage("Left");
                rollMultiplier = 1;
                rollDirection = "left";
            } else if (player.getCurrentInput().isBackward()) {
                player.sendMessage("Back");
                rollMultiplier = -1;
                rollDirection = "back";
            } else {
                player.sendMessage("Forward");
                rollMultiplier = 1;
                rollDirection = "forward";
            }
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
                        Rolling.Roll(player, rollMultiplier, rollDirection);
                        duration++;
                    }
                }
            };
            if(!player.getScoreboardTags().contains("recovery")) {
                if(!player.getScoreboardTags().contains("drinking")) {
                    if(player.getLocation().subtract(0, 0.3, 0).getBlock().isSolid()) {
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
    }
    @EventHandler
    public void onEntityHurt(EntityDamageEvent event) {
        try {
            if(event.getEntity().getScoreboardTags().contains("dying") || event.getEntity().getScoreboardTags().contains("iframe")) {
                event.setCancelled(true);
                return;
            }
            if(event.getDamageSource().getCausingEntity() instanceof Player) {
                Player player = (Player) event.getDamageSource().getCausingEntity();
                event.setCancelled(true);
                if(player.getGameMode().equals(GameMode.CREATIVE) && player.isOp()) {
                    event.getEntity().remove();
                    return;
                }
                if(player.getScoreboardTags().contains("dying") || player.getScoreboardTags().contains("iframe")) {
                    return;
                }
                if(player.getAttackCooldown() != 1) {
                    return;
                }
                if(stats.get(player.getName() + "_stamina") <= 0) {
                    player.sendMessage("Not enough stamina.");
                    return;
                }
                if(!player.getScoreboardTags().contains("iframe")) {
                    if(stats.get(player.getName() + "_stamina") >= 1) {
                        try {
                            //Damage Calculation
                            double finalDamage = 0;
                            String lastLine = ((TextComponent) player.getInventory().getItemInMainHand().getItemMeta().lore().getLast()).content();
                            String displayName = ((TextComponent) player.getInventory().getItemInMainHand().getItemMeta().displayName()).content();
                            if(lastLine.equals("Straight Sword") || lastLine.equals("Great Sword") || lastLine.equals("Katana") || lastLine.equals("Axe") || lastLine.equals("Hammer") || lastLine.equals("Admin Item")) {
                                for (Component line : Objects.requireNonNull(player.getInventory().getItemInMainHand().lore())) {
                                    TextComponent textComponent = (TextComponent) line;
                                    if (textComponent.content().contains("| ")) {
                                        TextComponent content = (TextComponent) textComponent.children().getFirst();
                                        String[] values = content.content().split(" ");
                                        for (String value : values) {
                                            if (!value.isBlank()) {
                                                if (value.contains("+")) {
                                                    value = value.replace("+", "");
                                                }
                                                try {
                                                    int damage = Integer.parseInt(value);
                                                    finalDamage = finalDamage + damage;
                                                } catch (NumberFormatException ignore) {
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(finalDamage == 0) {
                                return;
                            }
                            for(String tag : event.getEntity().getScoreboardTags()) {
                                if(tag.contains("riposte_")) {
                                    if(event.getEntity().getNearbyEntities(0.4,0.5,0.4).contains(player)) {
                                        riposteAttack(event.getEntity(), player, tag, finalDamage);
                                    }
                                    return;
                                }
                            }
                            player.sendMessage("Total Damage " + finalDamage);
                            if(event.getEntity() instanceof LivingEntity) {
                                LivingEntity entity = (LivingEntity) event.getEntity();
                                if(finalDamage >= entity.getHealth()) {
                                    addRunes(player);
                                }
                                entity.damage(finalDamage);
                            }
                            //Stamina Cost and Stance Damage
                            String uuid = event.getEntity().getUniqueId().toString();
                            float entityPoise = stats.get(uuid + "_poise");
                            if(displayName.equals("Stance Breaker")) {
                                if(!event.getEntity().getScoreboardTags().contains("stanceBroken")) {
                                    stats.put(uuid + "_poise", 0F);
                                }
                            }
                            if(player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Straight Sword", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                                stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 10);
                            }
                            if(player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Greatsword", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                                stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 15);
                            }
                            if(player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().equals(Component.text("Katana", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))) {
                                if(player.getFallDistance() != 0) {
                                    stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 10);
                                    if(!event.getEntity().getScoreboardTags().contains("stanceBroken")) {
                                        stats.put(uuid + "_poise", entityPoise - 7.5F);
                                    }
                                    player.sendMessage("Jump Attack");
                                } else if (player.isSprinting() ) {
                                    stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 15);
                                    if(!event.getEntity().getScoreboardTags().contains("stanceBroken")) {
                                        stats.put(uuid + "_poise", entityPoise - 5);
                                    }
                                    player.sendMessage("Running Attack");
                                } else {
                                    stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 12);
                                    if(!event.getEntity().getScoreboardTags().contains("stanceBroken")) {
                                        stats.put(uuid + "_poise", entityPoise - 5);
                                    }
                                }
                            }
                            if(player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Axe", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                                stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 13);
                            }
                            if(player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Hammer", NamedTextColor.DARK_GRAY))) {
                                stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 13);
                            }
                            if(stats.get(uuid + "_poise") <= 0) {
                                player.playSound(player, Sound.BLOCK_ANVIL_LAND, 1, 0.7F);
                                player.playSound(player, Sound.ENTITY_IRON_GOLEM_REPAIR, 1, 1F);
                                stats.put(uuid + "_poise", stats.get(uuid + "_maxPoise"));
                                player.sendMessage("stance broken");
                                stanceBreak((LivingEntity) event.getEntity());
                            } else {
                                testInstance.poiseRegen(uuid);
                            }
                            testInstance.setMaxStamina(player.getName());
                            testInstance.staminaRegen(player.getName());
                            //Effects
                            player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1F,1.3F);
                            Location location = player.getEyeLocation();
                            location.subtract(0,0.3,0);
                            location.add(player.getLocation().getDirection().setY(0).normalize());
                            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, location, 1,0,0,0,0);
                        } catch (NullPointerException ignore) {
                        }
                    }
                }
            }
            if(event.getEntity() instanceof Player) {
                Player player = ((Player) event.getEntity()).getPlayer();
                if(player.getScoreboardTags().contains("drinking")) {
                    Bukkit.getScheduler().cancelTask(taskID.get(player.getName() + "_lastTask"));
                    player.removeScoreboardTag("drinking");
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
                    player.sendMessage("Healing interrupted.");
                }
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
        testInstance.setMaxHP(player.getName());
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        testInstance.setMaxFP(player.getName());
        stats.put(player.getName() + "_FP", stats.get(player.getName() + "_maxFP"));
        testInstance.showFP(player.getName());
        stats.put(player.getName() + "_stamina", 0F);
        testInstance.setMaxStamina(player.getName());
        testInstance.staminaRegen(player.getName());
        player.getAttribute(Attribute.SAFE_FALL_DISTANCE).setBaseValue(8);
        player.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER).setBaseValue(6);
        player.sendMessage(stats.entrySet().toString());
        player.setExperienceLevelAndProgress(0);
        player.setLevel(stats.get(player.getName() + "_level").intValue());
        setItemLore(player, player.getInventory());

    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if(player.getScoreboardTags().contains("dying")) {
            event.setCancelled(true);
            return;
        }
        if(event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            if(event.getRightClicked().getScoreboardTags().contains("uchigatana")) {
                uchigatana(player);
            } else if (event.getRightClicked().getScoreboardTags().contains("grace")) {
                Entity grace = event.getRightClicked();
                Location graceLocation = grace.getLocation();
                graceLocation.add(graceLocation.getDirection().setY(0).normalize().multiply(-1.5));
                player.setRespawnLocation(graceLocation, true);
                String[] stringList = grace.getName().split(" ");
                if(!player.getScoreboardTags().contains("discovered_" + String.join("_", stringList))) {
                    graceDiscovered(player);
                    player.addScoreboardTag("discovered_" + String.join("_", stringList));
                } else {
                    baseLocation.put(player.getName(), player.getLocation());
                    grace(player);
                    mainMenu(player);
                }
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
        ItemStack graceCheck2 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta graceMeta2 = graceCheck2.getItemMeta();
        graceMeta2.displayName(Component.text("Travel", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false));
        graceCheck2.setItemMeta(graceMeta2);
        ItemStack graceCheck3 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta graceMeta3 = graceCheck3.getItemMeta();
        graceMeta3.displayName(Component.text("Travel", NamedTextColor.DARK_AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        graceCheck3.setItemMeta(graceMeta3);
        ItemStack graceCheck4 = new ItemStack(Material.REDSTONE);
        ItemMeta graceMeta4 = graceCheck4.getItemMeta();
        graceMeta4.displayName(Component.text("Flasks", NamedTextColor.RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        graceCheck4.setItemMeta(graceMeta4);
        ItemStack graceCheck5 = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta graceMeta5 = graceCheck5.getItemMeta();
        graceMeta5.displayName(Component.text("Allocate flask charges", NamedTextColor.DARK_AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        graceCheck5.setItemMeta(graceMeta5);
        ItemStack smithingCheck = new ItemStack(Material.ANVIL);
        ItemMeta smithingMeta = smithingCheck.getItemMeta();
        smithingMeta.displayName(Component.text("Smithing Table", NamedTextColor.GRAY, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        smithingCheck.setItemMeta(smithingMeta);
        if(event.getInventory().contains(graceCheck) || event.getInventory().contains(graceCheck2) || event.getInventory().contains(graceCheck3) || event.getInventory().contains(graceCheck4) || event.getInventory().contains(graceCheck5)) {
            player.setLevel(stats.get(player.getName() + "_level").intValue());
            String selector = String.format("@e[tag=%s_sit]", event.getPlayer().getName());
            for(Entity entity : Bukkit.selectEntities(Bukkit.getConsoleSender(), selector)) {
                entity.remove();
            }
        }
        if(event.getInventory().contains(smithingCheck)) {
            if(player.getScoreboardTags().contains("upgrading")) {
                player.removeScoreboardTag("upgrading");
                return;
            }
            try {
                if (!event.getInventory().getItem(10).getType().equals(Material.BARRIER)) {
                    player.getInventory().setItem(player.getInventory().firstEmpty(), event.getInventory().getItem(10));
                }
            } catch (NullPointerException ignore) {
            }
        }

    }
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack graceCheck = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta graceMeta = graceCheck.getItemMeta();
        graceMeta.displayName(Component.text("Level Up", NamedTextColor.YELLOW, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        graceCheck.setItemMeta(graceMeta);
        ItemStack graceCheck2 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta graceMeta2 = graceCheck2.getItemMeta();
        graceMeta2.displayName(Component.text("Travel", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false));
        graceCheck2.setItemMeta(graceMeta2);
        ItemStack graceCheck3 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta graceMeta3 = graceCheck3.getItemMeta();
        graceMeta3.displayName(Component.text("Travel", NamedTextColor.DARK_AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        graceCheck3.setItemMeta(graceMeta3);
        ItemStack graceCheck4 = new ItemStack(Material.REDSTONE);
        ItemMeta graceMeta4 = graceCheck4.getItemMeta();
        graceMeta4.displayName(Component.text("Flasks", NamedTextColor.RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        graceCheck4.setItemMeta(graceMeta4);
        ItemStack smithingCheck = new ItemStack(Material.ANVIL);
        ItemMeta smithingMeta = smithingCheck.getItemMeta();
        smithingMeta.displayName(Component.text("Smithing Table", NamedTextColor.GRAY, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        smithingCheck.setItemMeta(smithingMeta);
        ItemStack graceCheck5 = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta graceMeta5 = graceCheck5.getItemMeta();
        graceMeta5.displayName(Component.text("Allocate flask charges", NamedTextColor.DARK_AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        graceCheck5.setItemMeta(graceMeta5);
        if(event.getInventory().contains(graceCheck5)) {
            event.setCancelled(true);
            if(event.getClick().isLeftClick()) {
                try {
                    if (event.getCurrentItem().getType().equals(Material.POTION)) {
                        if (((PotionMeta) event.getCurrentItem().getItemMeta()).getColor().equals(Color.RED)) {
                            if (stats.get(player.getName() + "_ceruleanFlasks") != 0) {
                                player.removeScoreboardTag("crimsonFlasks_" + stats.get(player.getName() + "_crimsonFlasks"));
                                stats.put(player.getName() + "_crimsonFlasks", (stats.get(player.getName() + "_crimsonFlasks") + 1));
                                player.addScoreboardTag("crimsonFlasks_" + stats.get(player.getName() + "_crimsonFlasks"));
                                player.removeScoreboardTag("ceruleanFlasks_" + stats.get(player.getName() + "_ceruleanFlasks"));
                                stats.put(player.getName() + "_ceruleanFlasks", (stats.get(player.getName() + "_ceruleanFlasks") - 1));
                                player.addScoreboardTag("ceruleanFlasks_" + stats.get(player.getName() + "_ceruleanFlasks"));
                            } else {
                                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                                player.sendMessage(Component.text("You can't allocate any more charges to this flask.", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                            }
                        }
                        if (((PotionMeta) event.getCurrentItem().getItemMeta()).getColor().equals(Color.BLUE)) {
                            if (stats.get(player.getName() + "_crimsonFlasks") != 0) {
                                player.removeScoreboardTag("ceruleanFlasks_" + stats.get(player.getName() + "_ceruleanFlasks"));
                                stats.put(player.getName() + "_ceruleanFlasks", (stats.get(player.getName() + "_ceruleanFlasks") + 1));
                                player.addScoreboardTag("ceruleanFlasks_" + stats.get(player.getName() + "_ceruleanFlasks"));
                                player.removeScoreboardTag("crimsonFlasks_" + stats.get(player.getName() + "_crimsonFlasks"));
                                stats.put(player.getName() + "_crimsonFlasks", (stats.get(player.getName() + "_crimsonFlasks") - 1));
                                player.addScoreboardTag("crimsonFlasks_" + stats.get(player.getName() + "_crimsonFlasks"));
                            } else {
                                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                                player.sendMessage(Component.text("You can't allocate any more charges to this flask.", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                            }
                        }
                        allocateMenu(player);
                        grace(player);
                    }
                } catch (NullPointerException ignore) {
                }
            }
        }
        if(event.getInventory().contains(graceCheck4)) {
            event.setCancelled(true);
            if(event.getClick().isLeftClick()) {
                try {
                    List<Component> Lore = event.getCurrentItem().getItemMeta().lore();
                    if (event.getCurrentItem().getType().equals(Material.LIME_WOOL)) {
                        ItemStack goldenSeed = new ItemStack(Material.SUNFLOWER);
                        List<Component> seedLore = new ArrayList<>();
                        ItemMeta seedMeta = goldenSeed.getItemMeta();
                        seedMeta.displayName(Component.text("Golden Seed", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                        seedLore.add(Component.text("Increases a Sacred Flask's number of uses.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                        seedMeta.lore(seedLore);
                        goldenSeed.setItemMeta(seedMeta);
                        int flaskCost = 0;
                        for (Component line : Lore) {
                            TextComponent textComponent = (TextComponent) line;
                            if (textComponent.content().contains("Golden Seed  ")) {
                                flaskCost = Integer.parseInt(textComponent.content().replace("Golden Seed  ", ""));
                            }
                        }
                        if (flaskCost != 0) {
                            if (player.getInventory().containsAtLeast(goldenSeed, flaskCost)) {
                                player.removeScoreboardTag("crimsonFlasks_" + stats.get(player.getName() + "_crimsonFlasks"));
                                stats.put(player.getName() + "_crimsonFlasks", (stats.get(player.getName() + "_crimsonFlasks") + 1));
                                player.addScoreboardTag("crimsonFlasks_" + stats.get(player.getName() + "_crimsonFlasks"));
                                goldenSeed.setAmount(flaskCost);
                                player.getInventory().removeItem(goldenSeed);
                            } else {
                                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                                player.sendMessage(Component.text("Not enough Golden Seeds.", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                            }
                        } else {
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                            player.sendMessage(Component.text("You already hold the maximum amount of flask charges", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                        }
                        grace(player);
                        flaskMenu(player);
                    }
                    if(event.getCurrentItem().getType().equals(Material.DRAGON_BREATH)) {
                        ItemStack sacredTear = new ItemStack(Material.DRAGON_BREATH);
                        List<Component> tearLore = new ArrayList<>();
                        ItemMeta tearMeta = sacredTear.getItemMeta();
                        tearMeta.displayName(Component.text("Sacred Tear", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                        tearLore.add(Component.text("Increases the potency of a Sacred Flask's restorative effects.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                        tearMeta.lore(tearLore);
                        sacredTear.setItemMeta(tearMeta);
                        if(stats.get(player.getName() + "_flaskPotency") >= 12) {
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                            player.sendMessage(Component.text("You can't increase the potency of Sacred Flasks any further.", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                        } else {
                            if (player.getInventory().containsAtLeast(sacredTear, 1)) {
                                player.removeScoreboardTag("flaskPotency_" + stats.get(player.getName() + "_flaskPotency"));
                                stats.put(player.getName() + "_flaskPotency", (stats.get(player.getName() + "_flaskPotency") + 1));
                                player.addScoreboardTag("flaskPotency_" + stats.get(player.getName() + "_flaskPotency"));
                                sacredTear.setAmount(1);
                                player.getInventory().removeItem(sacredTear);
                            } else {
                                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                                player.sendMessage(Component.text("Not enough Sacred Tears.", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                            }
                        }
                        grace(player);
                        flaskMenu(player);
                    }
                } catch (NullPointerException ignore) {
                }
            }
            try {
                if (event.getCurrentItem().getType().equals(Material.BLAZE_POWDER)) {
                    grace(player);
                    allocateMenu(player);
                }
            } catch (NullPointerException ignore) {
            }
        }
        if(event.getInventory().contains(graceCheck3)) {
            event.setCancelled(true);
            try {
                if(event.getCurrentItem().getType().equals(Material.GOLD_NUGGET)) {
                    if(event.isLeftClick()) {
                        graceTravel(player, event);
                    }
                }
            } catch(NullPointerException ignore) {
            }
        }
        if(event.getInventory().contains(graceCheck2)) {
            event.setCancelled(true);
            try {
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Travel", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false))) {
                    grace(player);
                    travelMenu(player);
                }
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Level Up", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))) {
                    grace(player);
                    levelMenu(player);
                }
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Sacred Flasks", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))) {
                    grace(player);
                    flaskMenu(player);
                }
            } catch (NullPointerException ignore) {
            }
        }
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
                            testInstance.setMaxHP(player.getName());
                        } else {
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
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
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
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
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
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
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
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
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
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
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                            player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                        }
                    } else {
                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                        player.sendMessage(Component.text("Intelligence is already at the maximum level.", NamedTextColor.RED));
                    }
                }
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Your Stats", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false))) {
                    if(event.getClick().equals(ClickType.DROP)) {
                        Bukkit.broadcast(Component.text("<" + player.getName() + "> ")
                                .append(event.getCurrentItem().displayName()));
                    }
                }
                setItemLore(player, player.getInventory());
                grace(player);
                levelMenu(player);
            } catch (NullPointerException ignore) {
            }
        }
        if(event.getInventory().contains(smithingCheck)) {
            event.setCancelled(true);
            try {
                if(event.getClick().isLeftClick()) {
                    if(event.getCurrentItem().getType().equals(Material.MACE)) {
                        if(event.getClickedInventory().getItem(10).getType().equals(Material.BARRIER)) {
                            player.sendMessage(Component.text("You must first select an armament to upgrade.", NamedTextColor.RED));
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                        } else {
                            String displayName = ((TextComponent) event.getInventory().getItem(10).getItemMeta().displayName()).content();
                            int currLevel;
                            if(!displayName.contains("+")) {
                                currLevel = 0;
                            } else {
                                int plusIndex = displayName.indexOf("+");
                                currLevel = Integer.parseInt(displayName.substring(plusIndex + 1));
                            }
                            int runeCost = 1000 + (currLevel * 84);
                            int stoneCost = 0;
                            int stoneLevel;
                            if(currLevel == 24) {
                                stoneCost = 1;
                            } else if (currLevel % 3 == 0) {
                                stoneCost = 2;
                            } else if (currLevel % 3 == 1) {
                                stoneCost = 4;
                            } else if (currLevel % 3 == 2) {
                                stoneCost = 6;
                            }
                            stoneLevel = ((currLevel / 3) + 1);
                            ItemStack stone = new ItemStack(Material.ARMADILLO_SCUTE);
                            ItemMeta stoneMeta = stone.getItemMeta();
                            List<Component> Lore = new ArrayList<>();
                            String stoneName;
                            if (stoneLevel == 9) {
                                stoneName = "Ancient Dragon Smithing Stone";
                                stoneMeta.displayName(Component.text(stoneName, NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                                Lore.add(Component.text("Strengthens armaments to +25.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                            } else {
                                stoneName = "Smithing Stone [" + stoneLevel + "]";
                                stoneMeta.displayName(Component.text(stoneName, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
                                Lore.add(Component.text("Strengthens armaments to +" + (3 * stoneLevel) + ".", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                            }
                            stoneMeta.lore(Lore);
                            stone.setItemMeta(stoneMeta);
                            if(player.getInventory().containsAtLeast(stone, stoneCost)) {
                                if(stats.get(player.getName() + "_runesHeld") >= runeCost) {
                                    player.getInventory().setItem(player.getInventory().firstEmpty(), event.getClickedInventory().getItem(16));
                                    stone.setAmount(stoneCost);
                                    player.getInventory().removeItem(stone);
                                    player.removeScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                                    stats.put(player.getName() + "_runesHeld", (stats.get(player.getName() + "_runesHeld") - runeCost));
                                    player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
                                    player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1F, 1.3F);
                                    player.addScoreboardTag("upgrading");
                                    upgradeMenu(player);
                                } else {
                                    player.sendMessage(Component.text("Not enough runes.", NamedTextColor.RED));
                                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                                }
                            } else {
                                player.sendMessage(Component.text("Not enough smithing stones.", NamedTextColor.RED));
                                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                            }
                        }
                    }
                    if(event.getClickedInventory().equals(player.getInventory())) {
                        String lastLine = ((TextComponent) event.getCurrentItem().getItemMeta().lore().getLast()).content();
                        String displayName = ((TextComponent) event.getCurrentItem().getItemMeta().displayName()).content();
                        if(lastLine.equals("Straight Sword") || lastLine.equals("Great Sword") || lastLine.equals("Katana") || lastLine.equals("Axe") || lastLine.equals("Hammer")) {
                            if(!displayName.contains("+25")) {
                                ItemStack upgradedItem = event.getCurrentItem().clone();
                                ItemMeta upgradedMeta = upgradedItem.getItemMeta();
                                int upgradeLevel;
                                int currLevel;
                                if(!displayName.contains("+")) {
                                    currLevel = 0;
                                    upgradeLevel = 1;
                                    upgradedMeta.displayName(Component.text(displayName + " +" + upgradeLevel, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                                } else {
                                    int plusIndex = displayName.indexOf("+");
                                    currLevel = Integer.parseInt(displayName.substring(plusIndex + 1));
                                    upgradeLevel = currLevel + 1;
                                    upgradedMeta.displayName(Component.text(displayName.replace(String.valueOf(currLevel), String.valueOf(upgradeLevel)), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                                }
                                upgradedItem.setItemMeta(upgradedMeta);
                                event.getInventory().setItem(10, event.getCurrentItem());
                                event.getInventory().setItem(16, upgradedItem);
                                player.getInventory().setItem(event.getSlot(), null);
                                setItemLore(player, event.getInventory());
                                int runeCost = 1000 + (currLevel * 84);
                                int stoneCost = 0;
                                int stoneLevel;
                                if(currLevel == 24) {
                                    stoneCost = 1;
                                } else if (currLevel % 3 == 0) {
                                    stoneCost = 2;
                                } else if (currLevel % 3 == 1) {
                                    stoneCost = 4;
                                } else if (currLevel % 3 == 2) {
                                    stoneCost = 6;
                                }
                                stoneLevel = ((currLevel / 3) + 1);
                                String stoneName;
                                if(stoneLevel == 9) {
                                    stoneName = "Ancient Dragon Smithing Stone";
                                } else {
                                    stoneName = "Smithing Stone [" + stoneLevel + "]";
                                }
                                List<Component> Lore = new ArrayList<>();
                                Lore.add(Component.text("Required Items:", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                                Lore.add(Component.text(stoneName + "  " + stoneCost, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
                                Lore.add(Component.text(" "));
                                Lore.add(Component.text("Cost:", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                                Lore.add(Component.text("💮 " + runeCost, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
                                Lore.add(Component.text(""));
                                Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false)
                                        .append(Component.keybind("key.attack", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                                        .append(Component.text(" on this item to upgrade your armament.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
                                ItemStack item = event.getInventory().getItem(13);
                                ItemMeta itemMeta = item.getItemMeta();
                                itemMeta.lore(Lore);
                                item.setItemMeta(itemMeta);
                                player.playSound(player, Sound.ENTITY_HORSE_ARMOR, 1F, 1.7F);
                            } else {
                                player.sendMessage(Component.text("This armament can't be upgraded any further", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1F, 0.8F);
                            }
                        }
                    }
                }
            } catch (NullPointerException ignore) {
            }
        }
    }
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if(player.getScoreboardTags().contains("dying")) {
            event.setCancelled(true);
            return;
        }
        if(stats.get(player.getName() + "_FP") >= 12) {
            stats.put(player.getName() + "_FP", stats.get(player.getName() + "_FP") - 12);
            player.sendMessage("Current FP: " + stats.get(player.getName() + "_FP"));
            testInstance.showFP(player.getName());
        } else {
            player.sendMessage("Not enough FP.");
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)   {
        if(event.getEntity() instanceof Horse) {
            if(!event.getEntity().getPassengers().isEmpty()) {
                for(Entity passenger : event.getEntity().getPassengers()) {
                    if(passenger instanceof Player) {
                        Player player = (Player) passenger;
                        if(event.getEntity().getScoreboardTags().contains(player.getName() + "_torrent")) {
                            event.getDrops().clear();
                            player.sendMessage("Torrent died.");
                            player.addScoreboardTag("deadTorrent");
                        }
                    }
                }
            }
        }
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            //Death Screen
            event.setCancelled(true);
            player.setHealth(0.005);
            player.addScoreboardTag("dying");
            player.playSound(player, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 0);
            Location deathLocation = player.getLocation();
            deathLocation.subtract(0,2,0);
            Entity graceSit = player.getWorld().spawnEntity(deathLocation, EntityType.ARMOR_STAND);
            graceSit.addScoreboardTag(player.getName() + "_death");
            graceSit.setGravity(false);
            graceSit.setInvisible(true);
            graceSit.addPassenger(player);
            BukkitTask dying = new BukkitRunnable() {
                @Override
                public void run() {
                    player.playSound(player, Sound.ENTITY_WITHER_HURT, 1F, 0.5F);
                    Title.Times time = Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(1));
                    Title title = Title.title(Component.text("YOU DIED", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false), Component.empty(), time);
                    player.showTitle(title);
                    BukkitTask delayedSound = new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.playSound(player, Sound.ENTITY_GLOW_SQUID_SQUIRT, 1, 0);
                        }
                    }.runTaskLater(Dsplugin.getInstance(), 20);
                }
            }.runTaskLater(Dsplugin.getInstance(), 40);
            BukkitTask respawn = new BukkitRunnable() {
                @Override
                public void run() {
                    String selector = String.format("@e[tag=%s_death]", player.getName());
                    List<Entity> deathSeats = Bukkit.selectEntities(Bukkit.getConsoleSender(), selector);
                    for(Entity entity : deathSeats) {
                        entity.remove();
                    }
                    player.removeScoreboardTag("dying");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50,255, false, false, false));
                    player.teleport(player.getRespawnLocation());
                    player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
                    stats.put(player.getName() + "_FP", stats.get(player.getName() + "_maxFP"));
                    testInstance.showFP(player.getName());
                    if(player.getScoreboardTags().contains("deadTorrent")) {
                        player.removeScoreboardTag("deadTorrent");
                    }
                }
            }.runTaskLater(Dsplugin.getInstance(), 115);
            //Rune Loss
            if(player.getScoreboardTags().contains("runesHeld_" + stats.get(player.getName() + "_runesHeld"))) {
                player.removeScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
            }
            float runesAfterDeath = Math.round(stats.get(player.getName() + "_runesHeld") / 2);
            stats.put(player.getName() + "_runesHeld", runesAfterDeath);
            player.addScoreboardTag("runesHeld_" + runesAfterDeath);
            player.sendMessage("Runes after death: " + runesAfterDeath);
        }
    }
    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if(event.getExited() instanceof Player) {
            Player player = (Player) event.getExited();
            Entity horse = event.getVehicle();
            if(horse.getScoreboardTags().contains(player.getName() + "_torrent")) {
                if(player.getScoreboardTags().contains("canHorseDismount")) {
                    horse.remove();
                    player.removeScoreboardTag("onTorrent");
                    player.removeScoreboardTag("canHorseJump");
                    player.removeScoreboardTag("canHorseDismount");
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(player.getScoreboardTags().contains("dying")) {
            event.setCancelled(true);
            return;
        }
        if(event.getAction().isRightClick()) {
            if(event.hasItem()) {
                try {
                    if (((TextComponent) event.getItem().getItemMeta().displayName()).content().contains("Flask of Crimson Tears")) {
                        potionDrink(player, "crimson", event.getItem());
                        event.setCancelled(true);
                    }
                    if (((TextComponent) event.getItem().getItemMeta().displayName()).content().contains("Flask of Cerulean Tears")) {
                        potionDrink(player, "cerulean", event.getItem());
                        event.setCancelled(true);
                    }
                } catch (NullPointerException ignore) {
                }
            }
            if(player.getScoreboardTags().contains("canHorseJump") || player.getScoreboardTags().contains("canDoubleJump")) {
                if(event.hasItem()) {
                    try {
                        if(event.getItem().getItemMeta().displayName().equals(Component.text("Spectral Steed Whistle", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))) {
                            torrentJump(player);
                        }
                    } catch (NullPointerException ignore) {
                    }
                }
            } else {
                if(event.hasItem()) {
                    try {
                        if(event.getItem().getItemMeta().displayName().equals(Component.text("Spectral Steed Whistle", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))) {
                            if(!player.getScoreboardTags().contains("onTorrent")) {
                                if(player.getScoreboardTags().contains("deadTorrent")) {
                                    player.sendMessage("You need to rest at a Site of Grace to revive Torrent.");
                                } else {
                                    summonTorrent(player);
                                }
                            }
                        }
                    } catch (NullPointerException ignore) {
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerRegen(EntityRegainHealthEvent event) {
        if(event.getEntity() instanceof Player) {
            if(event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            event.setCancelled(true);
            player.setFoodLevel(20);
        }
    }
    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }
    @EventHandler
    public void onPlayerDrink(PlayerItemConsumeEvent event) {
        if(event.getItem().getType().equals(Material.POTION)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerChangeSlot(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if(player.getScoreboardTags().contains("dying")) {
            event.setCancelled(true);
            return;
        }
        if(player.getScoreboardTags().contains("drinking")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        if(event.getPlayer().getScoreboardTags().contains("dying")) {
            event.setCancelled(true);
            return;
        }
        if(event.getPlayer().getScoreboardTags().contains("drinking")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if(event.getEntity() instanceof LivingEntity && !event.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            String uuid = entity.getUniqueId().toString();
            entity.addScoreboardTag("maxPoise_80");
            stats.put(uuid + "_maxPoise", 80F);
            stats.put(uuid + "_poise", 80F);
            if(entity.getType().equals(EntityType.IRON_GOLEM)) {
                entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20000);
                entity.setHealth(20000);
            }
            Bukkit.broadcast(Component.text(entity.getAbsorptionAmount()));
        }
    }
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}