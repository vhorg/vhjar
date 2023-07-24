package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.BountyTableTileEntity;
import iskallia.vault.block.model.BountyBlockExclamationModel;
import iskallia.vault.block.model.BountyBlockQuestionModel;
import iskallia.vault.bounty.Bounty;
import iskallia.vault.bounty.client.ClientBountyData;
import iskallia.vault.client.ClientExpertiseData;
import iskallia.vault.init.ModItems;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.expertise.type.BountyHunterExpertise;
import iskallia.vault.util.InventoryUtil;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.Level;

public class BountyTableRenderer implements BlockEntityRenderer<BountyTableTileEntity> {
   private final Minecraft mc = Minecraft.getInstance();
   BountyBlockExclamationModel exclamationPoint;
   BountyBlockQuestionModel questionMark;

   public BountyTableRenderer(Context context) {
      this.exclamationPoint = new BountyBlockExclamationModel(context.bakeLayer(BountyBlockExclamationModel.LAYER_LOCATION));
      this.questionMark = new BountyBlockQuestionModel(context.bakeLayer(BountyBlockQuestionModel.LAYER_LOCATION));
   }

   public void render(
      BountyTableTileEntity mobBarrierTile,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = mobBarrierTile.getLevel();
      if (world != null) {
         if (this.mc.player != null) {
            ArrayList<Bounty> bountyList = ClientBountyData.INSTANCE.getBounties();
            ArrayList<Bounty> availableBountyList = ClientBountyData.INSTANCE.getAvailable();
            boolean hasCompleteBounty = false;
            boolean hasLegendary = false;

            for (Bounty bounty : bountyList) {
               if (bounty.getTask().isComplete()) {
                  hasCompleteBounty = true;
               }

               if (bounty.getTask().getProperties().getRewardPool().equals("legendary")) {
                  hasLegendary = true;
               }
            }

            int maxActiveBounties = 1;

            for (TieredSkill learnedTalentNode : ClientExpertiseData.getLearnedTalentNodes()) {
               if (learnedTalentNode.getChild() instanceof BountyHunterExpertise bountyHunterExpertise) {
                  maxActiveBounties = bountyHunterExpertise.getMaxActive();
               }
            }

            boolean hasLostBounty = InventoryUtil.findAllItems(this.mc.player).stream().anyMatch(itemAccess -> itemAccess.getStack().is(ModItems.LOST_BOUNTY));
            boolean canActivateAvailable = hasLegendary ? bountyList.size() - 1 < maxActiveBounties : bountyList.size() < maxActiveBounties;
            if (canActivateAvailable && !hasCompleteBounty && availableBountyList.size() > 0 || hasLostBounty && !hasLegendary) {
               matrixStack.pushPose();
               matrixStack.translate(0.5, 2.0 + Math.sin(this.mc.player.tickCount / 10.0F) / 20.0, 0.5);
               matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
               matrixStack.mulPose(Vector3f.YP.rotationDegrees(this.mc.player.tickCount));
               matrixStack.scale(0.75F, 0.75F, 0.75F);
               VertexConsumer vertexConsumer = BountyBlockExclamationModel.MATERIAL.buffer(buffer, RenderType::entityTranslucent);
               this.exclamationPoint.renderToBuffer(matrixStack, vertexConsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
               matrixStack.popPose();
            }

            if (hasCompleteBounty) {
               matrixStack.pushPose();
               matrixStack.translate(0.5, 2.0 + Math.sin(this.mc.player.tickCount / 10.0F) / 20.0, 0.5);
               matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
               matrixStack.mulPose(Vector3f.YP.rotationDegrees(this.mc.player.tickCount));
               matrixStack.scale(0.75F, 0.75F, 0.75F);
               VertexConsumer vertexConsumer = BountyBlockQuestionModel.MATERIAL.buffer(buffer, RenderType::entityTranslucent);
               this.questionMark.renderToBuffer(matrixStack, vertexConsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
               matrixStack.popPose();
            }
         }
      }
   }
}
