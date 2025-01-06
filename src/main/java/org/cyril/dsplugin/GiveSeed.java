package org.cyril.dsplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GiveSeed extends Command {
    protected GiveSeed(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            ItemStack goldenSeed = new ItemStack(Material.SUNFLOWER);
            List<Component> seedLore = new ArrayList<>();
            ItemMeta seedMeta = goldenSeed.getItemMeta();
            seedMeta.displayName(Component.text("Golden Seed", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC,false));
            seedLore.add(Component.text("Increases a Sacred Flask's number of uses.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            seedMeta.lore(seedLore);
            goldenSeed.setItemMeta(seedMeta);
            player.getInventory().setItem(player.getInventory().firstEmpty(), goldenSeed);
        } else {
            commandSender.sendMessage("Only players can use this command");
        }
        return true;
    }
}
