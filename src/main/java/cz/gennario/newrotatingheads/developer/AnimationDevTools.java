package cz.gennario.newrotatingheads.developer;

import cz.gennario.newrotatingheads.Main;
import cz.gennario.newrotatingheads.system.animations.AnimationData;
import cz.gennario.newrotatingheads.system.animations.HeadAnimationExtender;

public class AnimationDevTools {

    private final Main instance;

    public AnimationDevTools() {
        this.instance = Main.getInstance();
    }

    /**
     * It registers an animation
     *
     * @param headAnimationExtender The HeadAnimationExtender instance that you created in the previous step.
     * @param animationData The animation data to register.
     */
    public void registerAnimation(HeadAnimationExtender headAnimationExtender, AnimationData animationData) {
        instance.getAnimationLoader().animations.put(headAnimationExtender.getName(), animationData);
    }

}
