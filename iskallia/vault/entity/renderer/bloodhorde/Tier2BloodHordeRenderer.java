package iskallia.vault.entity.renderer.bloodhorde;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodhorde.Tier2BloodHordeEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodhorde.Tier2BloodHordeModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class Tier2BloodHordeRenderer extends HumanoidMobRenderer<Tier2BloodHordeEntity, Tier2BloodHordeModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodhorde/t2.png");

   public Tier2BloodHordeRenderer(Context context) {
      super(context, new Tier2BloodHordeModel(context.bakeLayer(ModModelLayers.T2_BLOOD_HORDE)), 0.5F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull Tier2BloodHordeEntity entity) {
      return TEXTURE_LOCATION;
   }
}
