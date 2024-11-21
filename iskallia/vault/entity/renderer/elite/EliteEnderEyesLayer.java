package iskallia.vault.entity.renderer.elite;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.elite.EliteEndermanEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;

public class EliteEnderEyesLayer extends EyesLayer<EliteEndermanEntity, EndermanModel<EliteEndermanEntity>> {
   private static final RenderType ENDERMAN_EYES = RenderType.eyes(VaultMod.id("textures/entity/elite/enderman_eyes.png"));

   public EliteEnderEyesLayer(RenderLayerParent<EliteEndermanEntity, EndermanModel<EliteEndermanEntity>> parent) {
      super(parent);
   }

   @Nonnull
   public RenderType renderType() {
      return ENDERMAN_EYES;
   }
}
