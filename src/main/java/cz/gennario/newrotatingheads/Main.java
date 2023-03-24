package cz.gennario.newrotatingheads;

import com.comphenix.protocol.events.PacketContainer;
import cz.gennario.newrotatingheads.heads.HeadRunnable;
import cz.gennario.newrotatingheads.heads.RotatingHead;
import cz.gennario.newrotatingheads.heads.animations.AnimationLoader;
import cz.gennario.newrotatingheads.heads.animations.RotateAnimation;
import cz.gennario.newrotatingheads.heads.animations.UpDownAnimation;
import cz.gennario.newrotatingheads.utils.Utils;
import cz.gennario.newrotatingheads.utils.config.Config;
import cz.gennario.newrotatingheads.utils.debug.Logger;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.graalvm.compiler.hotspot.amd64.PluginFactory_AMD64X87MathIntrinsicNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public final class Main extends JavaPlugin implements Listener {

    public static Main instance;

    private Map<String, RotatingHead> heads;
    private AnimationLoader animationLoader;

    private Logger log;

    private Config configFile;

    @Override
    public void onEnable() {
        instance = this;

        heads = new HashMap<>();
        log = new Logger(Main.getInstance());

        configFile = new Config(this, "", "config", getResource("config.yml"))
                .setUpdate(true);
        try {
            configFile.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        animationLoader = new AnimationLoader();
        animationLoader.loadDefaults();

        File heads = new File(getDataFolder()+"/heads/");
        if(!heads.exists()) {
            heads.mkdir();
            try {
                new Config(this, "heads", "example", getResource("heads/example.yml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (File file : heads.listFiles()) {
            String name = file.getName().replace(".yml", "");

            RotatingHead rotatingHead = new RotatingHead(null, name, true);
            rotatingHead.loadFromConfig();
            rotatingHead.updateHead();

            this.heads.put(name, rotatingHead);
        }

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
        head.addAnimation(new UpDownAnimation("updown", 0.05, 20, 0, true));
       /* {Head:[339f,0f,0f],
            LeftLeg:[20f,19f,0f],
            RightLeg:[20f,326f,0f],LeftArm:[38f,31f,0f],RightArm:[26f,332f,0f]}}*/
        head.setArms(true);
        head.setInvisible(false);
        head.setLeftLegRotation(20, 19, 0)
                .setRightLegRotation(20,326,0)
                .setLeftArmRotation(38, 31, 0)
                .setRightArmRotation(26, 332, 0);

        head.updateHead();

        Location clone = player.getLocation().clone();
        clone.add(10,10,10);

        int eid = PacketUtils.generateRandomEntityId();
        PacketContainer packetContainer = PacketUtils.spawnEntityPacket(EntityType.ALLAY, clone, eid);
        PacketUtils.sendPacket(player, packetContainer);
        new BukkitRunnable() {
            boolean b= true;
            @Override
            public void run() {
                clone.setYaw(clone.getYaw()+3);

                if (b) {
                    clone.add(0, 0.1, 0);
                    b = !b;
                }else {
                    clone.add(0, -0.1, 0);
                    b = !b;
                }

                PacketContainer packetContainer1 = PacketUtils.getHeadLookPacket(eid, clone);
                PacketContainer packetContainer2 = PacketUtils.getHeadRotatePacket(eid, clone);
                PacketUtils.sendPacket(player, packetContainer2);
                PacketUtils.sendPacket(player, packetContainer1);
            }
        }.runTaskTimerAsynchronously(this, 1,1);


        heads.put("sd", head);
    }

    public static Main getInstance() {
        return instance;
    }

    public Logger getLog() {
        return log;
    }
}
