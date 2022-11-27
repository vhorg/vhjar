package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.block.SpiritExtractorBlock;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.entity.renderer.PlayerSkinUpdater;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SpiritExtractorRenderer implements BlockEntityRenderer<SpiritExtractorTileEntity> {
   private final PlayerModel<Player> alexModel;
   private final PlayerModel<Player> steveModel;
   private final PlayerSkinUpdater playerSkinUpdater = new PlayerSkinUpdater();

   public SpiritExtractorRenderer(Context context) {
      this.alexModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
      this.setupModelAttributes(this.alexModel);
      this.steveModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
      this.setupModelAttributes(this.steveModel);
   }

   private void setupModelAttributes(PlayerModel<Player> model) {
      model.young = false;
      model.body.y += 0.01F;
      model.jacket.y += 0.01F;
      model.head.y += 0.02F;
      model.hat.y += 0.02F;
      model.leftArm.x += 0.01F;
      model.leftSleeve.x += 0.01F;
      model.rightArm.x -= 0.01F;
      model.rightSleeve.x -= 0.01F;
      model.leftLeg.x += 0.01F;
      model.leftPants.x += 0.01F;
      model.rightLeg.x -= 0.01F;
      model.rightPants.x -= 0.01F;
   }

   public void render(
      SpiritExtractorTileEntity spiritExtractorTileEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
   ) {
      if (!spiritExtractorTileEntity.getGameProfile().isEmpty()) {
         ResourceLocation playerSkin = this.getPlayerSkin(spiritExtractorTileEntity);
         PlayerModel<Player> model = spiritExtractorTileEntity.hasSlimSkin() ? this.alexModel : this.steveModel;
         poseStack.pushPose();
         poseStack.translate(0.5, 2.02, 0.5);
         poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         poseStack.mulPose(this.getRotation((Direction)spiritExtractorTileEntity.getBlockState().getValue(SpiritExtractorBlock.FACING)));
         RenderType renderType = model.renderType(playerSkin);
         model.renderToBuffer(poseStack, buffer.getBuffer(renderType), packedLight, packedOverlay, 0.5F, 0.5F, 0.5F, 0.5F);
         poseStack.popPose();
      }
   }

   private ResourceLocation getPlayerSkin(SpiritExtractorTileEntity spiritExtractorTileEntity) {
      return spiritExtractorTileEntity.getSkinLocation()
         .orElseGet(
            () -> spiritExtractorTileEntity.getGameProfile()
               .map(gp -> this.playerSkinUpdater.updatePlayerSkin(spiritExtractorTileEntity, gp))
               .orElse(DefaultPlayerSkin.getDefaultSkin())
         );
   }

   private Quaternion getRotation(Direction direction) {
      return switch (direction) {
         case NORTH -> Quaternion.ONE;
         case SOUTH -> Vector3f.YP.rotationDegrees(180.0F);
         case WEST -> Vector3f.YP.rotationDegrees(-90.0F);
         case EAST -> Vector3f.YP.rotationDegrees(90.0F);
         case UP, DOWN -> Quaternion.ONE;
         default -> throw new IncompatibleClassChangeError();
      };
   }
}
