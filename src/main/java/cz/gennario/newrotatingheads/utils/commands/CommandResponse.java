package cz.gennario.newrotatingheads.utils.commands;

import org.bukkit.command.CommandSender;

public interface CommandResponse {

    void cmd(CommandSender sender, String label, CommandArg[] commandArgs);

}
