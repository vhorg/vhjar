package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.PlayerStatueBlock;
import iskallia.vault.block.RelicStatueBlock;
import iskallia.vault.block.entity.RelicStatueTileEntity;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.item.RelicItem;
import iskallia.vault.util.RelicSet;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;

public class RelicStatueRenderer extends TileEntityRenderer<RelicStatueTileEntity> {
   public static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel(0.1F, true);
   public static final ResourceLocation TWOLF999_SKIN = Vault.id("textures/block/statue_twolf999.png");
   public static final ResourceLocation SHIELDMANH_SKIN = Vault.id("textures/block/statue_shieldmanh.png");

   public RelicStatueRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public void render(
      RelicStatueTileEntity statue, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay
   ) {
      RelicSet relicSet = RelicSet.REGISTRY.get(statue.getRelicSet());
      BlockState state = statue.func_195044_w();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.5, 0.0, 0.5);
      float horizontalAngle = ((Direction)state.func_177229_b(RelicStatueBlock.FACING)).func_185119_l();
      matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(180.0F + horizontalAngle));
      if (relicSet == RelicSet.DRAGON) {
         matrixStack.func_227861_a_(0.0, 0.0, 0.15);
         matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(90.0F));
         this.renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.7F, 7.0F, (Item)Registry.field_212630_s.func_82594_a(Vault.id("statue_dragon")));
      } else if (relicSet == RelicSet.MINER) {
         this.renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2F, 2.0F, RelicItem.withCustomModelData(0));
      } else if (relicSet == RelicSet.WARRIOR) {
         this.renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2F, 2.0F, RelicItem.withCustomModelData(1));
      } else if (relicSet == RelicSet.RICHITY) {
         this.renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2F, 2.0F, RelicItem.withCustomModelData(2));
      } else if (relicSet == RelicSet.TWITCH) {
         this.renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2F, 2.0F, RelicItem.withCustomModelData(3));
      } else if (relicSet == RelicSet.CUPCAKE) {
         this.renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2F, 2.0F, RelicItem.withCustomModelData(4));
      } else if (relicSet == RelicSet.ELEMENT) {
         this.renderItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.2F, 2.0F, RelicItem.withCustomModelData(5));
      } else if (relicSet == RelicSet.TWOLF999) {
         IVertexBuilder vertexBuilder = this.getPlayerVertexBuilder(TWOLF999_SKIN, buffer);
         this.renderPlayer(matrixStack, state, vertexBuilder, combinedLight, combinedOverlay);
      } else if (relicSet == RelicSet.SHIELDMANH) {
         IVertexBuilder vertexBuilder = this.getPlayerVertexBuilder(SHIELDMANH_SKIN, buffer);
         this.renderPlayer(matrixStack, state, vertexBuilder, combinedLight, combinedOverlay);
      }

      matrixStack.func_227865_b_();
   }

   public IVertexBuilder getPlayerVertexBuilder(ResourceLocation skinTexture, IRenderTypeBuffer buffer) {
      RenderType renderType = PLAYER_MODEL.func_228282_a_(skinTexture);
      return buffer.getBuffer(renderType);
   }

   public void renderPlayer(MatrixStack matrixStack, BlockState blockState, IVertexBuilder vertexBuilder, int combinedLight, int combinedOverlay) {
      Direction direction = (Direction)blockState.func_177229_b(PlayerStatueBlock.FACING);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.0, 1.6, 0.0);
      matrixStack.func_227862_a_(0.4F, 0.4F, 0.4F);
      matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(180.0F));
      PLAYER_MODEL.field_78115_e.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      PLAYER_MODEL.field_178722_k.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      PLAYER_MODEL.field_178721_j.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      PLAYER_MODEL.field_178724_i.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      PLAYER_MODEL.field_178723_h.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      PLAYER_MODEL.field_178730_v.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      PLAYER_MODEL.field_178733_c.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      PLAYER_MODEL.field_178731_d.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      PLAYER_MODEL.field_178734_a.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.0, 0.0, -0.62F);
      PLAYER_MODEL.field_178732_b.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.func_227865_b_();
      PLAYER_MODEL.field_178720_f.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      PLAYER_MODEL.field_78116_c.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.func_227865_b_();
   }

   private void renderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, int overlay, float yOffset, float scale, Item item) {
      this.renderItem(matrixStack, buffer, lightLevel, overlay, yOffset, scale, new ItemStack(item));
   }

   private void renderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, int overlay, float yOffset, float scale, ItemStack itemStack) {
      Minecraft minecraft = Minecraft.func_71410_x();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.0, yOffset, 0.0);
      matrixStack.func_227862_a_(scale, scale, scale);
      IBakedModel ibakedmodel = minecraft.func_175599_af().func_184393_a(itemStack, null, null);
      minecraft.func_175599_af().func_229111_a_(itemStack, TransformType.GROUND, true, matrixStack, buffer, lightLevel, overlay, ibakedmodel);
      matrixStack.func_227865_b_();
   }
}
