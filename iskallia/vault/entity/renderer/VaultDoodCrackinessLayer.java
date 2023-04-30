package iskallia.vault.entity.renderer;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.entity.entity.VaultDoodEntity;
import iskallia.vault.entity.model.VaultDoodModel;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem.Crackiness;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VaultDoodCrackinessLayer extends RenderLayer<VaultDoodEntity, VaultDoodModel> {
   private static final Map<Crackiness, ResourceLocation> resourceLocations = ImmutableMap.of(
      Crackiness.LOW,
      new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
      Crackiness.MEDIUM,
      new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
      Crackiness.HIGH,
      new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png")
   );

   public VaultDoodCrackinessLayer(RenderLayerParent<VaultDoodEntity, VaultDoodModel> p_117135_) {
      super(p_117135_);
   }

   public void render(
      PoseStack pMatrixStack,
      MultiBufferSource pBuffer,
      int pPackedLight,
      VaultDoodEntity pLivingEntity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float pPartialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      if (!pLivingEntity.isInvisible()) {
         Crackiness irongolem$crackiness = pLivingEntity.getCrackiness();
         if (irongolem$crackiness != Crackiness.NONE) {
            ResourceLocation resourcelocation = resourceLocations.get(irongolem$crackiness);
            renderColoredCutoutModel(this.getParentModel(), resourcelocation, pMatrixStack, pBuffer, pPackedLight, pLivingEntity, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}
