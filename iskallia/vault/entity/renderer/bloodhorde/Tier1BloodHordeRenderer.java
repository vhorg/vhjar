package iskallia.vault.entity.renderer.bloodhorde;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodhorde.Tier1BloodHordeEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodhorde.Tier1BloodHordeModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class Tier1BloodHordeRenderer extends HumanoidMobRenderer<Tier1BloodHordeEntity, Tier1BloodHordeModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodhorde/t1.png");

   public Tier1BloodHordeRenderer(Context context) {
      super(context, new Tier1BloodHordeModel(context.bakeLayer(ModModelLayers.T1_BLOOD_HORDE)), 0.5F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull Tier1BloodHordeEntity entity) {
      return TEXTURE_LOCATION;
   }
}
