package cz.gennario.newrotatingheads.rotatingengine.holograms.providers;

import cz.gennario.newrotatingheads.system.RotatingHead;
import cz.gennario.newrotatingheads.rotatingengine.PacketArmorStand;
import cz.gennario.newrotatingheads.rotatingengine.holograms.HologramExtender;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class PrivateHologramProvider extends HologramExtender {

    private Map<Integer, PacketArmorStand> lines;
    private CopyOnWriteArrayList<Player> players;
    private double space;
    private boolean attachBottom;

    public PrivateHologramProvider(String name, RotatingHead rotatingHead) {
        super(name, rotatingHead);
        lines = new HashMap<>();
        players = new CopyOnWriteArrayList<>();
    }

    @Override
    public void createHologram(double space, Location location, boolean attachBottom, List<String> lines) {
        this.space = space;
        this.attachBottom = attachBottom;

        location = location.clone();

        location.setY(location.getY()+(lines.size()*space));

        int lineNumber = 0;
        for (String line : lines) {
            PacketArmorStand packetArmorStand = new PacketArmorStand()
                    .setName(line)
                    .setShowName(true)
                    .setHasNoGravity(true)
                    .setInvisible(true)
                    .setLocation(location.clone())
                    .setSmall(true);

            this.lines.put(lineNumber, packetArmorStand);

            location.setY(location.getY()-space);
            lineNumber++;
        }
    }

    @Override
    public void updateLine(int line, String newLine) {
        PacketArmorStand packetArmorStand = new ArrayList<>(lines.values()).get(line);
        packetArmorStand.setName(newLine);
        for (Player player : players) {
            packetArmorStand.spawn(player);
        }
    }

    @Override
    public void moveHologram(Location location) {
        if(!attachBottom) {
            for (int i = 0; i < lines.values().size(); i++) {
                location.add(0, space, 0);
            }
        }

        for (PacketArmorStand line : new ArrayList<>(lines.values())) {
            for (Player player : players) {
                if(player == null) continue;
                if(!player.isOnline()) continue;
                line.teleport(player, location.clone());
            }

            location.add(0, -space, 0);
        }
    }

    @Override
    public void deleteHologram() {
        for (Player player : new ArrayList<>(players)) {
            despawn(player);
        }
        lines.clear();
    }

    @Override
    public void spawn(Player player) {
        Map<Integer, PacketArmorStand> tempLines = new HashMap<>(lines);
        for (PacketArmorStand packetArmorStand : new ArrayList<>(tempLines.values())) {
            if(!packetArmorStand.getName().isEmpty()) {
                packetArmorStand.spawn(player);
            }
        }

        players.add(player);
    }

    @Override
    public void despawn(Player player) {
        Map<Integer, PacketArmorStand> tempLines = new HashMap<>(lines);
        for (PacketArmorStand packetArmorStand : new ArrayList<>(tempLines.values())) {
            packetArmorStand.delete(player);
        }

        players.remove(player);
    }

    @Override
    public void refreshLines(Player player) {
        Map<Integer, PacketArmorStand> tempLines = new HashMap<>(lines);
        for (PacketArmorStand packetArmorStand : new ArrayList<>(tempLines.values())) {
            packetArmorStand.updateName(player);
        }
    }
}
