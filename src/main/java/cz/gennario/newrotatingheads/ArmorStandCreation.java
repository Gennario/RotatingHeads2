package cz.gennario.newrotatingheads;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.*;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ArmorStandCreation {

    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public void spawnArmorStand(Location location, Player player, int entityid) {

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        // Entity ID
        packet.getIntegers().write(0, entityid);
        // Entity Type
        packet.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        // Set optional velocity (/8000)
        packet.getIntegers().write(1, 0);
        packet.getIntegers().write(2, 0);
        packet.getIntegers().write(3, 0);
        // Set location
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        // Set UUID
        packet.getUUIDs().write(0, UUID.randomUUID());

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setMetadata(Player player, int id) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, id);
        WrappedDataWatcher watcher = new WrappedDataWatcher();

        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);

        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        if(MinecraftVersion.getCurrentVersion().isAtLeast(new MinecraftVersion("1.19.3"))) {
            final List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
            watcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
                final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
            });
            packet.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        }else {
            packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        }

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendDataPacket(Player player, int entityId, String text) {
        PacketType type = PacketType.Play.Server.ENTITY_METADATA;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(type);
        WrappedDataWatcher watcher = new WrappedDataWatcher();

        packet.getIntegers().write(0, entityId);

        Byte flags = 0x20;
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) flags);
        Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage(text.replace("&", "§"))[0].getHandle());
        //watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2,WrappedDataWatcher.Registry.getChatComponentSerializer(true)), opt);
        Boolean nameVisible = true;
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
        Byte armorStandTypeFlags = 0x10;
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x01 | 0x08 | 0x10));

        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEquipment(int entityId, Player player, Pair<EnumWrappers.ItemSlot, ItemStack>... items) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, entityId);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = Arrays.asList(items);
        packet.getSlotStackPairLists().write(0, list);

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void teleport(Location location, int entityID, Player player) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers()
                .write(0, entityID);
        packet.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());
        packet.getBytes()
                .write(0, (byte) location.getYaw());
        packet.getBooleans()
                .write(0, false);
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register() {
        protocolManager.addPacketListener(new PacketAdapter(Main.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                Player p = e.getPlayer();
                PacketContainer packet = e.getPacket();
                int id = packet.getIntegers().read(0);

                p.sendMessage("cs");
            }
        });
    }

}
