package iskallia.vault.entity.renderer.elite;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.world.entity.monster.EnderMan;

public class EliteEnderEyesLayer extends EyesLayer<EnderMan, EndermanModel<EnderMan>> {
   private static final RenderType ENDERMAN_EYES = RenderType.eyes(VaultMod.id("textures/entity/elite/enderman_eyes.png"));

   public EliteEnderEyesLayer(RenderLayerParent<EnderMan, EndermanModel<EnderMan>> parent) {
      super(parent);
   }

   @Nonnull
   public RenderType renderType() {
      return ENDERMAN_EYES;
   }
}
