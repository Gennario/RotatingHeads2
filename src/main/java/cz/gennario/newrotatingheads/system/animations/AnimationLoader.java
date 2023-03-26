package cz.gennario.newrotatingheads.system.animations;

import cz.gennario.newrotatingheads.system.animations.list.RotateAnimation;
import cz.gennario.newrotatingheads.system.animations.list.UpDownAnimation;
import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.HashMap;
import java.util.Map;

public class AnimationLoader {

    public Map<String, AnimationData> animations;

    public AnimationLoader() {
        animations = new HashMap<>();
    }

    public void addAnimation(String name, AnimationData animationData) {
        animations.put(name, animationData);
    }

    public void loadDefaults() {
        animations.put("rotate", new AnimationData() {
            @Override
            public HeadAnimationExtender getExtender(Section section) {
                RotateAnimation.RotateDirection rotateDirection = RotateAnimation.RotateDirection.valueOf(section.getString("direction", "LEFT").toUpperCase());
                double speed = section.getDouble("speed", 1.0);

                return new RotateAnimation("rotate", rotateDirection, speed);
            }
        });
        animations.put("updown", new AnimationData() {
            @Override
            public HeadAnimationExtender getExtender(Section section) {
                double jump = section.getDouble("jump", 0.01);
                int jumps = section.getInt("jumps", 20);
                int cooldown = section.getInt("cooldown", 0);
                boolean smooth = section.getBoolean("smooth", true);

                return new UpDownAnimation("updown", jump, jumps, cooldown, smooth);
            }
        });
    }

}
