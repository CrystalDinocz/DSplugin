package org.cyril.dsplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class TestCommand extends Command {
    Dsplugin dsInstance = new Dsplugin();
    HashMap<String, Float> stats = dsInstance.getStats();
    protected TestCommand(@NotNull String name) {
        super(name);
    }
    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();
            player.sendMessage("test");
            return true;
        } else {
            commandSender.sendMessage("Only players can use this command.");
            return true;
        }
    }
}
