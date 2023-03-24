package cz.gennario.newrotatingheads.utils;

import cz.gennario.newrotatingheads.utils.centermessage.CenterMessage;
import cz.gennario.newrotatingheads.utils.iridiumcolorapi.IridiumColorAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
}
