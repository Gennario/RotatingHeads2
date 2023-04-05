package cz.gennario.newrotatingheads.rotatingengine;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import cz.gennario.newrotatingheads.utils.Utils;
import cz.gennario.newrotatingheads.utils.items.ItemSystem;
import cz.gennario.newrotatingheads.utils.replacement.Replacement;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public class PacketArmorStand {

    private final int entityId;

    private boolean invisible, small, arms, showName, noBaseplate, hasNoGravity, isSilent, glowing;
    private String name;
    private Location coreLocation;
    private Location location;

    private EulerAngle headRotation, bodyRotation, leftArmRotation, rightArmRotation, leftLegRotation, rightLegRotation;

    private List<Pair<EnumWrappers.ItemSlot, HeadEquipmentValue>> equipment;

    public PacketArmorStand() {
        this.entityId = PacketUtils.generateRandomEntityId();

        this.invisible = false;
        this.small = false;
        this.arms = true;
        this.showName = false;
        this.noBaseplate = true;
        this.hasNoGravity = false;
        this.isSilent = false;

        this.name = "";

        this.equipment = new ArrayList<>();
    }


    public void spawn(Player player) {
        try {
            /* SPAWN */
            PacketContainer packet = PacketUtils.spawnEntityPacket(EntityType.ARMOR_STAND, location, entityId);
            PacketUtils.sendPacket(player, packet);

            /* DATA WATCHER - metadata set */
            WrappedDataWatcher dataWatcher = PacketUtils.getDataWatcher();

            byte flags = 0;
            if (isSmall()) {
                flags += (byte) 0x01;
            }
            if (isArms()) {
                flags += (byte) 0x04;
            }
            if (isNoBaseplate()) {
                flags += (byte) 0x08;
            }
            if (Utils.versionIsAfter(16)) {
                PacketUtils.setMetadata(dataWatcher, 15, Byte.class, flags);
            }else if(Utils.versionIsAfterOrEqual(15)) {
                PacketUtils.setMetadata(dataWatcher, 14, Byte.class, flags);
            } else {
                PacketUtils.setMetadata(dataWatcher, 11, Byte.class, flags);
            }

            byte flags1 = 0;
            if (isInvisible()) {
                flags1 += (byte) 0x20;
            }
            if (isGlowing()) {
                flags1 += (byte) 0x40;
            }

            PacketUtils.setMetadata(dataWatcher, 0, Byte.class, flags1);

            if (headRotation != null) {
                int id = 16;
                if (Utils.versionIsBeforeOrEqual(16)) id = id - 1;
                if (Utils.versionIsBeforeOrEqual(14)) id = 12;
                PacketUtils.setMetadata(dataWatcher, id, Vector3F.getMinecraftClass(), new Vector3F((float) Math.toDegrees(headRotation.getX()), (float) Math.toDegrees(headRotation.getY()), (float) Math.toDegrees(headRotation.getZ())));
            }
            if (bodyRotation != null) {
                int id = 17;
                if (Utils.versionIsBeforeOrEqual(16)) id = id - 1;
                if (Utils.versionIsBeforeOrEqual(14)) id = 13;
                PacketUtils.setMetadata(dataWatcher, id, Vector3F.getMinecraftClass(), new Vector3F((float) Math.toDegrees(bodyRotation.getX()), (float) Math.toDegrees(bodyRotation.getY()), (float) Math.toDegrees(bodyRotation.getZ())));
            }
            if (leftArmRotation != null) {
                int id = 18;
                if (Utils.versionIsBeforeOrEqual(16)) id = id - 1;
                if (Utils.versionIsBeforeOrEqual(14)) id = 14;
                PacketUtils.setMetadata(dataWatcher, id, Vector3F.getMinecraftClass(), new Vector3F((float) Math.toDegrees(leftArmRotation.getX()), (float) Math.toDegrees(leftArmRotation.getY()), (float) Math.toDegrees(leftArmRotation.getZ())));
            }
            if (rightArmRotation != null) {
                int id = 19;
                if (Utils.versionIsBeforeOrEqual(16)) id = id - 1;
                if (Utils.versionIsBeforeOrEqual(14)) id = 15;
                PacketUtils.setMetadata(dataWatcher, id, Vector3F.getMinecraftClass(), new Vector3F((float) Math.toDegrees(rightArmRotation.getX()), (float) Math.toDegrees(rightArmRotation.getY()), (float) Math.toDegrees(rightArmRotation.getZ())));
            }
            if (leftArmRotation != null) {
                int id = 20;
                if (Utils.versionIsBeforeOrEqual(16)) id = id - 1;
                if (Utils.versionIsBeforeOrEqual(14)) id = 16;
                PacketUtils.setMetadata(dataWatcher, id, Vector3F.getMinecraftClass(), new Vector3F((float) Math.toDegrees(leftLegRotation.getX()), (float) Math.toDegrees(leftLegRotation.getY()), (float) Math.toDegrees(leftLegRotation.getZ())));
            }
            if (rightLegRotation != null) {
                int id = 21;
                if (Utils.versionIsBeforeOrEqual(16)) id = id - 1;
                if (Utils.versionIsBeforeOrEqual(14)) id = 17;
                PacketUtils.setMetadata(dataWatcher, id, Vector3F.getMinecraftClass(), new Vector3F((float) Math.toDegrees(rightLegRotation.getX()), (float) Math.toDegrees(rightLegRotation.getY()), (float) Math.toDegrees(rightLegRotation.getZ())));
            }

            if (name != null) {
                Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage(Utils.colorize(player, this.name))[0].getHandle());
                try {
                    PacketUtils.setMetadata(dataWatcher, 2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), opt);
                } catch (Exception e) {
                    PacketUtils.setMetadata(dataWatcher, 2, String.class, this.name);
                }
            }
            if (isShowName()) {
                PacketUtils.setMetadata(dataWatcher, 3, Boolean.class, isShowName());
            }

            if (isHasNoGravity()) {
                PacketUtils.setMetadata(dataWatcher, 5, Boolean.class, isHasNoGravity());
            }
            if (isSilent()) {
                PacketUtils.setMetadata(dataWatcher, 4, Boolean.class, isSilent());
            }

            PacketContainer packet1 = PacketUtils.applyMetadata(entityId, dataWatcher);
            PacketUtils.sendPacket(player, packet1);

            for (Pair<EnumWrappers.ItemSlot, HeadEquipmentValue> itemSlotItemStackPair : equipment) {
                PacketContainer packet2 = PacketUtils.getEquipmentPacket(entityId, new Pair<>(
                        itemSlotItemStackPair.getFirst(),
                        itemSlotItemStackPair.getSecond().convert(player)
                ));
                PacketUtils.sendPacket(player, packet2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Player player) {
        PacketContainer destroyPacket = PacketUtils.destroyEntityPacket(entityId);
        PacketUtils.sendPacket(player, destroyPacket);
    }

    public void teleport(Player player, Location location) {
        this.location = location;

        PacketContainer packet = PacketUtils.teleportEntityPacket(entityId, location);
        PacketUtils.sendPacket(player, packet);
    }


    public PacketArmorStand addEquipment(Pair<EnumWrappers.ItemSlot, HeadEquipmentValue>... equipment) {
        this.equipment.addAll(Arrays.asList(equipment));
        return this;
    }

    public PacketArmorStand addEquipment(Pair<EnumWrappers.ItemSlot, HeadEquipmentValue> equipment) {
        this.equipment.add(equipment);
        return this;
    }

    public PacketArmorStand setEquipment(List<Pair<EnumWrappers.ItemSlot, HeadEquipmentValue>> equipment) {
        this.equipment = equipment;
        return this;
    }

    public PacketArmorStand setInvisible(boolean invisible) {
        this.invisible = invisible;
        return this;
    }

    public PacketArmorStand setSmall(boolean small) {
        this.small = small;
        return this;
    }

    public PacketArmorStand setArms(boolean arms) {
        this.arms = arms;
        return this;
    }

    public PacketArmorStand setShowName(boolean showName) {
        this.showName = showName;
        return this;
    }

    public PacketArmorStand setNoBaseplate(boolean noBaseplate) {
        this.noBaseplate = noBaseplate;
        return this;
    }

    public PacketArmorStand setName(String name) {
        this.name = name;
        return this;
    }

    public PacketArmorStand setHasNoGravity(boolean hasNoGravity) {
        this.hasNoGravity = hasNoGravity;
        return this;
    }

    public PacketArmorStand setSilent(boolean silent) {
        isSilent = silent;
        return this;
    }

    public PacketArmorStand setLocation(Location location) {
        this.location = location;
        this.coreLocation = location;
        return this;
    }

    public PacketArmorStand setCoreLocation(Location coreLocation) {
        this.coreLocation = coreLocation;
        return this;
    }

    public PacketArmorStand setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public PacketArmorStand setHeadRotation(EulerAngle headRotation) {
        this.headRotation = headRotation;
        return this;
    }

    public PacketArmorStand setBodyRotation(EulerAngle bodyRotation) {
        this.bodyRotation = bodyRotation;
        return this;
    }

    public PacketArmorStand setLeftArmRotation(EulerAngle leftArmRotation) {
        this.leftArmRotation = leftArmRotation;
        return this;
    }

    public PacketArmorStand setLeftLegRotation(EulerAngle leftLegRotation) {
        this.leftLegRotation = leftLegRotation;
        return this;
    }

    public PacketArmorStand setRightArmRotation(EulerAngle rightArmRotation) {
        this.rightArmRotation = rightArmRotation;
        return this;
    }

    public PacketArmorStand setRightLegRotation(EulerAngle rightLegRotation) {
        this.rightLegRotation = rightLegRotation;
        return this;
    }
}
