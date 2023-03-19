package cz.gennario.newrotatingheads.utils.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Getter
@Setter
public class Config {

    private final JavaPlugin plugin;
    private String path, name;
    private boolean update;

    private YamlDocument yamlDocument;
    private InputStream resource;

    private UpdaterSettings.Builder updaterSettings;
    private LoaderSettings.Builder loaderSettings;

    public Config(JavaPlugin plugin, String path, String name, InputStream resource) {
        this.plugin = plugin;
        this.path = path;
        this.name = name;
        this.resource = resource;

        this.loaderSettings = LoaderSettings.builder().setAutoUpdate(true);
        this.updaterSettings = UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"));
    }

    public Config(JavaPlugin plugin, String path, String name) {
        this.plugin = plugin;
        this.path = path;
        this.name = name;
        this.resource = resource;

        this.loaderSettings = LoaderSettings.builder().setAutoUpdate(true);
        this.updaterSettings = UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"));
    }

    public Config(JavaPlugin plugin, String path, String name, boolean updater) {
        this.plugin = plugin;
        this.path = path;
        this.name = name;
        this.resource = resource;
        this.update = updater;

        this.loaderSettings = LoaderSettings.builder().setAutoUpdate(true);
        this.updaterSettings = UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"));
    }

    public Config(JavaPlugin plugin, String path) {
        this.plugin = plugin;
        this.path = path;
        this.resource = resource;

        this.loaderSettings = LoaderSettings.builder().setAutoUpdate(true);
        this.updaterSettings = UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"));
    }

    public Config setName(String name) {
        this.name = name;
        return this;
    }

    public Config setUpdate(boolean update) {
        this.update = update;
        return this;
    }

    public Config setPath(String path) {
        this.path = path;
        return this;
    }

    public Config setLoaderSettings(LoaderSettings.Builder loaderSettings) {
        this.loaderSettings = loaderSettings;
        return this;
    }

    public Config setResource(InputStream resource) {
        this.resource = resource;
        return this;
    }

    public Config setUpdaterSettings(UpdaterSettings.Builder updaterSettings) {
        this.updaterSettings = updaterSettings;
        return this;
    }

    public void setYamlDocument(YamlDocument yamlDocument) {
        this.yamlDocument = yamlDocument;
    }

    public void load() throws IOException {
        String path;
        if (this.name == null) {
            path = this.path;
        } else {
            path = this.path + File.separator + name + ".yml";
        }
        if (!update) {
            yamlDocument = YamlDocument.create(new File(plugin.getDataFolder(), path), resource);
            return;
        }
        yamlDocument = YamlDocument.create(new File(plugin.getDataFolder(), path), resource,
                GeneralSettings.DEFAULT,
                loaderSettings.build(),
                DumperSettings.DEFAULT,
                updaterSettings.build());
    }

    public void loadIfNotExist() throws IOException {
        String path;
        if (this.name == null) {
            path = this.path;
        } else {
            path = this.path + File.separator + name + ".yml";
        }
        if(new File(plugin.getDataFolder()+"/"+path).exists()) {
            if (!update) {
                yamlDocument = YamlDocument.create(new File(plugin.getDataFolder(), path));
                return;
            }
            yamlDocument = YamlDocument.create(new File(plugin.getDataFolder(), path),
                    GeneralSettings.DEFAULT,
                    loaderSettings.build(),
                    DumperSettings.DEFAULT,
                    updaterSettings.build());
            return;
        }
        if (!update) {
            yamlDocument = YamlDocument.create(new File(plugin.getDataFolder(), path), resource);
            return;
        }
        yamlDocument = YamlDocument.create(new File(plugin.getDataFolder(), path), resource,
                GeneralSettings.DEFAULT,
                loaderSettings.build(),
                DumperSettings.DEFAULT,
                updaterSettings.build());
    }

}
