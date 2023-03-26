package cz.gennario.newrotatingheads.system.animations;

import dev.dejvokep.boostedyaml.block.implementation.Section;
public abstract class AnimationData {

    public abstract HeadAnimationExtender getExtender(Section section);

}
