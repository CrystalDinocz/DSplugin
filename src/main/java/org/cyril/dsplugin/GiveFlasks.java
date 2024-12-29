package org.cyril.dsplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GiveFlasks extends Command {
    protected GiveFlasks(@NotNull String name) {
        super(name);
    }
    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            List<Component> Lore = new ArrayList<>();
            ItemStack crimsonFlask = new ItemStack(Material.POTION);
            ItemStack ceruleanFlask = new ItemStack(Material.POTION);
            PotionMeta crimsonMeta = (PotionMeta) crimsonFlask.getItemMeta();
            PotionMeta ceruleanMeta = (PotionMeta) ceruleanFlask.getItemMeta();
            crimsonMeta.setColor(Color.RED);
            crimsonMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            crimsonMeta.displayName(Component.text("Flask of Crimson Tears", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false));
            Lore.add(Component.text("Rest at a site of grace to replenish.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            Lore.add(Component.text(""));
            Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                    .append(Component.keybind("key.use", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .append(Component.text(" to restore HP", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false)));
            crimsonMeta.lore(Lore);
            crimsonFlask.setItemMeta(crimsonMeta);
            Lore.clear();
            ceruleanMeta.setColor(Color.BLUE);
            ceruleanMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            ceruleanMeta.displayName(Component.text("Flask of Cerulean Tears", NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false));
            Lore.add(Component.text("Rest at a site of grace to replenish.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            Lore.add(Component.text(""));
            Lore.add(Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                    .append(Component.keybind("key.use", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .append(Component.text(" to restore FP", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false)));
            ceruleanMeta.lore(Lore);
            ceruleanFlask.setItemMeta(ceruleanMeta);
            player.getInventory().setItem(player.getInventory().firstEmpty(), crimsonFlask);
            player.getInventory().setItem(player.getInventory().firstEmpty(), ceruleanFlask);
        } else {
            commandSender.sendMessage("Only players can use this command.");
        }
        return true;
    }
}
