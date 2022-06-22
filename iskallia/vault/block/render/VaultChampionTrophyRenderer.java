package iskallia.vault.block.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.block.VaultChampionTrophy;
import iskallia.vault.block.entity.VaultChampionTrophyTileEntity;
import iskallia.vault.util.McClientHelper;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Direction;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class VaultChampionTrophyRenderer extends TileEntityRenderer<VaultChampionTrophyTileEntity> {
   public VaultChampionTrophyRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public void render(
      @Nonnull VaultChampionTrophyTileEntity tileEntity,
      float partialTicks,
      @Nonnull MatrixStack matrixStack,
      @Nonnull IRenderTypeBuffer buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      ClientWorld world = (ClientWorld)tileEntity.func_145831_w();
      if (world != null) {
         BlockState blockState = tileEntity.func_195044_w();
         Direction facing = (Direction)blockState.func_177229_b(VaultChampionTrophy.FACING);
         String ownerNickname = McClientHelper.getOnlineProfile(tileEntity.getOwnerUUID())
            .<String>map(GameProfile::getName)
            .orElse(tileEntity.getOwnerNickname());
         int score = tileEntity.getScore();
         this.drawNameplate(matrixStack, buffer, ownerNickname, score, facing, combinedLight, combinedOverlay);
      }
   }

   private void drawNameplate(
      MatrixStack matrixStack, IRenderTypeBuffer buffer, String displayName, int score, Direction direction, int combinedLight, int combinedOverlay
   ) {
      IReorderingProcessor text = new StringTextComponent(displayName).func_240699_a_(TextFormatting.BLACK).func_241878_f();
      IReorderingProcessor scoreText = new StringTextComponent(String.valueOf(score)).func_240699_a_(TextFormatting.BLACK).func_241878_f();
      FontRenderer fr = this.field_228858_b_.func_147548_a();
      int width = fr.func_243245_a(text);
      int scoreWidth = fr.func_243245_a(scoreText);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.5, 0.2, 0.5);
      matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(direction.func_185119_l() + 180.0F));
      matrixStack.func_227861_a_(0.0, 0.0, 0.255);
      matrixStack.func_227862_a_(0.01F, -0.01F, 0.01F);
      fr.func_238416_a_(text, -width / 2.0F, 0.0F, -16777216, false, matrixStack.func_227866_c_().func_227870_a_(), buffer, false, 0, combinedLight);
      fr.func_238416_a_(scoreText, -scoreWidth / 2.0F, 8.0F, -16777216, false, matrixStack.func_227866_c_().func_227870_a_(), buffer, false, 0, combinedLight);
      matrixStack.func_227865_b_();
   }
}
