package cz.gennario.newrotatingheads.heads;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import cz.gennario.newrotatingheads.Main;
import cz.gennario.newrotatingheads.PacketArmorStand;
import cz.gennario.newrotatingheads.PacketUtils;
import cz.gennario.newrotatingheads.heads.animations.HeadAnimationExtender;
import cz.gennario.newrotatingheads.utils.config.Config;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Getter
public class RotatingHead {

    private final int id;
    private final String name;
    private YamlDocument yamlDocument;

    public enum HeadStatus {
        ENABLED,
        DISABLED
    }
    private HeadStatus headStatus;

    private int viewDistance;
    private String viewPermission;
    private Location location, lastlocation;
    private String headType;
    private ItemStack head;
    private List<HeadAnimationExtender> animations;


    private PacketArmorStand packetArmorStand;
    private List<Player> players;


    public RotatingHead(Location location, String name, boolean withConfig) {
        this.packetArmorStand = new PacketArmorStand();
        this.id = this.packetArmorStand.getEntityId();
        this.name = name;
        this.headStatus = HeadStatus.ENABLED;

        if(withConfig) {
            Config config = new Config(Main.getInstance(), "heads", name, false);
            try {
                config.load();
            } catch (IOException e) {
                Main.getInstance().getLog().log(Level.WARNING, "Error catched while loading rotatinghead named: " + name + "!");
                throw new RuntimeException(e);
            }
            this.yamlDocument = config.getYamlDocument();
        }

        this.location = location.clone();
        this.lastlocation = location.clone();
        this.packetArmorStand.setLocation(lastlocation);
        this.animations = new ArrayList<>();
        this.players = new ArrayList<>();

        // DEFAULT HEAD SETTINGS
        viewDistance = 30;
        head = new ItemStack(Material.STONE);
    }

    public void updateHead() {
        packetArmorStand.setInvisible(true);
        packetArmorStand.setShowName(false);
        packetArmorStand.setSilent(true);
        packetArmorStand.setArms(true);

        packetArmorStand.setHasNoGravity(true);
        packetArmorStand.setGlowing(true);

        packetArmorStand.addEquipment(new Pair<>(EnumWrappers.ItemSlot.HEAD, head));

        players.forEach(player -> packetArmorStand.spawn(player));
    }

    public void spawn(Player player) {
        if (!players.contains(player)) {
            packetArmorStand.spawn(player);
            players.add(player);
        }
    }

    public void despawn(Player player) {
        if (players.contains(player)) {
            packetArmorStand.delete(player);
            players.remove(player);
        }
    }

    public void delete(Player player) {
        players.remove(player);
    }

    public void pingAnimations() {
        for (HeadAnimationExtender animation : animations) {
            animation.rotate(this);
        }
        for (Player player : players) {
            packetArmorStand.teleport(player, lastlocation);
        }
    }

    public RotatingHead setHeadStatus(HeadStatus headStatus) {
        this.headStatus = headStatus;
        if(headStatus.equals(HeadStatus.DISABLED)) {
            if(0 < players.size()) {
                players.forEach(this::despawn);
            }
        }
        return this;
    }

    public RotatingHead setLocation(Location location) {
        this.location = location;
        return this;
    }

    public RotatingHead setLastLocation(Location location) {
        this.lastlocation = location;
        return this;
    }

    public RotatingHead setHead(ItemStack head) {
        this.head = head;
        return this;
    }

    public RotatingHead addAnimation(HeadAnimationExtender animation) {
        this.animations.add(animation);
        return this;
    }

    public RotatingHead setAnimations(List<HeadAnimationExtender> animations) {
        this.animations = animations;
        return this;
    }

    public RotatingHead setHeadType(String headType) {
        this.headType = headType;
        return this;
    }

    public RotatingHead setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
        return this;
    }

    public RotatingHead setViewPermission(String viewPermission) {
        this.viewPermission = viewPermission;
        return this;
    }

    public RotatingHead setPacketArmorStand(PacketArmorStand packetArmorStand) {
        this.packetArmorStand = packetArmorStand;
        return this;
    }

    public RotatingHead setPlayers(List<Player> players) {
        this.players = players;
        return this;
    }

    public RotatingHead setYamlDocument(YamlDocument yamlDocument) {
        this.yamlDocument = yamlDocument;
        return this;
    }
}