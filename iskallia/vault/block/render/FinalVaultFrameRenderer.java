package iskallia.vault.block.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.FinalVaultFrameBlock;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.block.VaultPortalSize;
import iskallia.vault.block.entity.FinalVaultFrameTileEntity;
import iskallia.vault.client.util.LightmapUtil;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.init.ModShaders;
import iskallia.vault.util.McClientHelper;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class FinalVaultFrameRenderer implements BlockEntityRenderer<FinalVaultFrameTileEntity> {
   public static StatuePlayerModel PLAYER_MODEL;
   private static final Map<BlockPos, Long> PARTICLE_SPAWN_TIMESTAMPS = new HashMap<>();

   public FinalVaultFrameRenderer(Context context) {
      PLAYER_MODEL = new StatuePlayerModel(context);
   }

   public void render(
      @Nonnull FinalVaultFrameTileEntity tileEntity,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      ClientLevel world = (ClientLevel)tileEntity.getLevel();
      if (world != null) {
         boolean ownerOnline = McClientHelper.getOnlineProfile(tileEntity.getOwnerUUID())
            .<UUID>map(GameProfile::getId)
            .filter(uuid -> uuid.equals(tileEntity.getOwnerUUID()))
            .isPresent();
         ResourceLocation skinLocation = tileEntity.getSkin().getLocationSkin();
         RenderType renderType = PLAYER_MODEL.renderType(skinLocation);
         VertexConsumer vertexBuilder = buffer.getBuffer(renderType);
         BlockPos blockPos = tileEntity.getBlockPos();
         BlockState blockState = tileEntity.getBlockState();
         Direction direction = (Direction)blockState.getValue(FinalVaultFrameBlock.FACING);
         matrixStack.pushPose();
         Color color = new Color(-6646101);
         ModShaders.getColorizePositionTexShader()
            .withColorize(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F)
            .withBrightness(LightmapUtil.getLightmapBrightness(combinedLight))
            .withGrayscale(ownerOnline ? 0.45F : 1.0F)
            .enable();
         matrixStack.translate(0.5, 0.5, 0.5);
         matrixStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot() + 180.0F));
         float headScale = 0.75F;
         matrixStack.scale(headScale, headScale, 1.0F);
         matrixStack.translate(0.0, -0.25, -0.275F);
         matrixStack.scale(-1.0F, -1.0F, 1.0F);
         PLAYER_MODEL.hat.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.head.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         if (buffer instanceof BufferSource) {
            ((BufferSource)buffer).endBatch(renderType);
         }

         boolean portalFormed = VaultPortalSize.getPortalSize(world, blockPos.north(), Axis.Z, VaultPortalBlock.FRAME).isPresent()
            || VaultPortalSize.getPortalSize(world, blockPos.south(), Axis.Z, VaultPortalBlock.FRAME).isPresent()
            || VaultPortalSize.getPortalSize(world, blockPos.west(), Axis.X, VaultPortalBlock.FRAME).isPresent()
            || VaultPortalSize.getPortalSize(world, blockPos.east(), Axis.X, VaultPortalBlock.FRAME).isPresent();
         if (portalFormed) {
            long now = System.currentTimeMillis();
            long prevTime = PARTICLE_SPAWN_TIMESTAMPS.computeIfAbsent(blockPos, p -> now);
            long dt = now - prevTime;
            if (dt >= 300L && world.random.nextBoolean()) {
               addFlameParticle(world, blockPos, direction, 0.375F);
               addFlameParticle(world, blockPos, direction, -0.375F);
               PARTICLE_SPAWN_TIMESTAMPS.put(blockPos, now);
            }
         }

         matrixStack.popPose();
      }
   }

   private static void addFlameParticle(ClientLevel world, BlockPos blockPos, Direction direction, float offset) {
      float x = blockPos.getX() + 0.5F + direction.getStepX() * 0.625F;
      float y = blockPos.getY() + 0.8125F;
      float z = blockPos.getZ() + 0.5F + direction.getStepZ() * 0.625F;
      if (direction.getAxis() == Axis.Z) {
         x += offset;
      }

      if (direction.getAxis() == Axis.X) {
         z += offset;
      }

      float xSpeed = 0.0F;
      float ySpeed = 0.01F;
      float zSpeed = 0.0F;
      world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, xSpeed, ySpeed, zSpeed);
   }
}
