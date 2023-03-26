package cz.gennario.newrotatingheads.rotatingengine.holograms;

import cz.gennario.newrotatingheads.system.RotatingHead;
import cz.gennario.newrotatingheads.rotatingengine.holograms.providers.PrivateHologramProvider;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class HologramSystem {

    private final List<Hologram> holograms;

    public HologramSystem() {
        holograms = new ArrayList<>();
    }

    public Hologram createHologram(Location location, RotatingHead rotatingHead) {
        Hologram hologram = new Hologram(rotatingHead, new PrivateHologramProvider("", rotatingHead));

        return hologram;
    }

}
