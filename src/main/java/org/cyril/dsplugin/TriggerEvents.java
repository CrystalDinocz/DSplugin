package org.cyril.dsplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class TriggerEvents implements Listener {
    Dsplugin dsInstance = new Dsplugin();
    Test testInstance = new Test(dsInstance);
    HashMap<String, Float> stats = dsInstance.getStats();
    HashMap<String, Location> baseLocation = new HashMap<String, Location>();
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
        graceMeta3.displayName(Component.text("Flasks", NamedTextColor.RED).decoration(TextDecoration.ITALIC,false));
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
        Lore.add(Component.text("Runes Held: " + Math.round(stats.get(player.getName() + "_runesHeld")), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        Lore.add(Component.text("Runes Needed: " + Math.round(stats.get(player.getName() + "_runesNeeded")), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
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
    public void grace(Player player) {
        if(player.getScoreboardTags().contains("deadTorrent")) {
            player.removeScoreboardTag("deadTorrent");
        }
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        stats.put(player.getName() + "_FP", stats.get(player.getName() + "_maxFP"));
        testInstance.showFP(player.getName());
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
    public void onEntityHurt(EntityDamageEvent event) {
        try {
            if (event.getEntity().getScoreboardTags().contains("iframe")) {
                event.setCancelled(true);
            }
            if(event.getDamageSource().getCausingEntity() instanceof Player) {
                Player player = (Player) event.getDamageSource().getCausingEntity();
                if(player.getAttackCooldown() != 1) {
                    event.setCancelled(true);
                    return;
                }
                if(stats.get(player.getName() + "_stamina") <= 0) {
                    event.setCancelled(true);
                    player.sendMessage("Not enough stamina.");
                    return;
                }
                if(!player.getScoreboardTags().contains("iframe")) {
                    if(stats.get(player.getName() + "_stamina") >= 1) {
                        try {
                            if(player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Straight Sword", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                                stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 10);
                            }
                            if(player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Greatsword", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                                stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 15);
                            }
                            if(player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().equals(Component.text("Katana", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))) {
                                stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 12);
                            }
                            if(player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Axe", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))) {
                                stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 13);
                            }
                            if(player.getInventory().getItemInMainHand().getItemMeta().lore().getLast().children().contains(Component.text("Hammer", NamedTextColor.DARK_GRAY))) {
                                stats.put(player.getName() + "_stamina", stats.get(player.getName() + "_stamina") - 13);
                            }
                            testInstance.setMaxStamina(player.getName());
                            testInstance.staminaRegen(player.getName());
                        } catch (NullPointerException ignore) {
                        }
                    }
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
                mainMenu(player);
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
        if(event.getInventory().contains(graceCheck) || event.getInventory().contains(graceCheck2) || event.getInventory().contains(graceCheck3)) {
            player.setLevel(stats.get(player.getName() + "_level").intValue());
            String selector = String.format("@e[tag=%s_sit]", event.getPlayer().getName());
            for(Entity entity : Bukkit.selectEntities(Bukkit.getConsoleSender(), selector)) {
                entity.remove();
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
                if(event.getCurrentItem().getItemMeta().displayName().equals(Component.text("Flasks", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))) {
                    player.sendMessage("Flasks");
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
                    if(event.getClick().equals(ClickType.DROP)) {
                        Bukkit.broadcast(Component.text("<" + player.getName() + "> ")
                                .append(event.getCurrentItem().displayName()));
                    }
                }
                grace(player);
                levelMenu(player);
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
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getDamageSource().getCausingEntity() instanceof Player) {
            Player player = (Player) event.getDamageSource().getCausingEntity();
            if(player.getScoreboardTags().contains("runesHeld_" + stats.get(player.getName() + "_runesHeld"))) {
                player.removeScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
            }
            stats.put(player.getName() + "_runesHeld", stats.get(player.getName() + "_runesHeld") + 1000000000);
            player.addScoreboardTag("runesHeld_" + stats.get(player.getName() + "_runesHeld"));
        }
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
    public void onHorseDismount(VehicleExitEvent event) {
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
        if(event.getAction().isRightClick()) {
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
                        if (event.getItem().getItemMeta().displayName().equals(Component.text("Spectral Steed Whistle", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))) {
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
}
