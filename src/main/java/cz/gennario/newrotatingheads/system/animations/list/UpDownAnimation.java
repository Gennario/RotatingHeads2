package cz.gennario.newrotatingheads.system.animations.list;

import cz.gennario.newrotatingheads.system.RotatingHead;
import cz.gennario.newrotatingheads.system.animations.HeadAnimationExtender;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpDownAnimation extends HeadAnimationExtender {

    private int jumps, jumped, direction;
    private double jump;
    private boolean smooth;
    private int cooldown;
    private List<Double> smoothJumps;

    public UpDownAnimation(String name, double jump, int jumps, int cooldown, boolean smooth) {
        super(name);
        this.jump = jump;
        this.jumps = jumps;
        this.smooth = smooth;
        this.cooldown =  cooldown;
        jumped = 0;
        direction = 0;

        smoothJumps = new ArrayList<>();

        List<Double> doubles = calculateSmoothMovement(jump, jumps/2);
        Collections.reverse(doubles);
        doubles.addAll(calculateSmoothMovement(jump, jumps/2));
        smoothJumps = doubles;
    }

    @Override
    public Location pingLocation(RotatingHead rotatingHead) {

        if(cooldown < 0) {
            cooldown++;
            return rotatingHead.getLastlocation();
        }else {
            cooldown = 0;
        }

        Location location = rotatingHead.getLastlocation().clone();
        if (!smooth) {
            if (direction == 0) {
                if (jumped <= jumps) {
                    location.add(0, jump, 0);
                } else {
                    direction = 1;
                    jumped = 0;
                }
            } else if (direction == 1) {
                if (jumped <= jumps) {
                    location.add(0, -jump, 0);
                } else {
                    direction = 0;
                    jumped = 0;
                }
            }
            jumped++;
        } else {
            if (direction == 0) {
                if(jumped >= smoothJumps.size()) {
                    direction = 1;
                    location.add(0, -smoothJumps.get(jumped-1), 0);
                }else {
                    location.add(0, smoothJumps.get(jumped), 0);
                    jumped++;
                }
            } else if (direction == 1) {
                if(jumped <= 0) {
                    direction = 0;
                    location.add(0, smoothJumps.get(jumped), 0);
                }else {
                    jumped--;
                    location.add(0, -smoothJumps.get(jumped), 0);
                }
            }
        }

        return location;
    }

    public List<Double> calculateSmoothMovement(double jump, int halfJumps) {
        List<Double> jumps = new ArrayList<>();

        double trajectory = halfJumps*jump;
        double normalTrajectory = trajectory-(trajectory/4);
        double normalJump = normalTrajectory/(halfJumps-(halfJumps/2));

        int ii = 1;
        for (int i = 0; i < halfJumps; i++) {
            if((i+1) >= (halfJumps-(halfJumps/2))) {
                jumps.add(normalJump/ii);
                ii++;
            }else {
                jumps.add(normalJump);
            }
        }

        return jumps;
    }

    public int getDirection() {
        return direction;
    }

    public double getJump() {
        return jump;
    }

    public int getJumps() {
        return jumps;
    }

    public int getJumped() {
        return jumped;
    }

    public boolean isSmooth() {
        return smooth;
    }
}
