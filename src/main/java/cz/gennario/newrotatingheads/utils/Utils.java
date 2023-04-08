package cz.gennario.newrotatingheads.utils;

import com.comphenix.protocol.wrappers.Pair;
import cz.gennario.newrotatingheads.Main;
import cz.gennario.newrotatingheads.utils.centermessage.CenterMessage;
import cz.gennario.newrotatingheads.utils.config.Config;
import cz.gennario.newrotatingheads.utils.iridiumcolorapi.IridiumColorAPI;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public final class Utils {

    public static String colorize(Player player, String string) {
        String playerName = "%player%";
        if (player != null && player.isOnline()) playerName = player.getName();
        string = string.replace("%player%", playerName);

        String s = string;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            if (player != null && player.isOnline()) {
                s = PlaceholderAPI.setPlaceholders(player, string);
            } else {
                s = PlaceholderAPI.setPlaceholders(null, string);
            }
        }

        s = s.replace("§l", "&l");
        s = IridiumColorAPI.process(s);

        if (s.startsWith("<center>")) {
            s = CenterMessage.getCenteredMessage(s.replaceFirst("<center>", ""));
        }

        return s;
    }

    public static List<String> colorize(Player player, String... strings) {
        List<String> list = new ArrayList<>();
        for (String string : strings) {
            String playerName = "%player%";
            if (player != null && player.isOnline()) playerName = player.getName();
            string = string.replace("%player%", playerName);

            String s = string;
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                if (player != null && player.isOnline()) {
                    s = PlaceholderAPI.setPlaceholders(player, string);
                } else {
                    s = PlaceholderAPI.setPlaceholders(null, string);
                }
            }
            s = s.replace("§l", "&l");
            s = IridiumColorAPI.process(s);

            if (s.startsWith("<center>")) {
                s = CenterMessage.getCenteredMessage(s.replaceFirst("<center>", ""));
            }
            list.add(s);
        }

        return list;
    }

    public static String getMinecraftVersion(Server server) {
        String version = server.getVersion();
        int start = version.indexOf("MC: ") + 4;
        int end = version.length() - 1;
        return version.substring(start, end);
    }

    public static boolean isOldVersion() {
        return Integer.parseInt(Utils.getMinecraftVersion(Bukkit.getServer()).split("\\.")[1]) < 13;
    }

    public static boolean versionIs(int version) {
        return Integer.parseInt(Utils.getMinecraftVersion(Bukkit.getServer()).split("\\.")[1]) == version;
    }

    public static boolean versionIsAfter(int version) {
        return Integer.parseInt(Utils.getMinecraftVersion(Bukkit.getServer()).split("\\.")[1]) > version;
    }

    public static boolean versionIsBefore(int version) {
        return Integer.parseInt(Utils.getMinecraftVersion(Bukkit.getServer()).split("\\.")[1]) < version;
    }

    public static boolean versionIsBeforeOrEqual(int version) {
        return Integer.parseInt(Utils.getMinecraftVersion(Bukkit.getServer()).split("\\.")[1]) <= version;
    }

    public static boolean versionIsAfterOrEqual(int version) {
        return Integer.parseInt(Utils.getMinecraftVersion(Bukkit.getServer()).split("\\.")[1]) >= version;
    }


    public static Location getLocation(String s) {
        String[] splitted = s.replace(")", "").split("\\(");
        if (splitted.length == 2) {
            World world = Bukkit.getWorld(splitted[0]);
            double x = Double.parseDouble(splitted[1].split(",")[0]);
            double y = Double.parseDouble(splitted[1].split(",")[1]);
            double z = Double.parseDouble(splitted[1].split(",")[2]);
            return new Location(world, x, y, z);
        }
        return null;
    }

    public static String locationToString(Location location) {
        String loc = location.getWorld().getName() + "(";
        loc += location.getX() + ",";
        loc += location.getY() + ",";
        loc += location.getZ() + ")";
        return loc;
    }

    public static String locationToStringCenter(Location location) {
        String loc = location.getWorld().getName() + "(";
        loc += (location.getX() + 0.5) + ",";
        loc += location.getY() + ",";
        loc += (location.getZ() + 0.5) + ")";
        return loc;
    }

    public static void optiomizeConfiguration(String path) {
        Config config = new Config(Main.getInstance(), path);
        try {
            config.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        YamlDocument yamlDocument = config.getYamlDocument();
        yamlDocument.set("settings.type", "STAND");

        List<String> animationsList = yamlDocument.getStringList("settings.animations", new ArrayList<>());
        if (yamlDocument.contains("settings.animations")) yamlDocument.remove("settings.animations");
        for (String s : animationsList) {
            StringReader stringReader = new StringReader(s);
            stringReader.convert();

            switch (stringReader.getMainValue().toLowerCase()) {
                case "rotate":

                    yamlDocument.set("settings.animations.rotate.type", "Rotate");
                    if (valueExist(stringReader, "direction")) {
                        Pair<String, String> direction = stringReader.getStringFromData(getData(stringReader, "direction"));
                        yamlDocument.set("settings.animations.rotate.direction", direction.getSecond());
                    }
                    if (valueExist(stringReader, "speed")) {
                        Pair<String, Integer> speed = stringReader.getIntFromData(getData(stringReader, "speed"));
                        yamlDocument.set("settings.animations.rotate.speed", speed.getSecond());
                    }
                    break;
                case "updown":

                    yamlDocument.set("settings.animations.updown.type", "UpDown");
                    if (valueExist(stringReader, "value")) {
                        Pair<String, Double> jump = stringReader.getDoubleFromData(getData(stringReader, "value"));
                        yamlDocument.set("settings.animations.updown.jump", jump.getSecond());
                    }
                    if (valueExist(stringReader, "jumps")) {
                        Pair<String, Integer> jumps = stringReader.getIntFromData(getData(stringReader, "jumps"));
                        yamlDocument.set("settings.animations.updown.jumps", jumps.getSecond());
                    }
                    if (valueExist(stringReader, "smooth")) {
                        Pair<String, Boolean> smooth = stringReader.getBooleanFromData(getData(stringReader, "smooth"));
                        yamlDocument.set("settings.animations.updown.smooth", smooth.getSecond());
                    }
                    break;
            }
        }

        if (yamlDocument.contains("head.value")) {
            StringReader stringReader = new StringReader(yamlDocument.getString("head.value"));
            stringReader.convert();

            yamlDocument.set("equipment.HEAD.material", stringReader.getMainValue());

            if (valueExist(stringReader, "amount")) {
                Pair<String, Integer> amount = stringReader.getIntFromData(getData(stringReader, "amount"));
                yamlDocument.set("equipment.HEAD.amount", amount.getSecond());
            }

            if (valueExist(stringReader, "skin")) {
                Pair<String, String> skin = stringReader.getStringFromData(getData(stringReader, "skin"));
                yamlDocument.set("equipment.HEAD.player", skin.getSecond());
            }

            if (valueExist(stringReader, "base64")) {
                Pair<String, String> base64 = stringReader.getStringFromData(getData(stringReader, "base64"));
                yamlDocument.set("equipment.HEAD.base64", base64.getSecond());
            }

            if (valueExist(stringReader, "data")) {
                Pair<String, Integer> data = stringReader.getIntFromData(getData(stringReader, "data"));
                yamlDocument.set("equipment.HEAD.data", data.getSecond());
            }

            if (valueExist(stringReader, "custommodeldata")) {
                Pair<String, Integer> custommodeldata = stringReader.getIntFromData(getData(stringReader, "custommodeldata"));
                yamlDocument.set("equipment.HEAD.custommodeldata", custommodeldata.getSecond());
            }

            yamlDocument.remove("head");
        }

        if (yamlDocument.contains("particles")) yamlDocument.remove("particles");

        if (yamlDocument.contains("name")) {
            Double offset = yamlDocument.getDouble("name.offset", 0.0);
            Double space = yamlDocument.getDouble("name.space", 0.0);

            yamlDocument.set("hologram.offset.x", 0.0);
            yamlDocument.set("hologram.offset.y", offset);
            yamlDocument.set("hologram.offset.z", 0.0);

            yamlDocument.set("hologram.space", space);
            yamlDocument.set("hologram.attach-bottom", false);
            yamlDocument.set("hologram.update-location", true);

            yamlDocument.set("hologram.lines", yamlDocument.get("name.lines", new ArrayList<>()));

            yamlDocument.remove("name");
        }

        if (yamlDocument.contains("actions")) {
            List<String> actions = new ArrayList<>(yamlDocument.getStringList("actions"));

            for (String action : actions) {
                StringReader stringReader = new StringReader(action);
                stringReader.convert();

                String actionType = stringReader.getMainValue();
                String click = "ALL";

                if (valueExist(stringReader, "clicks")) {
                    Pair<String, String> clicks = stringReader.getStringFromData(getData(stringReader, "clicks"));
                    click = clicks.getSecond().replace(":", "+");
                }

                yamlDocument.set("actions." + click + ".type", actionType);
                if (valueExist(stringReader, "value")) {
                    yamlDocument.set("actions." + click + ".value", stringReader.getStringFromData(getData(stringReader, "value")).getSecond());
                }
            }
        }

        try {
            yamlDocument.save();
            yamlDocument.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean valueExist(StringReader reader, String value) {
        return reader.getDataByValue(value) != null;
    }

    public static String getData(StringReader reader, String value) {
        return reader.getDataByValue(value);
    }
}
