package iskallia.vault.mixin;

import java.util.List;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({AnimationMetadataSection.class})
public interface AccessorAnimationMetadataSection {
   @Accessor("frames")
   List<AnimationFrame> getFrames();

   @Accessor("frameWidth")
   int getFrameWidth();

   @Accessor("frameHeight")
   int getFrameHeight();

   @Accessor("defaultFrameTime")
   int getDefaultFrameTime();

   @Accessor("interpolatedFrames")
   boolean getInterpolatedFrames();
}
