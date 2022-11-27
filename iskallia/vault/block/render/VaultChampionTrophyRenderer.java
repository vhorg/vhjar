package iskallia.vault.block.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.VaultChampionTrophy;
import iskallia.vault.block.entity.VaultChampionTrophyTileEntity;
import iskallia.vault.util.McClientHelper;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;

public class VaultChampionTrophyRenderer implements BlockEntityRenderer<VaultChampionTrophyTileEntity> {
   public VaultChampionTrophyRenderer(Context context) {
   }

   public void render(
      @Nonnull VaultChampionTrophyTileEntity tileEntity,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      ClientLevel world = (ClientLevel)tileEntity.getLevel();
      if (world != null) {
         BlockState blockState = tileEntity.getBlockState();
         Direction facing = (Direction)blockState.getValue(VaultChampionTrophy.FACING);
         String ownerNickname = McClientHelper.getOnlineProfile(tileEntity.getOwnerUUID())
            .<String>map(GameProfile::getName)
            .orElse(tileEntity.getOwnerNickname());
         this.drawNameplate(matrixStack, buffer, ownerNickname, facing, combinedLight, combinedOverlay);
      }
   }

   private void drawNameplate(PoseStack matrixStack, MultiBufferSource buffer, String displayName, Direction direction, int combinedLight, int combinedOverlay) {
      FormattedCharSequence text = new TextComponent(displayName).withStyle(ChatFormatting.BLACK).getVisualOrderText();
      Font fr = Minecraft.getInstance().font;
      int width = fr.width(text);
      matrixStack.pushPose();
      matrixStack.translate(0.5, 0.17, 0.5);
      matrixStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot() + 180.0F));
      matrixStack.translate(0.0, 0.0, 0.27);
      matrixStack.scale(0.01F, -0.01F, 0.01F);
      fr.drawInBatch(text, -width / 2.0F, 0.0F, -16777216, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
      matrixStack.popPose();
   }
}
