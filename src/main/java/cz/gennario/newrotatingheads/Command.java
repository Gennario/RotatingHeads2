package cz.gennario.newrotatingheads;

import cz.gennario.newrotatingheads.system.RotatingHead;
import cz.gennario.newrotatingheads.utils.TextComponentUtils;
import cz.gennario.newrotatingheads.utils.Utils;
import cz.gennario.newrotatingheads.utils.commands.CommandAPI;
import cz.gennario.newrotatingheads.utils.commands.SubCommandArg;
import cz.gennario.newrotatingheads.utils.language.LanguageAPI;
import cz.gennario.newrotatingheads.utils.replacement.Replacement;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Getter
public class Command {

    private final YamlDocument document;
    private final CommandAPI command;
    private LanguageAPI language;

    public Command() {
        document = Main.getInstance().getConfigFile().getYamlDocument();
        language = Main.getInstance().getLanguageAPI();

        command = new CommandAPI(Main.getInstance(), "rotatingheads")
                .setAliases("rh", "rheads", "rotatingh", "rh2", "rotatingheads2")
                .setDescription("Main command for RotatingHeads2")
                .setLanguageAPI(language)
                .setHelp(true);
        command.setEmptyCommandResponse((sender, label, commandArgs) -> {
            String prefix = language.getPrefix();
            sender.sendMessage(prefix + "§rThis server uses the §cRotatingHeads 2 §rplugin version §c" + Main.getInstance().getPluginUpdater().getPluginVersion() +
                    "§r, created by developer §cGennario§r...");
            if (sender.hasPermission("rh.help")) {
                sender.sendMessage(language.getMessage("commands.usage", null, new Replacement((player, string) -> string.replace("%label%", label).replace("%help%", "help"))).toArray(new String[0]));
            }
        });

        loadReloadCommand();
        loadListCommand();
        loadTeleportCommand();

        command.buildCommand();
    }

    public void loadReloadCommand() {
        command.addCommand("reload")
                .setAliases("rl")
                .setUsage("reload")
                .setPermission("rh.reload")
                .setDescription("Reload whole plugin")
                .setAllowConsoleSender(true)
                .setResponse((commandSender, s, commandArgs) -> {
                    long start = System.currentTimeMillis();
                    for (RotatingHead head : Main.getInstance().getHeads().values()) {
                        head.deleteHead();
                    }
                    try {
                        Main.getInstance().getConfigFile().getYamlDocument().reload();


                        Main.getInstance().loadLanguage();

                        File heads = Main.getInstance().createHeadsFolder();
                        Main.getInstance().loadHeads(heads);

                        language = Main.getInstance().getLanguageAPI();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    commandSender.sendMessage(language.getMessage("messages.reload", null, new Replacement((player, s1) -> s1.replace("%time%", "" + (System.currentTimeMillis() - start)))).toArray(new String[0]));
                });
    }

    public void loadListCommand() {
        command.addCommand("list")
                .setAliases("li")
                .setUsage("list")
                .setPermission("rh.list")
                .setDescription("Shows list of all heads")
                .setAllowConsoleSender(true)
                .setResponse((commandSender, s, commandArgs) -> {

                    for (String s1 : language.getStringList("messages.list.header", null, new Replacement((player, string) -> string))) {
                        commandSender.sendMessage(Utils.colorize(null, s1));
                    }

                    for (String headName : Main.getInstance().getHeads().keySet()) {
                        RotatingHead head = Main.getInstance().getHeadByName(headName);
                        Location clone = head.getLocation().clone();
                        if (commandSender instanceof Player) {
                            Player player = (Player) commandSender;
                            TextComponent textComponent = TextComponentUtils.create(Utils.colorize(player, language.getString("messages.list.format", player,
                                    new Replacement((player1, string) -> string.replace("%head%", headName).replace("%world%", clone.getWorld().getName()).replace("%x%", "" + clone.getX()).replace("%y%", "" + clone.getY()).replace("%z%", "" + clone.getZ())))));
                            TextComponentUtils textComponentUtils = new TextComponentUtils();
                            textComponentUtils.setClick(textComponent, ClickEvent.Action.RUN_COMMAND, "/rh teleport " + headName+ " "+player.getName()+" --silent");
                            textComponentUtils.setHover(textComponent, language.getColoredString("messages.list.hover", player));
                            TextComponentUtils.send(player, textComponent);
                        } else {
                            commandSender.sendMessage(Utils.colorize(null, language.getString("messages.list.format", null,
                                    new Replacement((player1, string) -> string.replace("%head%", headName).replace("%world%", clone.getWorld().getName()).replace("%x%", "" + clone.getX()).replace("%y%", "" + clone.getY()).replace("%z%", "" + clone.getZ())))));
                        }
                    }

                    for (String s1 : language.getStringList("messages.list.footer", null, new Replacement((player, string) -> string))) {
                        commandSender.sendMessage(Utils.colorize(null, s1));
                    }
                });
    }

    public void loadTeleportCommand() {
        command.addCommand("teleport")
                .setAliases("tp", "tpa")
                .setUsage("teleport <head> [player] [--silent]")
                .setPermission("rh.teleport")
                .setDescription("Teleports to specific head")
                .addArg("head", SubCommandArg.CommandArgType.REQUIRED, SubCommandArg.CommandArgValue.HEAD)
                .addArg("player", SubCommandArg.CommandArgType.OPTIONAL, SubCommandArg.CommandArgValue.PLAYER)
                .addArg("message", SubCommandArg.CommandArgType.OPTIONAL, SubCommandArg.CommandArgValue.STRING, Arrays.asList("--silent"))
                .setAllowConsoleSender(true)
                .setResponse((commandSender, s, commandArgs) -> {

                    if (commandSender instanceof ConsoleCommandSender && commandArgs.length == 1) {
                        commandSender.sendMessage(language.getMessage("commands.disabled-console", null, new Replacement((player, string) -> string)).toArray(new String[0]));
                        return;
                    }
                    RotatingHead head = commandArgs[0].getAsHead();

                    if (commandArgs.length > 1) {
                        Player argPlayer = commandArgs[1].getAsPlayer();

                        boolean silent = false;
                        if (commandArgs.length > 2) {
                            if (commandArgs[2].getAsString().equalsIgnoreCase("--silent")) {
                                silent = true;
                            }
                        }

                        String sString = "";
                        if (silent)
                            sString = language.getColoredString("messages.teleport.silent", null);
                        argPlayer.teleport(head.getLocation());

                        String finalSString = sString;
                        List<String> message = language.getMessage("messages.teleport.other", null, new Replacement((player, string) ->
                                string.replace("%head%", head.getName()).replace("%player%", argPlayer.getName()).replace("%silent%", finalSString)));
                        for (String s1 : message) {
                            commandSender.sendMessage(s1);
                        }
                        if(!silent) {
                            List<String> message1 = language.getMessage("messages.teleport.other-player", null, new Replacement((player, string) ->
                                    string.replace("%head%", head.getName()).replace("%sender%", commandSender.getName()).replace("%silent%", finalSString)));
                            for (String s1 : message1) {
                                argPlayer.sendMessage(s1);
                            }
                        }
                    } else {
                        Player sender = (Player) commandSender;

                        sender.teleport(head.getLocation());
                        List<String> message = language.getMessage("messages.teleport.self", sender, new Replacement((player, string) -> string.replace("%head%", head.getName())));
                        for (String s1 : message) {
                            sender.sendMessage(s1);
                        }
                    }
                });
    }
}
