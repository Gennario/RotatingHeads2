package cz.gennario.newrotatingheads.utils;

import cz.gennario.newrotatingheads.utils.centermessage.CenterMessage;
import cz.gennario.newrotatingheads.utils.iridiumcolorapi.IridiumColorAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Server;
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
}
