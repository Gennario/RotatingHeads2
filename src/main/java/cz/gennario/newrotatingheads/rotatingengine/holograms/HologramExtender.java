package cz.gennario.newrotatingheads.rotatingengine.holograms;

import cz.gennario.newrotatingheads.system.RotatingHead;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
public abstract class HologramExtender {

    private final String name;
    private final RotatingHead rotatingHead;

    public HologramExtender(String name, RotatingHead rotatingHead) {
        this.name = name;
        this.rotatingHead = rotatingHead;
    }

    public abstract void createHologram(double space, Location location, boolean attachBottom, List<String> lines);
    public abstract void updateLine(int line, String newLine);
    public abstract void moveHologram(Location location);
    public abstract void deleteHologram();
    public abstract void spawn(Player player);
    public abstract void despawn(Player player);
    public abstract void refreshLines(Player player);
}
