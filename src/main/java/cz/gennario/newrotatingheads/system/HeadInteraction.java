package cz.gennario.newrotatingheads.system;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import cz.gennario.newrotatingheads.Main;
import cz.gennario.newrotatingheads.developer.events.HeadInteractEvent;
import cz.gennario.newrotatingheads.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HeadInteraction {

    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public enum HeadClickType {
        LEFT,
        RIGHT,
        SHIFT_LEFT,
        SHIFT_RIGHT
    }

    public void register() {
        protocolManager.addPacketListener(new PacketAdapter(Main.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                Player p = e.getPlayer();
                PacketContainer packet = e.getPacket();
                int id = packet.getIntegers().read(0);

                EnumWrappers.EntityUseAction action;
                if (Utils.versionIsAfter(16)) {
                    action = packet.getEnumEntityUseActions().readSafely(0).getAction();
                } else {
                    action = packet.getEntityUseActions().readSafely(0);
                }
                boolean isShift = packet.getBooleans().readSafely(0); // Check if player is crouching

                HeadClickType headClickType = null;

                switch (action.compareTo(EnumWrappers.EntityUseAction.INTERACT)) {
                    case 1: // LEFT CLICK
                        if (isShift) headClickType = HeadClickType.SHIFT_LEFT;
                        else headClickType = HeadClickType.LEFT;
                        break;
                    case 2:
                        if (isShift) headClickType = HeadClickType.SHIFT_RIGHT;
                        else headClickType = HeadClickType.RIGHT;
                        break;
                    default:
                        return;
                }

                HeadInteractEvent event = new HeadInteractEvent(id, p, headClickType, isShift);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(event);
                    }
                }.runTask(Main.getInstance());
                if (event.isCancelled()) {
                    return;
                }

                if (e.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                    for (RotatingHead head : Main.getInstance().getHeads().values()) {
                        if (head.getId() == id) {
                            head.checkActions(p, headClickType);
                            return;
                        }
                    }
                }
            }
        });
    }

}
