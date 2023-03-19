package cz.gennario.newrotatingheads;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class PacketArmorStand {

    private final int entityId;

    private boolean invisible, small, arms, showName, noBaseplate, hasNoGravity, isSilent, glowing;
    private String name;
    private Location coreLocation;
    private Location location;

    private List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment;

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
        /* SPAWN */
        PacketContainer packet = PacketUtils.spawnEntityPacket(EntityType.ARMOR_STAND, location, entityId);
        PacketUtils.sendPacket(player, packet);

        /* DATA WATCHER - metadata set */
        WrappedDataWatcher dataWatcher = PacketUtils.getDataWatcher();

        byte flags = 0;
        if(isSmall()) {
            flags += (byte) 0x01;
        }
        if(isArms()) {
            flags += (byte) 0x04;
        }
        if(isNoBaseplate()) {
            flags += (byte) 0x08;
        }
        Bukkit.broadcastMessage(""+flags);
        PacketUtils.setMetadata(dataWatcher, 15, Byte.class, (byte) flags);

        byte flags1 = 0;
        if(isInvisible()) {
            flags1 += (byte) 0x20;
        }
        if(isGlowing()) {
            flags1 += (byte) 0x40;
        }

        PacketUtils.setMetadata(dataWatcher, 0, Byte.class, (byte) flags1);

        if(!Objects.equals(this.name, "")) {
            Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage(this.name.replace("&", "§"))[0].getHandle());
            PacketUtils.setMetadata(dataWatcher, 2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), opt);
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

        for (Pair<EnumWrappers.ItemSlot, ItemStack> itemSlotItemStackPair : equipment) {
            PacketContainer packet2 = PacketUtils.getEquipmentPacket(entityId, itemSlotItemStackPair);
            PacketUtils.sendPacket(player, packet2);
        }
    }

    public void delete(Player player) {
        PacketUtils.destroyEntityPacket(entityId);
    }

    public void teleport(Player player, Location location) {
        this.location = location;

        PacketContainer packet = PacketUtils.teleportEntityPacket(entityId, location);
        PacketUtils.sendPacket(player, packet);
    }


    public PacketArmorStand addEquipment(Pair<EnumWrappers.ItemSlot, ItemStack>... equipment) {
        this.equipment.addAll(Arrays.asList(equipment));
        return this;
    }

    public PacketArmorStand addEquipment(Pair<EnumWrappers.ItemSlot, ItemStack> equipment) {
        this.equipment.add(equipment);
        return this;
    }

    public PacketArmorStand setEquipment(List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment) {
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
}
