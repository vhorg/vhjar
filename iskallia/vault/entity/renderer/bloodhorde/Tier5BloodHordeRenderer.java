package iskallia.vault.entity.renderer.bloodhorde;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodhorde.Tier5BloodHordeEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodhorde.Tier5BloodHordeModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class Tier5BloodHordeRenderer extends HumanoidMobRenderer<Tier5BloodHordeEntity, Tier5BloodHordeModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodhorde/t5.png");

   public Tier5BloodHordeRenderer(Context context) {
      super(context, new Tier5BloodHordeModel(context.bakeLayer(ModModelLayers.T5_BLOOD_HORDE)), 0.5F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull Tier5BloodHordeEntity entity) {
      return TEXTURE_LOCATION;
   }
}
