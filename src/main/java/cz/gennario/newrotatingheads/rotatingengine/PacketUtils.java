package cz.gennario.newrotatingheads.rotatingengine;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class PacketUtils {

    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public static void sendPacket(Player player, PacketContainer packet) {
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            sendPacket(player, packet);
        }
    }

    public static int generateRandomEntityId() {
        return Integer.parseInt(RandomStringUtils.random(8, false, true));
    }

    public static PacketContainer spawnEntityPacket(EntityType entityType, Location location, int entityId) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        // Entity ID
        packet.getIntegers().write(0, entityId);

        // Entity Type
        packet.getEntityTypeModifier().write(0, entityType);

        // Set optional velocity (/8000)
        packet.getIntegers().write(1, 0);
        packet.getIntegers().write(2, 0);
        packet.getIntegers().write(3, 0);

        // Set location
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());

        packet.getBytes().write(0, (byte) location.getPitch());
        packet.getBytes().write(1, (byte) location.getYaw());

        // Set UUID
        packet.getUUIDs().write(0, UUID.randomUUID());

        return packet;
    }

    public static WrappedDataWatcher getDataWatcher() {
        return new WrappedDataWatcher();
    }

    public static PacketContainer applyMetadata(int entityId, WrappedDataWatcher watcher) {
        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            packet.getIntegers().write(0, entityId);

            if (MinecraftVersion.getCurrentVersion().isAtLeast(new MinecraftVersion("1.19.3"))) {
                final List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
                watcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
                    final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
                    wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
                });
                packet.getDataValueCollectionModifier().write(0, wrappedDataValueList);
            } else {
                packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
            }
            return packet;
        } catch (Exception e) {
            return applyMetadata(entityId, watcher);
        }
    }

    public static WrappedDataWatcher setMetadata(WrappedDataWatcher watcher, int index, Class c, Object value) {
        try {
            watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(index, WrappedDataWatcher.Registry.get(c)), value);
        } catch (Exception e) {
            return setMetadata(watcher, index, c, value);
        }
        return watcher;
    }

    public static WrappedDataWatcher setMetadata(WrappedDataWatcher watcher, int index, WrappedDataWatcher.Serializer serializer, Object value) {
        try {
            watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(index, serializer), value);
        } catch (Exception e) {
            return setMetadata(watcher, index, serializer, value);
        }
        return watcher;
    }

    public static PacketContainer getEquipmentPacket(int entityId, Pair<EnumWrappers.ItemSlot, ItemStack>... items) {
        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

            packet.getIntegers().write(0, entityId);
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = Arrays.asList(items);
            packet.getSlotStackPairLists().write(0, list);
            return packet;
        } catch (Exception e) {
            return getEquipmentPacket(entityId, items);
        }
    }

    public static PacketContainer teleportEntityPacket(int entityID, Location location) {
        try {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);

            packet.getIntegers().write(0, entityID);
            packet.getDoubles().write(0, location.getX())
                    .write(1, location.getY())
                    .write(2, location.getZ());
            packet.getBytes().write(0, (byte) location.getYaw());
            packet.getBooleans().write(0, false);

            return packet;
        } catch (Exception e) {
            return teleportEntityPacket(entityID, location);
        }
    }

    public static PacketContainer destroyEntityPacket(int entityID) {
        try {
            List<Integer> entityIDList = new ArrayList<>();
            entityIDList.add(entityID);
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            try {
                packet.getIntLists().write(0, entityIDList);
            }catch (Exception e) {
                packet.getModifier().write(0, entityIDList);
            }

            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PacketContainer getHeadRotatePacket(int entityId, Location location) {
        try {
            PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            pc.getModifier().writeDefaults();
            pc.getIntegers().write(0, entityId);
            pc.getBytes().write(0, (byte) getCompressedAngle(location.getYaw()));

            return pc;
        } catch (Exception e) {
            return getHeadRotatePacket(entityId, location);
        }
    }

    public static PacketContainer getHeadLookPacket(int entityId, Location location) {
        try {
            PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
            pc.getModifier().writeDefaults();
            pc.getIntegers().write(0, entityId);
            pc.getBytes().write(0, (byte) location.getYaw());
            pc.getBooleans().write(0, false);

            return pc;
        } catch (Exception e) {
            return getHeadLookPacket(entityId, location);
        }
    }

    public static PacketContainer getPassengerPacket(int vehicleId, int passengerCount, int... passengers) {
        try {
            PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.MOUNT);

            pc.getIntegers().write(0, vehicleId);
            pc.getIntegerArrays().write(0, passengers);

            return pc;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getCompressedAngle(float value) {
        return (int) (value * 256.0F / 360.0F);
    }


}
