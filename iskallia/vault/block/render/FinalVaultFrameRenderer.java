package iskallia.vault.block.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.block.FinalVaultFrameBlock;
import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.block.VaultPortalSize;
import iskallia.vault.block.entity.FinalVaultFrameTileEntity;
import iskallia.vault.client.util.LightmapUtil;
import iskallia.vault.client.util.ShaderUtil;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.util.McClientHelper;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.ARBShaderObjects;

public class FinalVaultFrameRenderer extends TileEntityRenderer<FinalVaultFrameTileEntity> {
   public static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel(0.1F, true);
   private static final Map<BlockPos, Long> PARTICLE_SPAWN_TIMESTAMPS = new HashMap<>();

   public FinalVaultFrameRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public void render(
      @Nonnull FinalVaultFrameTileEntity tileEntity,
      float partialTicks,
      @Nonnull MatrixStack matrixStack,
      @Nonnull IRenderTypeBuffer buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      ClientWorld world = (ClientWorld)tileEntity.func_145831_w();
      if (world != null) {
         boolean ownerOnline = McClientHelper.getOnlineProfile(tileEntity.getOwnerUUID())
            .<UUID>map(GameProfile::getId)
            .filter(uuid -> uuid.equals(tileEntity.getOwnerUUID()))
            .isPresent();
         ResourceLocation skinLocation = tileEntity.getSkin().getLocationSkin();
         RenderType renderType = PLAYER_MODEL.func_228282_a_(skinLocation);
         IVertexBuilder vertexBuilder = buffer.getBuffer(renderType);
         BlockPos blockPos = tileEntity.func_174877_v();
         BlockState blockState = tileEntity.func_195044_w();
         Direction direction = (Direction)blockState.func_177229_b(FinalVaultFrameBlock.FACING);
         matrixStack.func_227860_a_();
         ShaderUtil.useShader(ShaderUtil.COLORIZE_SHADER, () -> {
            Color color = new Color(-6646101);
            int colorR = ShaderUtil.getUniformLocation(ShaderUtil.COLORIZE_SHADER, "colorR");
            int colorG = ShaderUtil.getUniformLocation(ShaderUtil.COLORIZE_SHADER, "colorG");
            int colorB = ShaderUtil.getUniformLocation(ShaderUtil.COLORIZE_SHADER, "colorB");
            int brightness = ShaderUtil.getUniformLocation(ShaderUtil.COLORIZE_SHADER, "brightness");
            int grayscaleFactor = ShaderUtil.getUniformLocation(ShaderUtil.COLORIZE_SHADER, "grayscaleFactor");
            ARBShaderObjects.glUniform1fARB(colorR, color.getRed() / 255.0F);
            ARBShaderObjects.glUniform1fARB(colorG, color.getGreen() / 255.0F);
            ARBShaderObjects.glUniform1fARB(colorB, color.getBlue() / 255.0F);
            ARBShaderObjects.glUniform1fARB(brightness, LightmapUtil.getLightmapBrightness(combinedLight));
            ARBShaderObjects.glUniform1fARB(grayscaleFactor, ownerOnline ? 0.45F : 1.0F);
         });
         matrixStack.func_227861_a_(0.5, 0.5, 0.5);
         matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(direction.func_185119_l() + 180.0F));
         float headScale = 0.75F;
         matrixStack.func_227862_a_(headScale, headScale, 1.0F);
         matrixStack.func_227861_a_(0.0, -0.25, -0.275F);
         matrixStack.func_227862_a_(-1.0F, -1.0F, 1.0F);
         PLAYER_MODEL.field_178720_f.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         PLAYER_MODEL.field_78116_c.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         if (buffer instanceof Impl) {
            ((Impl)buffer).func_228462_a_(renderType);
         }

         ShaderUtil.releaseShader();
         boolean portalFormed = VaultPortalSize.getPortalSize(world, blockPos.func_177978_c(), Axis.Z, VaultPortalBlock.FRAME).isPresent()
            || VaultPortalSize.getPortalSize(world, blockPos.func_177968_d(), Axis.Z, VaultPortalBlock.FRAME).isPresent()
            || VaultPortalSize.getPortalSize(world, blockPos.func_177976_e(), Axis.X, VaultPortalBlock.FRAME).isPresent()
            || VaultPortalSize.getPortalSize(world, blockPos.func_177974_f(), Axis.X, VaultPortalBlock.FRAME).isPresent();
         if (portalFormed) {
            long now = System.currentTimeMillis();
            long prevTime = PARTICLE_SPAWN_TIMESTAMPS.computeIfAbsent(blockPos, p -> now);
            long dt = now - prevTime;
            if (dt >= 300L && world.field_73012_v.nextBoolean()) {
               addFlameParticle(world, blockPos, direction, 0.375F);
               addFlameParticle(world, blockPos, direction, -0.375F);
               PARTICLE_SPAWN_TIMESTAMPS.put(blockPos, now);
            }
         }

         matrixStack.func_227865_b_();
      }
   }

   private static void addFlameParticle(ClientWorld world, BlockPos blockPos, Direction direction, float offset) {
      float x = blockPos.func_177958_n() + 0.5F + direction.func_82601_c() * 0.625F;
      float y = blockPos.func_177956_o() + 0.8125F;
      float z = blockPos.func_177952_p() + 0.5F + direction.func_82599_e() * 0.625F;
      if (direction.func_176740_k() == Axis.Z) {
         x += offset;
      }

      if (direction.func_176740_k() == Axis.X) {
         z += offset;
      }

      float xSpeed = 0.0F;
      float ySpeed = 0.01F;
      float zSpeed = 0.0F;
      world.func_195594_a(ParticleTypes.field_239811_B_, x, y, z, xSpeed, ySpeed, zSpeed);
   }
}
