package org.cyril.dsplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SummonGrace extends Command {
    protected SummonGrace(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            if(strings.length >= 1 && !strings[0].isBlank()) {
                Player player = (Player) commandSender;
                Entity grace = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
                grace.setInvisible(true);
                grace.setInvulnerable(true);
                grace.setGravity(false);
                grace.addScoreboardTag("grace");
                grace.customName(Component.text(""));
                String name = String.join(" ", strings);
                grace.customName(Component.text(name));
            } else {
                commandSender.sendMessage(Component.text("Missing an argument. /creategrace >>NAME<<", NamedTextColor.RED).decoration(TextDecoration.ITALIC,false));
            }
        } else {
            commandSender.sendMessage("Only players can use this command.");
        }
        return true;
    }
}
