package cz.gennario.newrotatingheads;

import com.comphenix.protocol.ProtocolLibrary;
import cz.gennario.newrotatingheads.system.HeadInteraction;
import cz.gennario.newrotatingheads.system.HeadRunnable;
import cz.gennario.newrotatingheads.system.RotatingHead;
import cz.gennario.newrotatingheads.system.animations.AnimationLoader;
import cz.gennario.newrotatingheads.rotatingengine.actions.ActionsAPI;
import cz.gennario.newrotatingheads.rotatingengine.conditions.ConditionsAPI;
import cz.gennario.newrotatingheads.utils.PluginUpdater;
import cz.gennario.newrotatingheads.utils.Utils;
import cz.gennario.newrotatingheads.utils.config.Config;
import cz.gennario.newrotatingheads.utils.debug.Logger;
import cz.gennario.newrotatingheads.utils.language.LanguageAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public final class Main extends JavaPlugin {

    public static Main instance;
    private PluginUpdater pluginUpdater;

    private ConditionsAPI conditionsAPI;
    private ActionsAPI actionsAPI;

    private Map<String, RotatingHead> heads;
    private AnimationLoader animationLoader;

    private Logger log;

    private Config configFile;
    private LanguageAPI languageAPI;
    private Command command;

    @Override
    public void onEnable() {
        instance = this;
        log = new Logger(Main.getInstance());
        pluginUpdater = new PluginUpdater(2203, this);

        if (Utils.versionIsBeforeOrEqual(13)) {
            pluginUpdater.sendErrorOnLoadMessage("Unsupported server version. Use version from 1.14 to Latest.");

            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled(ProtocolLibrary.getPlugin())) {
            pluginUpdater.sendErrorOnLoadMessage("ProtocolLibrary is not enabled/installed!");

            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        heads = new HashMap<>();

        configFile = new Config(this, "", "config", getResource("config.yml"))
                .setUpdate(true);
        try {
            configFile.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        animationLoader = new AnimationLoader();
        animationLoader.loadDefaults();

        actionsAPI = new ActionsAPI();
        conditionsAPI = new ConditionsAPI();

        /*
         *  LANGUAGE LOADER
         * */
        loadLanguage();
        command = new Command();

        /*
         *  HEADS LOADER
         * */
        File heads = createHeadsFolder();
        loadHeads(heads);


        new HeadInteraction().register();
        new HeadRunnable().runTaskTimerAsynchronously(this, 1, 1);

        pluginUpdater.sendLoadMessage();
    }

    @Override
    public void onDisable() {
        for (RotatingHead head : getHeadsList()) {
            head.deleteHead();
        }
    }

    public void loadLanguage() {
        languageAPI = new LanguageAPI(this);
        languageAPI.addLanguage("en_GB");
        languageAPI.setActiveLanguage(configFile.getYamlDocument().getString("language"), new ArrayList<>(languageAPI.getLanguages().values()).get(0).getName());
    }

    public File createHeadsFolder() {
        File heads = new File(getDataFolder() + "/heads/");
        if (!heads.exists()) {
            heads.mkdir();
            try {
                new Config(this, "heads", "example_stand", getResource("heads/example_stand.yml")).load();
                new Config(this, "heads", "example_entity", getResource("heads/example_entity.yml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return heads;
    }

    public void loadHeads(File heads) {
        for (File file : heads.listFiles()) {
            String name = file.getName().replace(".yml", "");

            RotatingHead rotatingHead = new RotatingHead(null, name, true);
            rotatingHead.loadFromConfig();
            rotatingHead.updateHead();

            this.heads.put(name, rotatingHead);
        }
    }

    public void loadHead(String name) {
        RotatingHead rotatingHead = new RotatingHead(null, name, true);
        rotatingHead.loadFromConfig();
        rotatingHead.updateHead();

        this.heads.put(name, rotatingHead);
    }

    public RotatingHead getHeadByName(String name) {
        if (heads.containsKey(name)) return heads.get(name);
        return null;
    }

    public List<RotatingHead> getHeadsList() {
        return new ArrayList<>(heads.values());
    }

    public static Main getInstance() {
        return instance;
    }

    public Logger getLog() {
        return log;
    }
}
