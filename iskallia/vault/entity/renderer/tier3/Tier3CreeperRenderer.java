package iskallia.vault.entity.renderer.tier3;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier3.Tier3CreeperModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class Tier3CreeperRenderer extends MobRenderer<Creeper, CreeperModel<Creeper>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier3/creeper.png");

   public Tier3CreeperRenderer(Context context) {
      super(context, new Tier3CreeperModel(context.bakeLayer(ModModelLayers.T3_CREEPER)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Creeper entity) {
      return VaultMod.id("textures/entity/tier3/creeper.png");
   }
}
