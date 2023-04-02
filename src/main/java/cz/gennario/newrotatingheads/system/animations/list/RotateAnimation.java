package cz.gennario.newrotatingheads.system.animations.list;

import cz.gennario.newrotatingheads.system.RotatingHead;
import cz.gennario.newrotatingheads.system.animations.HeadAnimationExtender;
import org.bukkit.Location;

public class RotateAnimation extends HeadAnimationExtender {

    public enum RotateDirection {
        LEFT,
        RIGHT
    }

    private final RotateDirection rotateDirection;
    private final double speed;

    public RotateAnimation(String name, RotateDirection rotateDirection, double speed) {
        super(name);
        this.rotateDirection = rotateDirection;
        this.speed = speed;
    }

    @Override
    public Location pingLocation(RotatingHead rotatingHead) {
        Location location = rotatingHead.getLastlocation().clone();
        switch (rotateDirection) {
            case LEFT:
                location.setYaw((float) ((location.getYaw()-speed)));
                break;
            case RIGHT:
                location.setYaw((float) ((location.getYaw()+speed)));
                break;
        }
        return location;
    }

}
