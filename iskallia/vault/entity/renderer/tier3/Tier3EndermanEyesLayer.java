package iskallia.vault.entity.renderer.tier3;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier3.Tier3EndermanEntity;
import iskallia.vault.entity.model.tier3.Tier3EndermanModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;

public class Tier3EndermanEyesLayer extends EyesLayer<Tier3EndermanEntity, Tier3EndermanModel> {
   private static final RenderType ENDERMAN_EYES = RenderType.eyes(VaultMod.id("textures/entity/tier3/enderman_eyes.png"));

   public Tier3EndermanEyesLayer(RenderLayerParent<Tier3EndermanEntity, Tier3EndermanModel> p_116981_) {
      super(p_116981_);
   }

   @Nonnull
   public RenderType renderType() {
      return ENDERMAN_EYES;
   }
}
