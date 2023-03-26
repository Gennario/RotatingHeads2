package cz.gennario.newrotatingheads.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cz.gennario.newrotatingheads.Main;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;

@UtilityClass
public class BungeeUtils {

    private static boolean initialized = false;

    /**
     * It registers the plugin channel "BungeeCord" to the server
     */
    public static void init() {
        if (initialized) return;
        Messenger messenger = Bukkit.getServer().getMessenger();
        messenger.registerOutgoingPluginChannel(Main.getInstance(), "BungeeCord");
        initialized = true;
    }

    public static void destroy() {
        if (!initialized) return;
        Messenger messenger = Bukkit.getServer().getMessenger();
        messenger.unregisterOutgoingPluginChannel(Main.getInstance(), "BungeeCord");
        initialized = false;
    }

    /**
     * It sends a message to the BungeeCord server, telling it to connect the player to the specified server
     *
     * @param player The player you want to send to the server.
     * @param server The server you want to connect to.
     */
    public static void connect(Player player, String server) {
        if (!initialized) init();
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
        } catch (Exception ignored) {
        }
    }
}
