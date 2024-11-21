package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.BountyTableTileEntity;
import iskallia.vault.block.model.BountyBlockExclamationModel;
import iskallia.vault.block.model.BountyBlockQuestionModel;
import iskallia.vault.bounty.Bounty;
import iskallia.vault.bounty.client.ClientBountyData;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.entity.player.Player;

public class BountyTableRenderer implements BlockEntityRenderer<BountyTableTileEntity> {
   private final BountyBlockExclamationModel exclamationPoint;
   private final BountyBlockQuestionModel questionMark;

   public BountyTableRenderer(Context context) {
      this.exclamationPoint = new BountyBlockExclamationModel(context.bakeLayer(BountyBlockExclamationModel.LAYER_LOCATION));
      this.questionMark = new BountyBlockQuestionModel(context.bakeLayer(BountyBlockQuestionModel.LAYER_LOCATION));
   }

   public void render(
      BountyTableTileEntity bountyTable,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         int tickCount = player.tickCount;
         List<Bounty> bounties = ClientBountyData.INSTANCE.getBounties();
         List<Bounty> availableBounties = ClientBountyData.INSTANCE.getAvailable();
         boolean canActivateAvailable = bounties.size() < ClientBountyData.getMaxActiveBounties();
         if (ClientBountyData.hasLegendaryBounty()) {
            canActivateAvailable = bounties.size() - 1 < ClientBountyData.getMaxActiveBounties();
         }

         if (canActivateAvailable && !ClientBountyData.hasCompletedBounty() && availableBounties.size() > 0
            || ClientBountyData.hasLostBountyInInventory() && !ClientBountyData.hasLegendaryBounty()) {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 2.0 + Math.sin(tickCount / 10.0F) / 20.0, 0.5);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(tickCount));
            matrixStack.scale(0.75F, 0.75F, 0.75F);
            VertexConsumer vertexConsumer = BountyBlockExclamationModel.MATERIAL.buffer(buffer, RenderType::entityTranslucent);
            this.exclamationPoint.renderToBuffer(matrixStack, vertexConsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
         }

         if (ClientBountyData.hasCompletedBounty()) {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 2.0 + Math.sin(tickCount / 10.0F) / 20.0, 0.5);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(tickCount));
            matrixStack.scale(0.75F, 0.75F, 0.75F);
            VertexConsumer vertexConsumer = BountyBlockQuestionModel.MATERIAL.buffer(buffer, RenderType::entityTranslucent);
            this.questionMark.renderToBuffer(matrixStack, vertexConsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
         }
      }
   }
}
