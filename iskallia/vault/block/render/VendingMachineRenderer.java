package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.block.PlayerStatueBlock;
import iskallia.vault.block.VendingMachineBlock;
import iskallia.vault.block.entity.VendingMachineTileEntity;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.init.ModItems;
import iskallia.vault.vending.TraderCore;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class VendingMachineRenderer extends TileEntityRenderer<VendingMachineTileEntity> {
   public static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel(0.1F, true);

   public VendingMachineRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public void render(
      VendingMachineTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay
   ) {
      TraderCore renderCore = tileEntity.getRenderCore();
      if (renderCore != null) {
         Minecraft minecraft = Minecraft.func_71410_x();
         boolean shouldOutline = false;
         if (minecraft.field_71439_g != null && minecraft.field_71439_g.func_184614_ca().func_77973_b() == ModItems.TRADER_CORE) {
            ItemStack heldStack = minecraft.field_71439_g.func_184614_ca();
            if (heldStack.func_77942_o()) {
               CompoundNBT nbt = heldStack.func_77978_p();
               CompoundNBT coreNBT = nbt.func_74775_l("core");
               if (coreNBT.func_74779_i("NAME").equals(renderCore.getName())) {
                  shouldOutline = true;
               }
            }
         }

         BlockState blockState = tileEntity.func_195044_w();
         ResourceLocation skinLocation = tileEntity.getSkin().getLocationSkin();
         if (shouldOutline) {
            IVertexBuilder outlineBuffer = buffer.getBuffer(RenderType.func_228654_j_(skinLocation));
            this.renderTrader(matrixStack, blockState, renderCore, outlineBuffer, combinedLight, combinedOverlay, 0.5F);
         }

         this.renderTrader(
            matrixStack, blockState, renderCore, buffer.getBuffer(PLAYER_MODEL.func_228282_a_(skinLocation)), combinedLight, combinedOverlay, 1.0F
         );
         BlockPos pos = tileEntity.func_174877_v();
         this.drawString(
            matrixStack,
            ((Direction)blockState.func_177229_b(VendingMachineBlock.FACING)).func_176734_d(),
            tileEntity.getSkin().getLatestNickname(),
            0.375F,
            pos.func_177958_n(),
            pos.func_177956_o(),
            pos.func_177952_p(),
            0.01F
         );
      }
   }

   public void renderTrader(
      MatrixStack matrixStack, BlockState blockState, TraderCore renderCore, IVertexBuilder vertexBuilder, int combinedLight, int combinedOverlay, float alpha
   ) {
      Direction direction = (Direction)blockState.func_177229_b(PlayerStatueBlock.FACING);
      float scale = renderCore.isMegahead() ? 0.8F : 0.9F;
      float headScale = renderCore.isMegahead() ? 1.75F : 1.0F;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.5, renderCore.isMegahead() ? 1.1 : 1.3, 0.5);
      matrixStack.func_227862_a_(scale, scale, scale);
      matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(direction.func_185119_l() + 180.0F));
      matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(180.0F));
      PLAYER_MODEL.field_78115_e.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.field_178722_k.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.field_178721_j.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.field_178724_i.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.field_178723_h.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.field_178730_v.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.field_178733_c.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.field_178731_d.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.field_178734_a.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.0, 0.0, -0.62F);
      PLAYER_MODEL.field_178732_b.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      matrixStack.func_227865_b_();
      matrixStack.func_227862_a_(headScale, headScale, headScale);
      PLAYER_MODEL.field_178720_f.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.field_78116_c.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      matrixStack.func_227865_b_();
   }

   public void drawString(MatrixStack matrixStack, Direction facing, String text, float yOffset, double x, double y, double z, float scale) {
      FontRenderer fontRenderer = Minecraft.func_71410_x().field_71466_p;
      float size = fontRenderer.func_78256_a(text) * scale;
      float textCenter = (1.0F + size) / 2.0F;
      matrixStack.func_227860_a_();
      if (facing == Direction.NORTH) {
         matrixStack.func_227861_a_(textCenter, yOffset, -0.025000006F);
         matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
      } else if (facing == Direction.SOUTH) {
         matrixStack.func_227861_a_(-textCenter + 1.0F, yOffset, 1.025F);
         matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
         matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F));
      } else if (facing == Direction.EAST) {
         matrixStack.func_227861_a_(1.025F, yOffset, textCenter);
         matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
         matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(90.0F));
      } else if (facing == Direction.WEST) {
         matrixStack.func_227861_a_(-0.025000006F, yOffset, -textCenter + 1.0F);
         matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
         matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(270.0F));
      }

      matrixStack.func_227861_a_(0.0, 0.0, 0.03125);
      matrixStack.func_227862_a_(scale, scale, scale);
      fontRenderer.func_238421_b_(matrixStack, text, 0.0F, 0.0F, -1);
      matrixStack.func_227865_b_();
   }

   private int getLightAtPos(World world, BlockPos pos) {
      int blockLight = world.func_226658_a_(LightType.BLOCK, pos);
      int skyLight = world.func_226658_a_(LightType.SKY, pos);
      return LightTexture.func_228451_a_(blockLight, skyLight);
   }
}
