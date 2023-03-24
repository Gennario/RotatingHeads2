package cz.gennario.newrotatingheads.heads;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.sun.tools.jdi.Packet;
import cz.gennario.newrotatingheads.Main;
import cz.gennario.newrotatingheads.PacketArmorStand;
import cz.gennario.newrotatingheads.PacketUtils;
import cz.gennario.newrotatingheads.heads.animations.AnimationData;
import cz.gennario.newrotatingheads.heads.animations.AnimationLoader;
import cz.gennario.newrotatingheads.heads.animations.HeadAnimationExtender;
import cz.gennario.newrotatingheads.utils.Utils;
import cz.gennario.newrotatingheads.utils.config.Config;
import cz.gennario.newrotatingheads.utils.items.ItemSystem;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
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
    private boolean small, invisible, arms, glowing;
    private Location location, lastlocation;

    public enum HeadType {
        STAND,
        ENTITY,
        NPC
    }

    private HeadType headType;
    private List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment;
    private List<HeadAnimationExtender> animations;

    private EulerAngle headRotation, bodyRotation, leftArmRotation, rightArmRotation, leftLegRotation, rightLegRotation;

    private PacketArmorStand packetArmorStand;
    private List<Player> players;


    public RotatingHead(@Nullable Location location, String name, boolean withConfig) {
        this.packetArmorStand = new PacketArmorStand();
        this.id = this.packetArmorStand.getEntityId();
        this.name = name;
        this.headStatus = HeadStatus.ENABLED;

        if (withConfig) {
            Config config = new Config(Main.getInstance(), "heads", name, false);
            try {
                config.load();
            } catch (IOException e) {
                Main.getInstance().getLog().log(Level.WARNING, "Error catched while loading rotatinghead named: " + name + "!");
                throw new RuntimeException(e);
            }
            this.yamlDocument = config.getYamlDocument();
        }

        if(location != null) {
            this.location = location.clone();
        }else {
            this.location = Utils.getLocation(yamlDocument.getString("settings.location"));
        }
        this.lastlocation = this.location;
        this.packetArmorStand.setLocation(lastlocation);
        this.animations = new ArrayList<>();
        this.players = new ArrayList<>();

        this.arms = false;
        this.invisible = true;
        this.small = false;
        this.glowing = false;

        this.bodyRotation = new EulerAngle(0f, 0f, 0f);
        this.leftArmRotation = new EulerAngle(0f, 0f, 0f);
        this.leftLegRotation = new EulerAngle(0f, 0f, 0f);
        this.rightArmRotation = new EulerAngle(0f, 0f, 0f);
        this.rightLegRotation = new EulerAngle(0f, 0f, 0f);

        // DEFAULT HEAD SETTINGS
        viewDistance = 30;
        equipment = Arrays.asList(new Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemStack(Material.STONE)));
    }

    public void updateHead() {
        packetArmorStand.setInvisible(invisible);
        packetArmorStand.setShowName(false);
        packetArmorStand.setSilent(true);
        packetArmorStand.setArms(arms);
        packetArmorStand.setSmall(small);

        packetArmorStand.setHasNoGravity(true);
        packetArmorStand.setGlowing(glowing);

        packetArmorStand.setEquipment(equipment);

        packetArmorStand.setBodyRotation(bodyRotation);
        packetArmorStand.setLeftArmRotation(leftArmRotation);
        packetArmorStand.setRightArmRotation(rightArmRotation);
        packetArmorStand.setLeftLegRotation(leftLegRotation);
        packetArmorStand.setRightLegRotation(rightLegRotation);

        players.forEach(player -> packetArmorStand.spawn(player));
    }

    public void loadFromConfig() {
        this.viewDistance = yamlDocument.getInt("settings.view-distance", 30);
        this.viewPermission = yamlDocument.getString("settings.view-permission", null);
        this.location = Utils.getLocation(yamlDocument.getString("settings.location"));
        this.headType = HeadType.valueOf(yamlDocument.getString("vtype", "STAND"));

        this.invisible = yamlDocument.getBoolean("settings.invisible", true);
        this.arms = yamlDocument.getBoolean("settings.arms", false);
        this.small = yamlDocument.getBoolean("settings.small", false);
        this.glowing = yamlDocument.getBoolean("settings.glowing", false);

        AnimationLoader animationLoader = Main.getInstance().getAnimationLoader();
        this.animations = new ArrayList<>();
        if(yamlDocument.contains("settings.animations")) {
            for (String animation : yamlDocument.getSection("settings.animations").getRoutesAsStrings(false)) {
                animation = animation.toLowerCase(Locale.ROOT);
                if(animationLoader.animations.containsKey(animation)) {
                    AnimationData animationData = animationLoader.animations.get(animation);
                    HeadAnimationExtender extender = animationData.getExtender(yamlDocument.getSection("settings.animations." + animation));
                    this.animations.add(extender);
                }
            }
        }

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> items = new ArrayList<>();
        if(yamlDocument.contains("equipment")) {
            for (String slot : yamlDocument.getSection("equipment").getRoutesAsStrings(false)) {
                EnumWrappers.ItemSlot itemSlot = EnumWrappers.ItemSlot.valueOf(slot);
                ItemStack itemStack = ItemSystem.itemFromConfig(yamlDocument.getSection("equipment." + slot));

                items.add(new Pair<>(itemSlot, itemStack));
            }
        }
        this.equipment = items;

        List<String> rotationTypes = Arrays.asList("HEAD", "BODY", "LEFT_ARM", "RIGHT_ARM", "LEFT_LEG", "RIGHT_LEG");
        if(yamlDocument.contains("positions")) {
            for (String slot : yamlDocument.getSection("positions").getRoutesAsStrings(false)) {
                if(rotationTypes.contains(slot.toUpperCase(Locale.ROOT))) {
                    switch (slot.toUpperCase(Locale.ROOT)) {
                        case "HEAD":
                            headRotation = getBodyPartsRotation(yamlDocument.getSection("positions."+slot));
                            break;
                        case "BODY":
                            bodyRotation = getBodyPartsRotation(yamlDocument.getSection("positions."+slot));
                            break;
                        case "LEFT_ARM":
                            leftArmRotation = getBodyPartsRotation(yamlDocument.getSection("positions."+slot));
                            break;
                        case "RIGHT_ARM":
                            rightArmRotation = getBodyPartsRotation(yamlDocument.getSection("positions."+slot));
                            break;
                        case "LEFT_LEG":
                            leftLegRotation = getBodyPartsRotation(yamlDocument.getSection("positions."+slot));
                            break;
                        case "RIGHT_LEG":
                            rightLegRotation = getBodyPartsRotation(yamlDocument.getSection("positions."+slot));
                            break;
                    }
                }
            }
        }

    }

    public EulerAngle getBodyPartsRotation(Section section) {
        return new EulerAngle(Math.toRadians(section.getInt("x")),
                Math.toRadians(section.getInt("y")),
                Math.toRadians(section.getInt("z")));
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
        if (headStatus.equals(HeadStatus.DISABLED)) {
            if (0 < players.size()) {
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

    public RotatingHead addAnimation(HeadAnimationExtender animation) {
        this.animations.add(animation);
        return this;
    }

    public RotatingHead setAnimations(List<HeadAnimationExtender> animations) {
        this.animations = animations;
        return this;
    }

    public RotatingHead setHeadType(HeadType headType) {
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

    public RotatingHead setArms(boolean arms) {
        this.arms = arms;
        return this;
    }

    public RotatingHead addEquipment(Pair<EnumWrappers.ItemSlot, ItemStack> equipment) {
        this.equipment.add(equipment);
        return this;
    }

    public RotatingHead setEquipment(List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment) {
        this.equipment = equipment;
        return this;
    }

    public RotatingHead setInvisible(boolean invisible) {
        this.invisible = invisible;
        return this;
    }

    public RotatingHead setLastlocation(Location lastlocation) {
        this.lastlocation = lastlocation;
        return this;
    }

    public RotatingHead setSmall(boolean small) {
        this.small = small;
        return this;
    }

    public RotatingHead setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public RotatingHead setBodyRotation(EulerAngle bodyRotation) {
        this.bodyRotation = bodyRotation;
        return this;
    }

    public RotatingHead setLeftArmRotation(EulerAngle leftArmRotation) {
        this.leftArmRotation = leftArmRotation;
        return this;
    }

    public RotatingHead setLeftLegRotation(EulerAngle leftLegRotation) {
        this.leftLegRotation = leftLegRotation;
        return this;
    }

    public RotatingHead setRightArmRotation(EulerAngle rightArmRotation) {
        this.rightArmRotation = rightArmRotation;
        return this;
    }

    public RotatingHead setRightLegRotation(EulerAngle rightLegRotation) {
        this.rightLegRotation = rightLegRotation;
        return this;
    }

    public RotatingHead setHeadRotation(int x, int y, int z) {
        this.bodyRotation = new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
        return this;
    }

    public RotatingHead setBodyRotation(int x, int y, int z) {
        this.bodyRotation = new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
        return this;
    }

    public RotatingHead setLeftArmRotation(int x, int y, int z) {
        this.leftArmRotation = new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
        return this;
    }

    public RotatingHead setLeftLegRotation(int x, int y, int z) {
        this.leftLegRotation = new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
        return this;
    }

    public RotatingHead setRightArmRotation(int x, int y, int z) {
        this.rightArmRotation = new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
        return this;
    }

    public RotatingHead setRightLegRotation(int x, int y, int z) {
        this.rightLegRotation = new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
        return this;
    }
}