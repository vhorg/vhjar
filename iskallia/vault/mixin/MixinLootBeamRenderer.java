package iskallia.vault.mixin;

import com.lootbeams.LootBeamRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LootBeamRenderer.class})
public class MixinLootBeamRenderer {
   @Inject(
      method = {"renderLootBeam"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private static void preventAirLootBeams(PoseStack stack, MultiBufferSource buffer, float pticks, long worldtime, ItemEntity item, CallbackInfo ci) {
      if (item.getItem().isEmpty()) {
         ci.cancel();
      }
   }
}
