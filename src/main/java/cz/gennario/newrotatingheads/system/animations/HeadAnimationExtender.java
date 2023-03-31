package cz.gennario.newrotatingheads.system.animations;

import cz.gennario.newrotatingheads.rotatingengine.PacketUtils;
import cz.gennario.newrotatingheads.system.RotatingHead;
import lombok.Getter;
import org.bukkit.Location;

@Getter
public abstract class HeadAnimationExtender {

    private final String name;
    private final int id;

    public HeadAnimationExtender(String name) {
        this.name = name;
        this.id = PacketUtils.generateRandomEntityId();
    }

    public abstract Location pingLocation(RotatingHead rotatingHead);

    public void rotate(RotatingHead rotatingHead) {
        rotatingHead.setLastLocation(pingLocation(rotatingHead));
    }

}
