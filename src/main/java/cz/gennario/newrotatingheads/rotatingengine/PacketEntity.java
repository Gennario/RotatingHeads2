package cz.gennario.newrotatingheads.rotatingengine;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import cz.gennario.newrotatingheads.utils.Utils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.*;

@Getter
public class PacketEntity {

    private final int entityId;
    
    
    private EntityType entityType;
    private boolean crouching, invisible, glowing, elytraFlying, showName, silent, noGravity;
    private String name;
    private Location coreLocation;
    private Location location;

    private EulerAngle headRotation, bodyRotation, leftArmRotation, rightArmRotation, leftLegRotation, rightLegRotation;

    private List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment;

    public PacketEntity() {
        this.entityId = PacketUtils.generateRandomEntityId();

        this.entityType = EntityType.PIG;
        
        this.invisible = false;
        this.crouching = false;
        this.glowing = false;
        this.elytraFlying = false;
        this.showName = false;
        this.noGravity = true;
        this.silent = true;

        this.name = "";

        this.equipment = new ArrayList<>();
    }


    public void spawn(Player player) {
        /* SPAWN */
        PacketContainer packet = PacketUtils.spawnEntityPacket(entityType, location, entityId);
        PacketUtils.sendPacket(player, packet);

        /* DATA WATCHER - metadata set */
        WrappedDataWatcher dataWatcher = PacketUtils.getDataWatcher();

        byte flags = 0;
        if(isCrouching()) {
            flags += (byte) 0x02;
        }
        if(isInvisible()) {
            flags += (byte) 0x20;
        }
        if(isGlowing()) {
            flags += (byte) 0x40;
        }
        if(isElytraFlying()) {
            flags += (byte) 0x80;
        }
        PacketUtils.setMetadata(dataWatcher, 0, Byte.class, (byte) flags);
        
        if(!Objects.equals(this.name, "")) {
            Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage(Utils.colorize(player, this.name))[0].getHandle());
            PacketUtils.setMetadata(dataWatcher, 2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), opt);
        }
        if (isShowName()) {
            PacketUtils.setMetadata(dataWatcher, 3, Boolean.class, isShowName());
        }
        if (isNoGravity()) {
            PacketUtils.setMetadata(dataWatcher, 5, Boolean.class, isNoGravity());
        }
        if (isSilent()) {
            PacketUtils.setMetadata(dataWatcher, 4, Boolean.class, isSilent());
        }

        PacketContainer packet1 = PacketUtils.applyMetadata(entityId, dataWatcher);
        PacketUtils.sendPacket(player, packet1);

        for (Pair<EnumWrappers.ItemSlot, ItemStack> itemSlotItemStackPair : equipment) {
            PacketContainer packet2 = PacketUtils.getEquipmentPacket(entityId, itemSlotItemStackPair);
            PacketUtils.sendPacket(player, packet2);
        }
    }

    public void delete(Player player) {
        PacketContainer destroyPacket = PacketUtils.destroyEntityPacket(entityId);
        PacketUtils.sendPacket(player, destroyPacket);
    }

    public void teleport(Player player, Location location) {
        this.location = location;

        PacketContainer teleportPacket = PacketUtils.teleportEntityPacket(entityId, location);
        PacketContainer headRotatePacket = PacketUtils.getHeadRotatePacket(entityId, location);
        PacketContainer bodyRotatePacket = PacketUtils.getHeadLookPacket(entityId, location);
        PacketUtils.sendPacket(player, teleportPacket);
        PacketUtils.sendPacket(player, bodyRotatePacket);
        PacketUtils.sendPacket(player, headRotatePacket);
    }


    public PacketEntity addEquipment(Pair<EnumWrappers.ItemSlot, ItemStack>... equipment) {
        this.equipment.addAll(Arrays.asList(equipment));
        return this;
    }

    public PacketEntity addEquipment(Pair<EnumWrappers.ItemSlot, ItemStack> equipment) {
        this.equipment.add(equipment);
        return this;
    }

    public PacketEntity setEquipment(List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment) {
        this.equipment = equipment;
        return this;
    }

    public PacketEntity setInvisible(boolean invisible) {
        this.invisible = invisible;
        return this;
    }

    public PacketEntity setShowName(boolean showName) {
        this.showName = showName;
        return this;
    }

    public PacketEntity setName(String name) {
        this.name = name;
        return this;
    }

    public PacketEntity setNoGravity(boolean hasNoGravity) {
        this.noGravity = hasNoGravity;
        return this;
    }

    public PacketEntity setSilent(boolean silent) {
        this.silent = silent;
        return this;
    }

    public PacketEntity setLocation(Location location) {
        this.location = location;
        this.coreLocation = location;
        return this;
    }

    public PacketEntity setCoreLocation(Location coreLocation) {
        this.coreLocation = coreLocation;
        return this;
    }

    public PacketEntity setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public PacketEntity setHeadRotation(EulerAngle headRotation) {
        this.headRotation = headRotation;
        return this;
    }

    public PacketEntity setBodyRotation(EulerAngle bodyRotation) {
        this.bodyRotation = bodyRotation;
        return this;
    }

    public PacketEntity setLeftArmRotation(EulerAngle leftArmRotation) {
        this.leftArmRotation = leftArmRotation;
        return this;
    }

    public PacketEntity setLeftLegRotation(EulerAngle leftLegRotation) {
        this.leftLegRotation = leftLegRotation;
        return this;
    }

    public PacketEntity setRightArmRotation(EulerAngle rightArmRotation) {
        this.rightArmRotation = rightArmRotation;
        return this;
    }

    public PacketEntity setRightLegRotation(EulerAngle rightLegRotation) {
        this.rightLegRotation = rightLegRotation;
        return this;
    }

    public PacketEntity setCrouching(boolean crouching) {
        this.crouching = crouching;
        return this;
    }

    public PacketEntity setElytraFlying(boolean elytraFlying) {
        this.elytraFlying = elytraFlying;
        return this;
    }

    public PacketEntity setEntityType(EntityType entityType) {
        this.entityType = entityType;
        return this;
    }
}
