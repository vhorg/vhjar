package iskallia.vault.core.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.event.Event;

public class RenderLevelLastEvent extends Event<RenderLevelLastEvent, RenderLevelLastEvent.Data> {
   public RenderLevelLastEvent() {
   }

   protected RenderLevelLastEvent(RenderLevelLastEvent parent) {
      super(parent);
   }

   public RenderLevelLastEvent createChild() {
      return new RenderLevelLastEvent(this);
   }

   public RenderLevelLastEvent.Data invoke(float pTicks, long nanoTime, PoseStack poseStack) {
      return this.invoke(new RenderLevelLastEvent.Data(pTicks, nanoTime, poseStack));
   }

   public record Data(float partialTicks, long nanoTime, PoseStack poseStack) {
   }
}
