package iskallia.vault.entity.renderer.elite;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.elite.RaisedZombieChickenModel;
import java.util.List;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Chicken;

public class RaisedZombieChickenRenderer extends MobRenderer<Chicken, RaisedZombieChickenModel> {
   private static final List<ResourceLocation> TEXTURE_LOCATIONS = List.of(
      VaultMod.id("textures/entity/elite/raised_zombie_chicken_0.png"),
      VaultMod.id("textures/entity/elite/raised_zombie_chicken_1.png"),
      VaultMod.id("textures/entity/elite/raised_zombie_chicken_2.png"),
      VaultMod.id("textures/entity/elite/raised_zombie_chicken_3.png"),
      VaultMod.id("textures/entity/elite/raised_zombie_chicken_4.png"),
      VaultMod.id("textures/entity/elite/raised_zombie_chicken_5.png"),
      VaultMod.id("textures/entity/elite/raised_zombie_chicken_6.png")
   );

   public RaisedZombieChickenRenderer(Context context) {
      super(context, new RaisedZombieChickenModel(context.bakeLayer(ModModelLayers.RAISED_ZOMBIE_CHICKEN)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Chicken chicken) {
      long most = chicken.getUUID().getMostSignificantBits();
      int choice = Math.abs((int)(most % TEXTURE_LOCATIONS.size()));
      return TEXTURE_LOCATIONS.get(choice);
   }

   protected float getBob(Chicken pLivingBase, float pPartialTicks) {
      float f = Mth.lerp(pPartialTicks, pLivingBase.oFlap, pLivingBase.flap);
      float f1 = Mth.lerp(pPartialTicks, pLivingBase.oFlapSpeed, pLivingBase.flapSpeed);
      return (Mth.sin(f) + 1.0F) * f1;
   }
}
