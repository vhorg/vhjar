package iskallia.vault.entity.renderer.bloodhorde;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodhorde.Tier4BloodHordeEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodhorde.Tier4BloodHordeModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class Tier4BloodHordeRenderer extends HumanoidMobRenderer<Tier4BloodHordeEntity, Tier4BloodHordeModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodhorde/t4.png");

   public Tier4BloodHordeRenderer(Context context) {
      super(context, new Tier4BloodHordeModel(context.bakeLayer(ModModelLayers.T4_BLOOD_HORDE)), 0.5F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull Tier4BloodHordeEntity entity) {
      return TEXTURE_LOCATION;
   }
}
