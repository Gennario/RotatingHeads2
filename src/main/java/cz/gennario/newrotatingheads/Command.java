package cz.gennario.newrotatingheads;

import com.github.javafaker.Faker;
import cz.gennario.newrotatingheads.developer.events.HeadReloadEvent;
import cz.gennario.newrotatingheads.developer.events.HeadUnloadEvent;
import cz.gennario.newrotatingheads.system.RotatingHead;
import cz.gennario.newrotatingheads.utils.TextComponentUtils;
import cz.gennario.newrotatingheads.utils.Utils;
import cz.gennario.newrotatingheads.utils.commands.CommandAPI;
import cz.gennario.newrotatingheads.utils.commands.SubCommandArg;
import cz.gennario.newrotatingheads.utils.config.Config;
import cz.gennario.newrotatingheads.utils.language.LanguageAPI;
import cz.gennario.newrotatingheads.utils.replacement.Replacement;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Getter
public class Command {

    private final YamlDocument document;
    private final CommandAPI command;
    private LanguageAPI language;

    private Map<Player, String> headRemove = new HashMap<>();

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
        loadCreateCommand();
        loadDeleteCommand();
        loadMovehereCommand();
        loadConvertCommand();

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
                    Map<String, RotatingHead> oldHeads = new HashMap<>();
                    for (RotatingHead head : Main.getInstance().getHeadsList()) {
                        head.deleteHead();
                        oldHeads.put(head.getName(), head);
                    }
                    Main.getInstance().getHeads().clear();
                    try {
                        Main.getInstance().getConfigFile().getYamlDocument().reload();


                        Main.getInstance().loadLanguage();

                        File heads = Main.getInstance().createHeadsFolder();
                        Main.getInstance().loadHeads(heads);

                        for (RotatingHead head : Main.getInstance().getHeadsList()) {
                            if (oldHeads.containsKey(head.getName())) {
                                RotatingHead oldHead = oldHeads.get(head.getName());
                                HeadReloadEvent reloadEvent = new HeadReloadEvent(oldHead, head);
                                Bukkit.getPluginManager().callEvent(reloadEvent);
                            }
                        }

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
                            textComponentUtils.setClick(textComponent, ClickEvent.Action.RUN_COMMAND, "/rh teleport " + headName + " " + player.getName() + " --silent");
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
                        if (!silent) {
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

    public void loadCreateCommand() {
        Faker faker = new Faker();
        command.addCommand("create")
                .setUsage("create <head> [--center]")
                .setPermission("rh.create")
                .setDescription("Creates new head")
                .addArg("head", SubCommandArg.CommandArgType.REQUIRED, SubCommandArg.CommandArgValue.STRING, Arrays.asList(faker.app().name(), faker.app().name(), faker.app().name(), faker.app().name(), faker.app().name(), faker.app().name(), faker.app().name(), faker.app().name(), faker.app().name()))
                .addArg("position", SubCommandArg.CommandArgType.OPTIONAL, SubCommandArg.CommandArgValue.STRING, Arrays.asList("--center"))
                .setAllowConsoleSender(false)
                .setResponse((commandSender, s, commandArgs) -> {

                    String name = commandArgs[0].getAsString();

                    if (Main.getInstance().getHeads().containsKey(name)) {
                        String ideas = faker.app().name(); //faker.app().name();
                        for (int i = 0; i < 2; i++) {
                            ideas += ", " + faker.app().name(); //faker.app().name();
                        }
                        for (int i = 0; i < 2; i++) {
                            ideas += ", " + name + RandomStringUtils.random(3, false, true); //faker.app().name();
                        }
                        String finalIdeas = ideas;
                        commandSender.sendMessage(language.getMessage("messages.create.exist",
                                null,
                                new Replacement((player, string) -> string.replace("%name%", name).replace("%name_ideas%", finalIdeas))).toArray(new String[0]));
                        return;
                    }

                    boolean center = false;
                    if (commandArgs.length > 1) {
                        center = commandArgs[1].getAsString().equalsIgnoreCase("--center");
                    }

                    Player player = (Player) commandSender;
                    Location location = player.getLocation().clone();

                    Config heads = new Config(Main.getInstance(), "heads", name, Main.getInstance().getResource("heads/creation.yml"));
                    try {
                        heads.load();
                        if (!center) {
                            heads.getYamlDocument().set("settings.location", Utils.locationToString(location));
                        } else {
                            heads.getYamlDocument().set("settings.location", Utils.locationToStringCenter(location));
                            location = location.getBlock().getLocation();
                            location.setX(location.getX() + 0.5);
                            location.setZ(location.getZ() + 0.5);
                        }

                        heads.getYamlDocument().save();
                        heads.getYamlDocument().reload();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    RotatingHead head = new RotatingHead(location, name, true);
                    head.loadFromConfig();
                    head.updateHead();
                    Main.getInstance().getHeads().put(name, head);

                    commandSender.sendMessage(language.getMessage("messages.create.created",
                            null,
                            new Replacement((playe, string) -> string.replace("%name%", name))).toArray(new String[0]));
                });
    }


    public void loadDeleteCommand() {
        command.addCommand("delete")
                .setAliases("remove")
                .setUsage("delete <head> [--force]")
                .setPermission("rh.delete")
                .setDescription("Deletes head")
                .addArg("head", SubCommandArg.CommandArgType.REQUIRED, SubCommandArg.CommandArgValue.HEAD)
                .addArg("priority", SubCommandArg.CommandArgType.OPTIONAL, SubCommandArg.CommandArgValue.STRING, Arrays.asList("--force"))
                .setAllowConsoleSender(false)
                .setResponse((commandSender, s, commandArgs) -> {
                    Player player = (Player) commandSender;

                    RotatingHead head = commandArgs[0].getAsHead();
                    String name = head.getName();

                    if (headRemove.containsKey(player)) {
                        if (headRemove.get(player).equals(name)) {

                            YamlDocument yamlDocument = head.getYamlDocument();
                            if (yamlDocument != null) {
                                File file = new File(Main.getInstance().getDataFolder() + "/heads/" + head.getName() + ".yml");
                                file.delete();
                            }
                            head.deleteHead();

                            commandSender.sendMessage(language.getMessage("messages.delete.deleted",
                                    null,
                                    new Replacement((playe, string) -> string.replace("%name%", name))).toArray(new String[0]));

                            headRemove.remove(player);
                        } else {
                            commandSender.sendMessage(language.getMessage("messages.delete.protection",
                                    null,
                                    new Replacement((playe, string) -> string.replace("%name%", name))).toArray(new String[0]));
                            headRemove.put(player, name);
                        }
                    } else {
                        commandSender.sendMessage(language.getMessage("messages.delete.protection",
                                null,
                                new Replacement((playe, string) -> string.replace("%name%", name))).toArray(new String[0]));
                        headRemove.put(player, name);
                    }
                });
    }


    public void loadMovehereCommand() {
        command.addCommand("movehere")
                .setAliases("mh")
                .setUsage("movehere <head> [--center]")
                .setPermission("rh.movehere")
                .setDescription("Move head to your location")
                .addArg("head", SubCommandArg.CommandArgType.REQUIRED, SubCommandArg.CommandArgValue.HEAD)
                .addArg("position", SubCommandArg.CommandArgType.OPTIONAL, SubCommandArg.CommandArgValue.STRING, Arrays.asList("--center"))
                .setAllowConsoleSender(false)
                .setResponse((commandSender, s, commandArgs) -> {
                    RotatingHead head = commandArgs[0].getAsHead();
                    boolean center = false;
                    if (commandArgs.length > 1) {
                        center = commandArgs[1].getAsString().equalsIgnoreCase("--center");
                    }

                    Player player = (Player) commandSender;
                    Location location = player.getLocation().clone();

                    YamlDocument heads = head.getYamlDocument();
                    try {
                        if (!center) {
                            heads.set("settings.location", Utils.locationToString(location));
                        } else {
                            heads.set("settings.location", Utils.locationToStringCenter(location));
                            location = location.getBlock().getLocation();
                            location.setX(location.getX() + 0.5);
                            location.setZ(location.getZ() + 0.5);
                        }

                        heads.save();
                        heads.reload();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    head.deleteHead();
                    Main.getInstance().loadHead(head.getName());
                });
    }

    public void loadConvertCommand() {
        command.addCommand("convert")
                .setUsage("convert <plugin>")
                .setPermission("rh.convert")
                .setDescription("Transfer files from old RotatingHeads")
                .addArg("plugin", SubCommandArg.CommandArgType.REQUIRED, SubCommandArg.CommandArgValue.STRING, Arrays.asList("RH-REBORN", "RH-PRO"))
                .setAllowConsoleSender(true)
                .setResponse((commandSender, s, commandArgs) -> {
                    String plugin = commandArgs[0].getAsString();
                    long start = System.currentTimeMillis();

                    switch (plugin) {
                        case "RH-REBORN":
                            File file = new File(Main.getInstance().getDataFolder().toString().replace("/RotatingHeads2", "") + "/RotatingHeads");
                            if (file.exists()) {
                                commandSender.sendMessage(language.getMessage("messages.convert.start",
                                        null,
                                        new Replacement((playe, string) -> string.replace("%type%", plugin))).toArray(new String[0]));

                                File file1 = new File(file.getPath() + "/heads");
                                List<File> files = new ArrayList<>();
                                if (file1.exists()) {
                                    listFiles(file1, files);
                                }

                                for (File file2 : files) {
                                    try {
                                        Files.copy(file2.toPath(), Paths.get(Main.getInstance().getDataFolder() + "/heads/" + file2.getName()), StandardCopyOption.REPLACE_EXISTING);
                                        Utils.optiomizeConfiguration("/heads/" + file2.getName());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                commandSender.sendMessage(language.getMessage("messages.convert.transfer",
                                        null,
                                        new Replacement((playe, string) -> string.replace("%type%", plugin).replace("%time%", "" + (System.currentTimeMillis() - start)))).toArray(new String[0]));
                            } else {
                                commandSender.sendMessage(language.getMessage("messages.convert.no-files-found",
                                        null,
                                        new Replacement((playe, string) -> string.replace("%type%", plugin))).toArray(new String[0]));
                            }
                            break;
                        case "RH-PRO":
                            File file2 = new File(Main.getInstance().getDataFolder().toString().replace("/RotatingHeads2", "") + "/RotatingHeadsPRO");
                            if (file2.exists()) {
                                commandSender.sendMessage(language.getMessage("messages.convert.start",
                                        null,
                                        new Replacement((playe, string) -> string.replace("%type%", plugin))).toArray(new String[0]));

                                File file1 = new File(file2.getPath() + "/heads");
                                List<File> files = new ArrayList<>();
                                if (file1.exists()) {
                                    listFiles(file1, files);
                                }

                                for (File file3 : files) {
                                    try {
                                        Files.copy(file3.toPath(), Paths.get(Main.getInstance().getDataFolder() + "/heads/" + file3.getName()), StandardCopyOption.REPLACE_EXISTING);
                                        Utils.optiomizeConfiguration("/heads/" + file3.getName());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                commandSender.sendMessage(language.getMessage("messages.convert.transfer",
                                        null,
                                        new Replacement((playe, string) -> string.replace("%type%", plugin).replace("%time%", "" + (System.currentTimeMillis() - start)))).toArray(new String[0]));
                            } else {
                                commandSender.sendMessage(language.getMessage("messages.convert.no-files-found",
                                        null,
                                        new Replacement((playe, string) -> string.replace("%type%", plugin))).toArray(new String[0]));
                            }
                            break;
                        default:
                            commandSender.sendMessage(language.getMessage("messages.convert.invalid-type",
                                    null,
                                    new Replacement((playe, string) -> string)).toArray(new String[0]));
                            break;
                    }
                });
    }

    public void listFiles(File file, List<File> files) {
        for (File listFile : file.listFiles()) {
            if (listFile.isDirectory()) {
                listFiles(listFile, files);
            } else {
                files.add(listFile);
            }
        }
    }
}
