package iskallia.vault.entity.renderer.plastic;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.plastic.PlasticZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.plastic.PlasticZombieModel;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class PlasticZombieRenderer extends HumanoidMobRenderer<PlasticZombieEntity, PlasticZombieModel> {
   public static final Map<Integer, ResourceLocation> TEXTURES = Map.of(
      1,
      VaultMod.id("textures/entity/plastic/zombie/zombie_plastic_t1.png"),
      2,
      VaultMod.id("textures/entity/plastic/zombie/zombie_plastic_t2.png"),
      3,
      VaultMod.id("textures/entity/plastic/zombie/zombie_plastic_t3.png"),
      4,
      VaultMod.id("textures/entity/plastic/zombie/zombie_plastic_t4.png")
   );

   public PlasticZombieRenderer(Context context) {
      super(context, new PlasticZombieModel(context.bakeLayer(ModModelLayers.PLASTIC_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull PlasticZombieEntity zombie) {
      return TEXTURES.get(zombie.getTier());
   }
}
