package cz.gennario.newrotatingheads.rotatingengine.holograms;

import cz.gennario.newrotatingheads.system.RotatingHead;
import cz.gennario.newrotatingheads.rotatingengine.holograms.providers.PrivateHologramProvider;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
public class Hologram {

    private final RotatingHead rotatingHead;
    private final PrivateHologramProvider privateHologramProvider;

    private boolean updateLocation;
    private double offsetX, offsetY, offsetZ;

    private boolean updateLines;
    private int updateLinesTime;
    private int currentUpdateLinesTime;

    public Hologram(RotatingHead rotatingHead, PrivateHologramProvider privateHologramProvider) {
        this.rotatingHead = rotatingHead;
        this.privateHologramProvider = privateHologramProvider;
        this.updateLocation = false;
        this.updateLines = false;
        this.currentUpdateLinesTime = 0;
    }

    public void create(double space, double offsetX, double offsetY, double offsetZ, boolean attachBottom, List<String> lines) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        Location add = rotatingHead.getLastlocation().clone().add(offsetX, offsetY, offsetZ);
        privateHologramProvider.createHologram(space, add, attachBottom, lines);
    }

    public void delete() {
        privateHologramProvider.deleteHologram();
    }

    public void spawn(Player player) {
        privateHologramProvider.spawn(player);
    }

    public void deSpawn(Player player) {
        privateHologramProvider.despawn(player);
    }

    public void move() {
        if(updateLocation) {
            Location add = rotatingHead.getLastlocation().clone().add(offsetX, offsetY, offsetZ);
            privateHologramProvider.moveHologram(add.clone());
        }
    }

    public void updateLines(Player player, boolean force) {
        if(force) {
            privateHologramProvider.refreshLines(player);
        }else {
            if(updateLines) {
                if (currentUpdateLinesTime >= updateLinesTime) {

                    privateHologramProvider.refreshLines(player);
                    currentUpdateLinesTime = 0;
                } else {
                    currentUpdateLinesTime++;
                }
            }
        }
    }
}
