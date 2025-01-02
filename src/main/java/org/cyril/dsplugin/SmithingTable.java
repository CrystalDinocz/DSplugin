package org.cyril.dsplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SmithingTable extends Command {
    protected SmithingTable(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            TriggerEvents.upgradeMenu(player);
        } else {
            commandSender.sendMessage("Only players can use this command.");
        }
        return true;
    }
}
