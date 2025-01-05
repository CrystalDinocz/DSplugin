package org.cyril.dsplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TestCommand extends Command {
    TriggerEvents triggerEvents = new TriggerEvents();
    protected TestCommand(@NotNull String name) {
        super(name);
    }
    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            triggerEvents.travelMenu(player);
            ItemStack sword = new ItemStack(Material.STICK);
            ItemMeta swordMeta = sword.getItemMeta();
            List<Component> swordLore = new ArrayList<>();
            swordLore.add(Component.text("⚔ Attack Power", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("Physical 1+  0", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
            swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("Magic    0", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
            swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("Fire     0", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
            swordLore.add(Component.text(" "));
            swordLore.add(Component.text("💪 Attribute Scaling", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("Str -     Dex -", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
            swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("Int -", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
            swordLore.add(Component.text(" "));
            swordLore.add(Component.text("👕 Passive Effects", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("Instantly staggers enemies.", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
            swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("-", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
            swordLore.add(Component.text("| ", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("-", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));
            swordLore.add(Component.text(" "));
            swordLore.add(Component.text("Admin Item", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
            swordMeta.lore(swordLore);
            swordMeta.setUnbreakable(true);
            swordMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            swordMeta.displayName(Component.text("Stance Breaker", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            sword.setItemMeta(swordMeta);
            player.getInventory().setItem(player.getInventory().firstEmpty(), sword);
        } else {
            commandSender.sendMessage("Only players can use this command.");
        }
        return true;
    }
}
