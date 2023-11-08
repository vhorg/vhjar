package iskallia.vault.entity.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.AncientCopperGolemEntity;
import iskallia.vault.entity.model.AncientCopperGolemModel;
import iskallia.vault.entity.model.ModModelLayers;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class AncientCopperGolemRenderer extends HumanoidMobRenderer<AncientCopperGolemEntity, AncientCopperGolemModel<AncientCopperGolemEntity>> {
   private static final ResourceLocation COPPER = VaultMod.id("textures/entity/ancient_copper_golem/copper.png");
   private static final ResourceLocation EXPOSED = VaultMod.id("textures/entity/ancient_copper_golem/exposed.png");
   private static final ResourceLocation WEATHERED = VaultMod.id("textures/entity/ancient_copper_golem/weathered.png");
   private static final ResourceLocation OXIDIZED = VaultMod.id("textures/entity/ancient_copper_golem/oxidized.png");
   private final SkullModel skullModel;

   public AncientCopperGolemRenderer(Context context) {
      super(context, new AncientCopperGolemModel(context.bakeLayer(ModModelLayers.ANCIENT_COPPER_GOLEM)), 0.3F);
      this.skullModel = new SkullModel(context.bakeLayer(ModelLayers.PLAYER_HEAD));
   }

   protected void setupRotations(AncientCopperGolemEntity pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
      super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
      if (pEntityLiving.getWaxed()) {
         pEntityLiving.getPosing().ifPresent(pEntityLiving::loadAngles);
      }
   }

   public void render(
      AncientCopperGolemEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight
   ) {
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
      if (pEntity.skin != null && pEntity.skin.gameProfile.get() != null && !pEntity.isDeadOrDying()) {
         GameProfile profile = pEntity.skin.gameProfile.get();
         RenderType rendertype = getRenderType(profile);
         if (pEntity.getWaxed()) {
            renderSkull(pEntity.yBodyRot + 180.0F, pMatrixStack, pBuffer, pPackedLight, this.skullModel, rendertype);
         } else {
            renderSkull(Mth.lerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot) + 180.0F, pMatrixStack, pBuffer, pPackedLight, this.skullModel, rendertype);
         }
      }
   }

   protected void renderNameTag(AncientCopperGolemEntity pEntity, Component pDisplayName, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      pMatrixStack.pushPose();
      pMatrixStack.translate(0.0, 0.3, 0.0);
      super.renderNameTag(pEntity, pDisplayName, pMatrixStack, pBuffer, pPackedLight);
      pMatrixStack.popPose();
   }

   public static void renderSkull(
      float pYRot, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, SkullModel pModel, RenderType pRenderType
   ) {
      pPoseStack.pushPose();
      pPoseStack.translate(0.0, 1.0, 0.0);
      pPoseStack.scale(-1.0F, -1.0F, 1.0F);
      VertexConsumer vertexconsumer = pBufferSource.getBuffer(pRenderType);
      pModel.setupAnim(0.0F, pYRot, 0.0F);
      pModel.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      pPoseStack.popPose();
   }

   public static RenderType getRenderType(@Nullable GameProfile pGameProfile) {
      ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkin();
      if (pGameProfile != null) {
         Minecraft minecraft = Minecraft.getInstance();
         Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(pGameProfile);
         return map.containsKey(Type.SKIN)
            ? RenderType.entityTranslucent(minecraft.getSkinManager().registerTexture(map.get(Type.SKIN), Type.SKIN))
            : RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(pGameProfile)));
      } else {
         return RenderType.entityCutoutNoCullZOffset(resourcelocation);
      }
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull AncientCopperGolemEntity entity) {
      return switch (entity.getTypeVariant()) {
         case 1 -> EXPOSED;
         case 2 -> WEATHERED;
         case 3 -> OXIDIZED;
         default -> COPPER;
      };
   }
}
