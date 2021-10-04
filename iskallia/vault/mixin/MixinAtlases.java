package iskallia.vault.mixin;

import iskallia.vault.block.render.ScavengerChestRenderer;
import iskallia.vault.block.render.VaultChestRenderer;
import java.util.function.Consumer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.model.RenderMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Atlases.class})
public class MixinAtlases {
   @Inject(
      method = {"collectAllMaterials"},
      at = {@At("RETURN")}
   )
   private static void collectAllMaterials(Consumer<RenderMaterial> materialConsumer, CallbackInfo ci) {
      materialConsumer.accept(VaultChestRenderer.NORMAL);
      materialConsumer.accept(VaultChestRenderer.TREASURE);
      materialConsumer.accept(VaultChestRenderer.ALTAR);
      materialConsumer.accept(VaultChestRenderer.COOP);
      materialConsumer.accept(VaultChestRenderer.BONUS);
      materialConsumer.accept(ScavengerChestRenderer.MATERIAL);
   }
}
