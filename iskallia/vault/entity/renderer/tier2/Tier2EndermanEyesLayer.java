package iskallia.vault.entity.renderer.tier2;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier2.Tier2EndermanEntity;
import iskallia.vault.entity.model.tier2.Tier2EndermanModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;

public class Tier2EndermanEyesLayer extends EyesLayer<Tier2EndermanEntity, Tier2EndermanModel> {
   private static final RenderType ENDERMAN_EYES = RenderType.eyes(VaultMod.id("textures/entity/tier2/enderman_eyes.png"));

   public Tier2EndermanEyesLayer(RenderLayerParent<Tier2EndermanEntity, Tier2EndermanModel> p_116981_) {
      super(p_116981_);
   }

   @Nonnull
   public RenderType renderType() {
      return ENDERMAN_EYES;
   }
}
