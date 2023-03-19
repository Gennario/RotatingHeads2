package cz.gennario.newrotatingheads;

import cz.gennario.newrotatingheads.heads.HeadRunnable;
import cz.gennario.newrotatingheads.heads.RotatingHead;
import cz.gennario.newrotatingheads.heads.animations.RotateAnimation;
import cz.gennario.newrotatingheads.utils.debug.Logger;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class Main extends JavaPlugin implements Listener {

    public static Main instance;

    private Map<String, RotatingHead> heads;

    private Logger log;

    @Override
    public void onEnable() {
        instance = this;

        heads = new HashMap<>();
        log = new Logger(Main.getInstance());

        getServer().getPluginManager().registerEvents(this, this);
        new ArmorStandCreation().register();

        new HeadRunnable().runTaskTimerAsynchronously(this, 1, 1);
    }

    @Override
    public void onDisable() {
        for (RotatingHead head : heads.values()) {
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        RotatingHead head = new RotatingHead(player.getLocation(), "sd", false);
        head.addAnimation(new RotateAnimation("rotate", RotateAnimation.RotateDirection.LEFT, 0.5));
        head.updateHead();

        heads.put("sd", head);
    }

    public static Main getInstance() {
        return instance;
    }

    public Logger getLog() {
        return log;
    }
}
