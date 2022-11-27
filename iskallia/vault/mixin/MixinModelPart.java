package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.util.IScalablePart;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelPart.class})
public class MixinModelPart implements IScalablePart {
   private Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);

   @Override
   public Vector3f getScale() {
      return this.scale;
   }

   @Override
   public void setScale(Vector3f scale) {
      this.scale = scale;
   }

   @Inject(
      method = {"translateAndRotate"},
      at = {@At("TAIL")}
   )
   public void scale(PoseStack pPoseStack, CallbackInfo ci) {
      pPoseStack.scale(this.scale.x(), this.scale.y(), this.scale.z());
   }
}
