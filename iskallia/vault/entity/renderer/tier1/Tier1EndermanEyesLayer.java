package iskallia.vault.entity.renderer.tier1;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.world.entity.LivingEntity;

public class Tier1EndermanEyesLayer<T extends LivingEntity> extends EyesLayer<T, EndermanModel<T>> {
   private static final RenderType ENDERMAN_EYES = RenderType.eyes(VaultMod.id("textures/entity/tier1/enderman_eyes.png"));

   public Tier1EndermanEyesLayer(RenderLayerParent<T, EndermanModel<T>> p_116981_) {
      super(p_116981_);
   }

   @Nonnull
   public RenderType renderType() {
      return ENDERMAN_EYES;
   }
}
