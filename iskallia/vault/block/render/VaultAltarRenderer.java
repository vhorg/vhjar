package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.altar.AltarInfusionRecipe;
import iskallia.vault.altar.RequiredItem;
import iskallia.vault.block.entity.VaultAltarTileEntity;
import iskallia.vault.init.ModItems;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class VaultAltarRenderer extends TileEntityRenderer<VaultAltarTileEntity> {
   private Minecraft mc = Minecraft.func_71410_x();
   private float currentTick = 0.0F;

   public VaultAltarRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public void render(VaultAltarTileEntity altar, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
      if (altar.getAltarState() != VaultAltarTileEntity.AltarState.IDLE) {
         ClientPlayerEntity player = this.mc.field_71439_g;
         int lightLevel = this.getLightAtPos(altar.func_145831_w(), altar.func_174877_v().func_177984_a());
         this.renderItem(
            new ItemStack(ModItems.VAULT_ROCK),
            new double[]{0.5, 1.35, 0.5},
            Vector3f.field_229181_d_.func_229187_a_(180.0F - player.field_70177_z),
            matrixStack,
            buffer,
            partialTicks,
            combinedOverlay,
            lightLevel
         );
         if (altar.getRecipe() != null && !altar.getRecipe().getRequiredItems().isEmpty()) {
            AltarInfusionRecipe recipe = altar.getRecipe();
            List<RequiredItem> items = recipe.getRequiredItems();

            for (int i = 0; i < items.size(); i++) {
               double[] translation = this.getTranslation(i);
               RequiredItem requiredItem = items.get(i);
               ItemStack stack = requiredItem.getItem();
               StringTextComponent text = new StringTextComponent(String.valueOf(requiredItem.getAmountRequired() - requiredItem.getCurrentAmount()));
               int textColor = 16777215;
               if (requiredItem.reachedAmountRequired()) {
                  text = new StringTextComponent("Complete");
                  textColor = 65280;
               }

               this.renderItem(
                  stack,
                  translation,
                  Vector3f.field_229181_d_.func_229187_a_(this.getAngle(player, partialTicks) * 5.0F),
                  matrixStack,
                  buffer,
                  partialTicks,
                  combinedOverlay,
                  lightLevel
               );
               this.renderLabel(requiredItem, matrixStack, buffer, lightLevel, translation, text, textColor);
            }
         }
      }
   }

   private void renderItem(
      ItemStack stack,
      double[] translation,
      Quaternion rotation,
      MatrixStack matrixStack,
      IRenderTypeBuffer buffer,
      float partialTicks,
      int combinedOverlay,
      int lightLevel
   ) {
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(translation[0], translation[1], translation[2]);
      matrixStack.func_227863_a_(rotation);
      if (stack.func_77973_b().getItem() != ModItems.VAULT_ROCK) {
         matrixStack.func_227862_a_(0.5F, 0.5F, 0.5F);
      }

      IBakedModel ibakedmodel = this.mc.func_175599_af().func_184393_a(stack, null, null);
      this.mc.func_175599_af().func_229111_a_(stack, TransformType.GROUND, true, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
      matrixStack.func_227865_b_();
   }

   private void renderLabel(
      RequiredItem item, MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, double[] corner, StringTextComponent text, int color
   ) {
      FontRenderer fontRenderer = this.mc.field_71466_p;
      ClientPlayerEntity player = Minecraft.func_71410_x().field_71439_g;
      if (player != null) {
         matrixStack.func_227860_a_();
         float scale = 0.01F;
         int opacity = 1711276032;
         float offset = -fontRenderer.func_238414_a_(text) / 2;
         Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
         matrixStack.func_227861_a_(corner[0], corner[1] + 0.25, corner[2]);
         matrixStack.func_227862_a_(scale, scale, scale);
         matrixStack.func_227863_a_(this.mc.func_175598_ae().func_229098_b_());
         matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
         fontRenderer.func_243247_a(text, offset, 0.0F, color, false, matrix4f, buffer, false, opacity, lightLevel);
         if (player.func_225608_bj_()) {
            ITextComponent itemName = item.getItem().func_200301_q();
            offset = -fontRenderer.func_238414_a_(itemName) / 2;
            matrixStack.func_227861_a_(0.0, 1.4F, 0.0);
            matrix4f.func_226597_a_(new Vector3f(0.0F, 0.15F, 0.0F));
            fontRenderer.func_243247_a(item.getItem().func_200301_q(), offset, 0.0F, color, false, matrix4f, buffer, false, opacity, lightLevel);
         }

         matrixStack.func_227865_b_();
      }
   }

   private float getAngle(ClientPlayerEntity player, float partialTicks) {
      this.currentTick = player.field_70173_aa;
      return (this.currentTick + partialTicks) % 360.0F;
   }

   private int getLightAtPos(World world, BlockPos pos) {
      int blockLight = world.func_226658_a_(LightType.BLOCK, pos);
      int skyLight = world.func_226658_a_(LightType.SKY, pos);
      return LightTexture.func_228451_a_(blockLight, skyLight);
   }

   private double[] getTranslation(int index) {
      switch (index) {
         case 0:
            return new double[]{0.95, 1.35, 0.05};
         case 1:
            return new double[]{0.95, 1.35, 0.95};
         case 2:
            return new double[]{0.05, 1.35, 0.95};
         default:
            return new double[]{0.05, 1.35, 0.05};
      }
   }
}
