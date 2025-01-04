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

public class GiveStones extends Command {
    protected GiveStones(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(strings.length != 1) {
                player.sendMessage("Wrong command usage. /stones 1-10");
            } else {
                try {
                    int stoneTier = Integer.parseInt(strings[0]);
                    if(stoneTier > 0 && stoneTier < 10) {
                        ItemStack stone = new ItemStack(Material.ARMADILLO_SCUTE);
                        ItemMeta stoneMeta = stone.getItemMeta();
                        List<Component> Lore = new ArrayList<>();
                        String stoneName;
                        if (stoneTier == 9) {
                            stoneName = "Ancient Dragon Smithing Stone";
                            stoneMeta.displayName(Component.text(stoneName, NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                            Lore.add(Component.text("Strengthens armaments to +25.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                        } else {
                            stoneName = "Smithing Stone [" + stoneTier + "]";
                            stoneMeta.displayName(Component.text(stoneName, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
                            Lore.add(Component.text("Strengthens armaments to +" + (3 * stoneTier) + ".", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                        }
                        stoneMeta.lore(Lore);
                        stone.setItemMeta(stoneMeta);
                        player.getInventory().setItem(player.getInventory().firstEmpty(), stone);
                    } else {
                        player.sendMessage("Wrong command usage. /stones 1-10");
                    }
                } catch (NumberFormatException exception) {
                    player.sendMessage("Wrong command usage. /stones 1-10");
                }
            }
        } else {
            commandSender.sendMessage("Only players can use this command");
        }
        return true;
    }
}
