package iskallia.vault.entity.renderer.guardian;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.guardian.CrystalGuardianEntity;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class CrystalGuardianRenderer extends HumanoidMobRenderer<CrystalGuardianEntity, PiglinModel<CrystalGuardianEntity>> {
   public static final Map<CrystalGuardianEntity.Color, ResourceLocation> TEXTURES = new HashMap<>();

   public CrystalGuardianRenderer(Context context) {
      super(context, new PiglinModel(context.bakeLayer(ModelLayers.PIGLIN)), 0.6F);
   }

   protected void scale(@Nonnull CrystalGuardianEntity entity, @Nonnull PoseStack matrixStack, float partialTickTime) {
      super.scale(entity, matrixStack, partialTickTime);
      matrixStack.scale(1.1F, 1.1F, 1.1F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull CrystalGuardianEntity entity) {
      CrystalGuardianEntity.Color crystalColor = entity.getCrystalColor();
      return TEXTURES.containsKey(crystalColor) ? TEXTURES.get(crystalColor) : TEXTURES.get(CrystalGuardianEntity.Color.BLUE);
   }

   static {
      TEXTURES.put(CrystalGuardianEntity.Color.BLUE, VaultMod.id("textures/entity/guardian/crystal_blue.png"));
      TEXTURES.put(CrystalGuardianEntity.Color.GREEN, VaultMod.id("textures/entity/guardian/crystal_green.png"));
      TEXTURES.put(CrystalGuardianEntity.Color.ORANGE, VaultMod.id("textures/entity/guardian/crystal_orange.png"));
      TEXTURES.put(CrystalGuardianEntity.Color.VIOLET, VaultMod.id("textures/entity/guardian/crystal_violet.png"));
   }
}
