package cz.gennario.newrotatingheads.utils;

import cz.gennario.newrotatingheads.Main;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Level;

@Getter
@Setter
public class PluginUpdater {

    private int resourceId;
    private JavaPlugin plugin;

    private Map<String, String> data;
    private List<String> dataList;

    private String pluginVersion, sitesVersion;

    public PluginUpdater(int resourceId, JavaPlugin plugin) {
        this.resourceId = resourceId;
        this.plugin = plugin;
        this.data = new HashMap<>();
        this.dataList = new ArrayList<>();

        try {
            checkVersions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLoadMessage() {
        PluginDescriptionFile description = plugin.getDescription();
        pluginVersion = description.getVersion();
        Main.getInstance().getLogger().log(Level.INFO, "--------------------------------------------------------------------------------------------");
        Main.getInstance().getLogger().log(Level.INFO, "Loading plugin " + description.getName() + " v." + description.getVersion());
        Main.getInstance().getLogger().log(Level.INFO, "--------------------------------------------------------------------------------------------");
        Main.getInstance().getLogger().log(Level.INFO, "");
        for (String key : data.keySet()) {
            Main.getInstance().getLogger().log(Level.INFO, " " + key + ": " + data.get(key));
        }
        for (String s : dataList) {
            Main.getInstance().getLogger().log(Level.INFO, s);
        }
        Main.getInstance().getLogger().log(Level.INFO, "");
        Main.getInstance().getLogger().log(Level.INFO, "This plugin is running on " + description.getVersion() + "...");
        if (sitesVersion != null)
            Main.getInstance().getLogger().log(Level.INFO, "Current plugin version on polymart is " + sitesVersion + "...");
        if (sitesVersion != null) {
            if (Objects.equals(pluginVersion, sitesVersion)) {
                Main.getInstance().getLogger().log(Level.INFO, "So your plugin is on the latest version...");
            } else {
                Main.getInstance().getLogger().log(Level.INFO, "So your plugin is outdated. Please update the plugin...");
            }
        }
        Main.getInstance().getLogger().log(Level.INFO, "");
        Main.getInstance().getLogger().log(Level.INFO, " Plugin author: " + description.getAuthors());
        Main.getInstance().getLogger().log(Level.INFO, "");
        Main.getInstance().getLogger().log(Level.INFO, "Thanks for choosing Gennario's Development...");
        Main.getInstance().getLogger().log(Level.INFO, "--------------------------------------------------------------------------------------------");
    }

    public void sendErrorOnLoadMessage(String message) {
        PluginDescriptionFile description = plugin.getDescription();
        pluginVersion = description.getVersion();

        Main.getInstance().getLogger().log(Level.WARNING, "--------------------------------------------------------------------------------------------");
        Main.getInstance().getLogger().log(Level.WARNING, "Loading plugin " + description.getName() + " v." + description.getVersion());
        Main.getInstance().getLogger().log(Level.WARNING, "--------------------------------------------------------------------------------------------");
        Main.getInstance().getLogger().log(Level.WARNING, "");
        Main.getInstance().getLogger().log(Level.WARNING, "An error occurred while loading the plugin: " + message);
        Main.getInstance().getLogger().log(Level.WARNING, "Disabling plugin...");
        Main.getInstance().getLogger().log(Level.WARNING, "");
        Main.getInstance().getLogger().log(Level.WARNING, "Thanks for choosing " + "Gennario's Development" + "...");
        Main.getInstance().getLogger().log(Level.WARNING, "--------------------------------------------------------------------------------------------");
    }

    public void checkVersions() throws IOException {
        if (resourceId == 0) return;
        URLConnection con = new URL("https://api.polymart.org/v1/getResourceInfoSimple/?resource_id=" + this.resourceId + "&key=version").openConnection();
        this.sitesVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
    }

}
