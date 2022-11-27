package iskallia.vault.block.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.entity.AncientCryoChamberTileEntity;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.client.util.LightmapUtil;
import iskallia.vault.client.util.RenderTypeDecorator;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.entity.renderer.EternalRenderer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModShaders;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class CryoChamberRenderer implements BlockEntityRenderer<CryoChamberTileEntity> {
   public static final Minecraft mc = Minecraft.getInstance();
   public static final ResourceLocation INFUSED_PLAYER_SKIN = VaultMod.id("textures/entity/infusion_skin_white.png");
   public static StatuePlayerModel PLAYER_MODEL;
   public static RenderType wrapped = RenderTypeDecorator.decorate(RenderType.lines(), RenderSystem::disableDepthTest, () -> {});
   private final Color[] colors = new Color[]{Color.WHITE, Color.YELLOW, Color.MAGENTA, Color.GREEN};
   private int index = 0;
   private boolean wait = false;
   private Color currentColor = Color.WHITE;
   private float currentRed = 1.0F;
   private float currentGreen = 1.0F;
   private float currentBlue = 1.0F;
   private final float colorChangeDelay = 3.0F;

   public CryoChamberRenderer(Context context) {
      PLAYER_MODEL = new StatuePlayerModel(context);
   }

   public VertexConsumer getPlayerVertexBuilder(ResourceLocation skinTexture, MultiBufferSource buffer) {
      RenderType renderType = PLAYER_MODEL.renderType(skinTexture);
      return buffer.getBuffer(renderType);
   }

   public void render(
      CryoChamberTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      if (tileEntity.isInfusing()) {
         float maxTime = ModConfigs.CRYO_CHAMBER.getInfusionTime();
         float scale = Math.min(tileEntity.getInfusionTimeRemaining() / maxTime, 0.8F);
         tileEntity.updateSkin();
         VertexConsumer vertexBuilder = this.getPlayerVertexBuilder(INFUSED_PLAYER_SKIN, buffer);
         this.renderPlayerModel(matrixStack, tileEntity, scale, 0.5F, vertexBuilder, combinedLight, combinedOverlay);
      } else if (tileEntity.isGrowingEternal()) {
         float maxTime = ModConfigs.CRYO_CHAMBER.getGrowEternalTime();
         float scale = Math.min(1.0F - tileEntity.getGrowEternalTimeRemaining() / maxTime, 0.8F);
         VertexConsumer vertexBuilder = this.getPlayerVertexBuilder(INFUSED_PLAYER_SKIN, buffer);
         this.renderPlayerModel(matrixStack, tileEntity, scale, 0.5F, vertexBuilder, combinedLight, combinedOverlay);
      } else if (tileEntity.getEternalId() != null) {
         EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(tileEntity.getEternalId());
         if (snapshot != null && snapshot.getName() != null) {
            tileEntity.updateSkin();
            if (buffer instanceof BufferSource) {
               ((BufferSource)buffer).endBatch();
            }

            if (!snapshot.isAlive()) {
               ModShaders.getGrayscalePositionTexShader().withGrayscale(0.0F).withBrightness(LightmapUtil.getLightmapBrightness(combinedLight)).enable();
            }

            ResourceLocation skinTexture = tileEntity.getSkin().getLocationSkin();
            boolean usingPlayerSkin = tileEntity.usingPlayerSkin;
            boolean flag = tileEntity.variant != null;
            VertexConsumer vertexBuilder = this.getPlayerVertexBuilder(
               usingPlayerSkin ? skinTexture : (flag ? EternalRenderer.getLocationByVariant().get(tileEntity.variant) : skinTexture), buffer
            );
            this.renderPlayerModel(matrixStack, tileEntity, 0.8F, 1.0F, vertexBuilder, combinedLight, combinedOverlay);
            if (buffer instanceof BufferSource) {
               ((BufferSource)buffer).endBatch();
            }
         }
      } else if (tileEntity instanceof AncientCryoChamberTileEntity) {
         tileEntity.updateSkin();
         ResourceLocation skinTexture = tileEntity.getSkin().getLocationSkin();
         boolean usingPlayerSkin = tileEntity.usingPlayerSkin;
         boolean flag = tileEntity.variant != null;
         VertexConsumer vertexBuilder = this.getPlayerVertexBuilder(
            usingPlayerSkin ? skinTexture : (flag ? EternalRenderer.getLocationByVariant().get(tileEntity.variant) : skinTexture), buffer
         );
         this.renderPlayerModel(matrixStack, tileEntity, 0.8F, 1.0F, vertexBuilder, combinedLight, combinedOverlay);
         if (buffer instanceof BufferSource) {
            ((BufferSource)buffer).endBatch();
         }
      }

      this.renderLiquid(matrixStack, tileEntity, buffer, partialTicks);
      if (mc.hitResult != null && mc.hitResult.getType() == Type.BLOCK) {
         String eternalName = null;
         EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(tileEntity.getEternalId());
         if (snapshot != null && snapshot.getName() != null) {
            eternalName = snapshot.getName();
         }

         if (tileEntity instanceof AncientCryoChamberTileEntity) {
            eternalName = ((AncientCryoChamberTileEntity)tileEntity).getEternalName();
         }

         if (eternalName != null) {
            BlockHitResult result = (BlockHitResult)mc.hitResult;
            if (tileEntity.getBlockPos().equals(result.getBlockPos()) || tileEntity.getBlockPos().above().equals(result.getBlockPos())) {
               this.renderLabel(
                  matrixStack,
                  buffer,
                  combinedLight,
                  new TextComponent(eternalName),
                  -1,
                  tileEntity.getLevel().getBlockState(result.getBlockPos()).getValue(CryoChamberBlock.HALF) == DoubleBlockHalf.UPPER
               );
            }
         }
      }
   }

   private void renderLabel(PoseStack matrixStack, MultiBufferSource buffer, int lightLevel, TextComponent text, int color, boolean topBlock) {
      Font fontRenderer = mc.font;
      matrixStack.pushPose();
      float scale = 0.02F;
      int opacity = 1711276032;
      float offset = -fontRenderer.width(text) / 2;
      Matrix4f matrix4f = matrixStack.last().pose();
      matrixStack.translate(0.5, 2.3F, 0.5);
      matrixStack.scale(scale, scale, scale);
      matrixStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      fontRenderer.drawInBatch(text, offset, 0.0F, color, false, matrix4f, buffer, true, opacity, lightLevel);
      fontRenderer.drawInBatch(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, lightLevel);
      matrixStack.popPose();
   }

   public void renderPlayerModel(
      PoseStack matrixStack, CryoChamberTileEntity tileEntity, float scale, float alpha, VertexConsumer vertexBuilder, int combinedLight, int combinedOverlay
   ) {
      BlockState blockState = tileEntity.getBlockState();
      Direction direction = (Direction)blockState.getValue(CryoChamberBlock.FACING);
      matrixStack.pushPose();
      matrixStack.translate(0.5, 1.3, 0.5);
      matrixStack.scale(scale, scale, scale);
      matrixStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot() + 180.0F));
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      PLAYER_MODEL.setSlim(tileEntity.getSkin().isSlim());
      PLAYER_MODEL.body.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.leftLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.rightLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.leftArm.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.rightArm.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.jacket.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.leftPants.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.rightPants.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.leftSleeve.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.rightSleeve.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.hat.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.head.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      matrixStack.popPose();
   }

   private Quaternion getRotationFromDirection(Direction direction) {
      return switch (direction) {
         case NORTH, SOUTH -> Vector3f.YP.rotationDegrees(direction.getOpposite().toYRot());
         default -> Vector3f.YP.rotationDegrees(direction.toYRot());
      };
   }

   private double[] getRootTranslation(Direction direction) {
      return switch (direction) {
         case SOUTH -> new double[]{-1.0, 0.0, -1.0};
         case WEST -> new double[]{-1.0, 0.0, 0.0};
         case EAST -> new double[]{0.0, 0.0, -1.0};
         default -> new double[]{0.0, 0.0, 0.0};
      };
   }

   private void renderLiquid(PoseStack matrixStack, CryoChamberTileEntity tileEntity, MultiBufferSource buffer, float partialTicks) {
      if (tileEntity.getMaxCores() != 0) {
         VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
         TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance()
            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
            .apply(Fluids.WATER.getAttributes().getStillTexture());
         BlockState blockState = tileEntity.getBlockState();
         Direction direction = (Direction)blockState.getValue(CryoChamberBlock.FACING);
         float max = tileEntity.getMaxCores();
         float difference = tileEntity.getCoreCount() - tileEntity.lastCoreCount;
         tileEntity.lastCoreCount += difference * 0.02F;
         float scale = tileEntity.lastCoreCount / max;
         this.updateIndex(mc.player.tickCount);
         this.updateColor(partialTicks, tileEntity);
         float r = this.currentColor.getRed() / 255.0F;
         float g = this.currentColor.getGreen() / 255.0F;
         float b = this.currentColor.getBlue() / 255.0F;
         float minU = sprite.getU(0.0);
         float maxU = sprite.getU(16.0);
         float minV = sprite.getV(0.0);
         float maxVBottom = sprite.getV(scale < 0.5 ? scale * 2.0F * 16.0 : 16.0);
         float maxVTop = sprite.getV(scale >= 0.5 ? (scale * 2.0F - 1.0F) * 16.0 : 0.0);
         float bottomHeight = scale < 0.5F ? scale * 2.0F : 1.0F;
         float topHeight = scale < 0.5F ? 0.0F : Math.min(scale * 2.0F, 1.9F);
         matrixStack.pushPose();
         this.renderSides(matrixStack, builder, scale, r, g, b, minU, maxU, minV, maxVBottom, maxVTop, bottomHeight, topHeight, direction);
         this.renderTop(matrixStack, builder, scale, r, g, b, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), bottomHeight, topHeight);
         matrixStack.popPose();
      }
   }

   private void renderTop(
      PoseStack matrixStack,
      VertexConsumer builder,
      float scale,
      float r,
      float g,
      float b,
      float minU,
      float maxU,
      float minV,
      float maxV,
      float bottomHeight,
      float topHeight
   ) {
      this.addVertex(builder, matrixStack, this.p2f(1), scale < 0.5F ? bottomHeight : topHeight, this.p2f(1), minU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(1), scale < 0.5F ? bottomHeight : topHeight, this.p2f(9), maxU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), scale < 0.5F ? bottomHeight : topHeight, this.p2f(9), maxU, maxV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), scale < 0.5F ? bottomHeight : topHeight, this.p2f(1), minU, maxV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(1), scale < 0.5F ? bottomHeight : topHeight, this.p2f(9), minU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(4), scale < 0.5F ? bottomHeight : topHeight, this.p2f(15), maxU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(12), scale < 0.5F ? bottomHeight : topHeight, this.p2f(15), maxU, maxV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), scale < 0.5F ? bottomHeight : topHeight, this.p2f(9), minU, maxV, r, g, b, 1.0F);
   }

   private void renderSides(
      PoseStack matrixStack,
      VertexConsumer builder,
      float scale,
      float r,
      float g,
      float b,
      float minU,
      float maxU,
      float minV,
      float maxVBottom,
      float maxVTop,
      float bottomHeight,
      float topHeight,
      Direction direction
   ) {
      double[] translation = this.getRootTranslation(direction);
      matrixStack.mulPose(this.getRotationFromDirection(direction));
      matrixStack.translate(translation[0], translation[1], translation[2]);
      this.addVertex(builder, matrixStack, this.p2f(4), this.p2f(1), this.p2f(15), minU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(12), this.p2f(1), this.p2f(15), maxU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(12), bottomHeight, this.p2f(15), maxU, maxVBottom, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(4), bottomHeight, this.p2f(15), minU, maxVBottom, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(1), this.p2f(1), this.p2f(9), minU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(4), this.p2f(1), this.p2f(15), maxU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(4), bottomHeight, this.p2f(15), maxU, maxVBottom, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(1), bottomHeight, this.p2f(9), minU, maxVBottom, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(12), this.p2f(1), this.p2f(15), minU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), this.p2f(1), this.p2f(9), maxU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), bottomHeight, this.p2f(9), maxU, maxVBottom, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(12), bottomHeight, this.p2f(15), minU, maxVBottom, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(1), this.p2f(1), this.p2f(1), minU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(1), this.p2f(1), this.p2f(9), maxU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(1), bottomHeight, this.p2f(9), maxU, maxVBottom, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(1), bottomHeight, this.p2f(1), minU, maxVBottom, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), this.p2f(1), this.p2f(9), minU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), this.p2f(1), this.p2f(1), maxU, minV, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), bottomHeight, this.p2f(1), maxU, maxVBottom, r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), bottomHeight, this.p2f(9), minU, maxVBottom, r, g, b, 1.0F);
      if (!(scale < 0.5F)) {
         this.addVertex(builder, matrixStack, this.p2f(4), this.p2f(16), this.p2f(15), minU, minV, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(12), this.p2f(16), this.p2f(15), maxU, minV, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(12), topHeight, this.p2f(15), maxU, maxVTop, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(4), topHeight, this.p2f(15), minU, maxVTop, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(1), this.p2f(16), this.p2f(9), minU, minV, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(4), this.p2f(16), this.p2f(15), maxU, minV, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(4), topHeight, this.p2f(15), maxU, maxVTop, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(1), topHeight, this.p2f(9), minU, maxVTop, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(12), this.p2f(16), this.p2f(15), minU, minV, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(15), this.p2f(16), this.p2f(9), maxU, minV, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(15), topHeight, this.p2f(9), maxU, maxVTop, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(12), topHeight, this.p2f(15), minU, maxVTop, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(1), this.p2f(16), this.p2f(1), minU, minV, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(1), this.p2f(16), this.p2f(9), maxU, minV, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(1), topHeight, this.p2f(9), maxU, maxVTop, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(1), topHeight, this.p2f(1), minU, maxVTop, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(15), this.p2f(16), this.p2f(9), minU, minV, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(15), this.p2f(16), this.p2f(1), maxU, minV, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(15), topHeight, this.p2f(1), maxU, maxVTop, r, g, b, 1.0F);
         this.addVertex(builder, matrixStack, this.p2f(15), topHeight, this.p2f(9), minU, maxVTop, r, g, b, 1.0F);
      }
   }

   private void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, float r, float g, float b, float a) {
      renderer.vertex(stack.last().pose(), x, y, z).color(r, g, b, 0.5F).uv(u, v).uv2(0, 240).normal(1.0F, 0.0F, 0.0F).endVertex();
   }

   private float p2f(int pixel) {
      return 0.0625F * pixel;
   }

   private void updateIndex(int ticksExisted) {
      if (ticksExisted % 60.0F == 0.0F) {
         if (this.wait) {
            return;
         }

         this.wait = true;
         if (this.index++ == this.colors.length - 1) {
            this.index = 0;
         }
      } else {
         this.wait = false;
      }
   }

   private void updateColor(float partialTicks, CryoChamberTileEntity tileEntity) {
      if (tileEntity.getBlockState().getValue(CryoChamberBlock.CHAMBER_STATE) == CryoChamberBlock.ChamberState.RUSTY) {
         this.currentColor = new Color(139, 69, 19);
      } else {
         int nextIndex = this.index + 1;
         if (nextIndex == this.colors.length) {
            nextIndex = 0;
         }

         this.currentColor = this.getBlendedColor(this.colors[this.index], this.colors[nextIndex], partialTicks);
      }
   }

   private Color getBlendedColor(Color prev, Color next, float partialTicks) {
      float prevRed = prev.getRed() / 255.0F;
      float prevGreen = prev.getGreen() / 255.0F;
      float prevBlue = prev.getBlue() / 255.0F;
      float nextRed = next.getRed() / 255.0F;
      float nextGreen = next.getGreen() / 255.0F;
      float nextBlue = next.getBlue() / 255.0F;
      float percentage = 0.01F;
      float transitionTime = 0.90000004F;
      float red = Math.abs((nextRed - prevRed) * percentage / transitionTime * partialTicks);
      float green = Math.abs((nextGreen - prevGreen) * percentage / transitionTime * partialTicks);
      float blue = Math.abs((nextBlue - prevBlue) * percentage / transitionTime * partialTicks);
      this.currentRed = nextRed > prevRed ? this.currentRed + red : this.currentRed - red;
      this.currentGreen = nextGreen > prevGreen ? this.currentGreen + green : this.currentGreen - green;
      this.currentBlue = nextBlue > prevBlue ? this.currentBlue + blue : this.currentBlue - blue;
      this.currentRed = this.ensureRange(this.currentRed);
      this.currentGreen = this.ensureRange(this.currentGreen);
      this.currentBlue = this.ensureRange(this.currentBlue);
      return new Color(this.currentRed, this.currentGreen, this.currentBlue);
   }

   private float ensureRange(float value) {
      return Math.min(Math.max(value, 0.0F), 1.0F);
   }
}
