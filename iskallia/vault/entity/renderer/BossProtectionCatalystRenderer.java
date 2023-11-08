package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.boss.BossProtectionCatalystEntity;
import iskallia.vault.entity.model.BossProtectionCatalystModel;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BossProtectionCatalystRenderer extends EntityRenderer<BossProtectionCatalystEntity> {
   private static final ResourceLocation WOODEN_TEXTURE = VaultMod.id("textures/entity/wooden_boss_protection_catalyst.png");
   private static final Map<BossProtectionCatalystEntity.CatalystType, ResourceLocation> CATALYST_TYPE_TEXTURES = Map.of(
      BossProtectionCatalystEntity.CatalystType.WOODEN,
      WOODEN_TEXTURE,
      BossProtectionCatalystEntity.CatalystType.GILDED,
      VaultMod.id("textures/entity/gilded_boss_protection_catalyst.png"),
      BossProtectionCatalystEntity.CatalystType.ORNATE,
      VaultMod.id("textures/entity/ornate_boss_protection_catalyst.png"),
      BossProtectionCatalystEntity.CatalystType.LIVING,
      VaultMod.id("textures/entity/living_boss_protection_catalyst.png")
   );
   private final BossProtectionCatalystModel model;

   public BossProtectionCatalystRenderer(Context context) {
      super(context);
      this.model = new BossProtectionCatalystModel(context.bakeLayer(BossProtectionCatalystModel.MODEL_LOCATION));
   }

   public void render(
      BossProtectionCatalystEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight
   ) {
      matrixStack.pushPose();
      matrixStack.translate(0.0, -0.5, 0.0);
      VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
      this.model.renderToBuffer(matrixStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
   }

   public ResourceLocation getTextureLocation(BossProtectionCatalystEntity entity) {
      return CATALYST_TYPE_TEXTURES.getOrDefault(entity.getCatalystType(), WOODEN_TEXTURE);
   }
}
