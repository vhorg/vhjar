package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.entity.BaseSpawnerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

public class SpawnerRenderer implements BlockEntityRenderer<BaseSpawnerTileEntity> {
   public SpawnerRenderer(Context context) {
   }

   public void render(
      BaseSpawnerTileEntity spawnerTileEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player != null && (player.isCreative() || player.isSpectator())) {
         BlockState state = spawnerTileEntity.getBlockState();
         BakedModel bakedModel = minecraft.getModelManager().getBlockModelShaper().getBlockModel(state);
         minecraft.getBlockRenderer()
            .getModelRenderer()
            .renderModel(
               poseStack.last(),
               buffer.getBuffer(ItemBlockRenderTypes.getRenderType(state, false)),
               state,
               bakedModel,
               1.0F,
               1.0F,
               1.0F,
               packedLight,
               packedOverlay,
               EmptyModelData.INSTANCE
            );
      }
   }
}
