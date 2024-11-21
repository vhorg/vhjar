package iskallia.vault.entity.renderer.bloodhorde;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodhorde.Tier3BloodHordeEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodhorde.Tier3BloodHordeModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class Tier3BloodHordeRenderer extends HumanoidMobRenderer<Tier3BloodHordeEntity, Tier3BloodHordeModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodhorde/t3.png");

   public Tier3BloodHordeRenderer(Context context) {
      super(context, new Tier3BloodHordeModel(context.bakeLayer(ModModelLayers.T3_BLOOD_HORDE)), 0.5F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull Tier3BloodHordeEntity entity) {
      return TEXTURE_LOCATION;
   }
}
