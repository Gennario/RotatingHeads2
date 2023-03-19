package cz.gennario.newrotatingheads.heads.animations;

import com.comphenix.protocol.events.PacketContainer;
import cz.gennario.newrotatingheads.PacketUtils;
import cz.gennario.newrotatingheads.heads.RotatingHead;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Location;

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
