package org.cyril.dsplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TorrentWhistle extends Command {
    protected TorrentWhistle(@NotNull String name) {
        super(name);
    }
    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
            ItemStack whistle = new ItemStack(Material.HONEYCOMB);
            ItemMeta whistleMeta = whistle.getItemMeta();
            List<Component> Lore = new ArrayList<>();
            whistleMeta.displayName(Component.text("Spectral Steed Whistle", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC,false));
            Lore.add(Component.text("Summon and ride Torrent, the spectral steed.", NamedTextColor.WHITE));
            Lore.add(Component.text(""));
            TextComponent lore2 = Component.text("Press ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                    .append(Component.keybind("key.use", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .append(Component.text(" to jump while riding Torrent.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));
            Lore.add(lore2);
            whistleMeta.lore(Lore);
            whistle.setItemMeta(whistleMeta);
            player.getInventory().setItem(player.getInventory().firstEmpty(), whistle);
            return true;
        } else {
            commandSender.sendMessage("Only players can use this command.");
            return true;
        }
    }
}
