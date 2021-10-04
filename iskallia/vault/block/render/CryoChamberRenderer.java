package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.entity.AncientCryoChamberTileEntity;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.client.util.LightmapUtil;
import iskallia.vault.client.util.ShaderUtil;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.init.ModConfigs;
import java.awt.Color;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.lwjgl.opengl.ARBShaderObjects;

public class CryoChamberRenderer extends TileEntityRenderer<CryoChamberTileEntity> {
   public static final Minecraft mc = Minecraft.func_71410_x();
   public static final ResourceLocation INFUSED_PLAYER_SKIN = Vault.id("textures/entity/infusion_skin_white.png");
   public static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel(0.1F, true);
   private final Color[] colors = new Color[]{Color.WHITE, Color.YELLOW, Color.MAGENTA, Color.GREEN};
   private int index = 0;
   private boolean wait = false;
   private Color currentColor = Color.WHITE;
   private float currentRed = 1.0F;
   private float currentGreen = 1.0F;
   private float currentBlue = 1.0F;
   private final float colorChangeDelay = 3.0F;

   public CryoChamberRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public IVertexBuilder getPlayerVertexBuilder(ResourceLocation skinTexture, IRenderTypeBuffer buffer) {
      RenderType renderType = PLAYER_MODEL.func_228282_a_(skinTexture);
      return buffer.getBuffer(renderType);
   }

   public void render(
      CryoChamberTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay
   ) {
      if (tileEntity.isInfusing()) {
         float maxTime = ModConfigs.CRYO_CHAMBER.getInfusionTime();
         float scale = Math.min(tileEntity.getInfusionTimeRemaining() / maxTime, 0.85F);
         tileEntity.updateSkin();
         ResourceLocation skinTexture = tileEntity.getSkin().getLocationSkin();
         IVertexBuilder vertexBuilder = this.getPlayerVertexBuilder(skinTexture, buffer);
         this.renderPlayerModel(matrixStack, tileEntity, scale, 0.5F, vertexBuilder, combinedLight, combinedOverlay);
      } else if (tileEntity.isGrowingEternal()) {
         float maxTime = ModConfigs.CRYO_CHAMBER.getGrowEternalTime();
         float scale = Math.min(1.0F - tileEntity.getGrowEternalTimeRemaining() / maxTime, 0.85F);
         IVertexBuilder vertexBuilder = this.getPlayerVertexBuilder(INFUSED_PLAYER_SKIN, buffer);
         this.renderPlayerModel(matrixStack, tileEntity, scale, 0.5F, vertexBuilder, combinedLight, combinedOverlay);
      } else if (tileEntity.getEternalId() != null) {
         EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(tileEntity.getEternalId());
         if (snapshot != null && snapshot.getName() != null) {
            tileEntity.updateSkin();
            if (buffer instanceof Impl) {
               ((Impl)buffer).func_228461_a_();
            }

            if (!snapshot.isAlive()) {
               ShaderUtil.useShader(ShaderUtil.GRAYSCALE_SHADER, () -> {
                  int grayScaleFactor = ShaderUtil.getUniformLocation(ShaderUtil.GRAYSCALE_SHADER, "grayFactor");
                  ARBShaderObjects.glUniform1fARB(grayScaleFactor, 0.0F);
                  float brightness = LightmapUtil.getLightmapBrightness(combinedLight);
                  int brightnessFactor = ShaderUtil.getUniformLocation(ShaderUtil.GRAYSCALE_SHADER, "brightness");
                  ARBShaderObjects.glUniform1fARB(brightnessFactor, brightness);
               });
            }

            ResourceLocation skinTexture = tileEntity.getSkin().getLocationSkin();
            IVertexBuilder vertexBuilder = this.getPlayerVertexBuilder(skinTexture, buffer);
            this.renderPlayerModel(matrixStack, tileEntity, 0.85F, 1.0F, vertexBuilder, combinedLight, combinedOverlay);
            if (buffer instanceof Impl) {
               ((Impl)buffer).func_228461_a_();
            }

            if (!snapshot.isAlive()) {
               ShaderUtil.releaseShader();
            }
         }
      } else if (tileEntity instanceof AncientCryoChamberTileEntity) {
         tileEntity.updateSkin();
         ResourceLocation skinTexturex = tileEntity.getSkin().getLocationSkin();
         IVertexBuilder vertexBuilderx = this.getPlayerVertexBuilder(skinTexturex, buffer);
         this.renderPlayerModel(matrixStack, tileEntity, 0.85F, 1.0F, vertexBuilderx, combinedLight, combinedOverlay);
         if (buffer instanceof Impl) {
            ((Impl)buffer).func_228461_a_();
         }
      }

      this.renderArmor(matrixStack, tileEntity, buffer, combinedOverlay);
      this.renderLiquid(matrixStack, tileEntity, buffer, partialTicks);
      if (mc.field_71476_x != null && mc.field_71476_x.func_216346_c() == Type.BLOCK) {
         String eternalName = null;
         EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(tileEntity.getEternalId());
         if (snapshot != null && snapshot.getName() != null) {
            eternalName = snapshot.getName();
         }

         if (tileEntity instanceof AncientCryoChamberTileEntity) {
            eternalName = ((AncientCryoChamberTileEntity)tileEntity).getEternalName();
         }

         if (eternalName != null) {
            BlockRayTraceResult result = (BlockRayTraceResult)mc.field_71476_x;
            if (tileEntity.func_174877_v().equals(result.func_216350_a()) || tileEntity.func_174877_v().func_177984_a().equals(result.func_216350_a())) {
               this.renderLabel(
                  matrixStack,
                  buffer,
                  combinedLight,
                  new StringTextComponent(eternalName),
                  -1,
                  tileEntity.func_145831_w().func_180495_p(result.func_216350_a()).func_177229_b(CryoChamberBlock.HALF) == DoubleBlockHalf.UPPER
               );
            }
         }
      }
   }

   private void renderLabel(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, StringTextComponent text, int color, boolean topBlock) {
      FontRenderer fontRenderer = mc.field_71466_p;
      matrixStack.func_227860_a_();
      float scale = 0.02F;
      int opacity = 1711276032;
      float offset = -fontRenderer.func_238414_a_(text) / 2;
      Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
      matrixStack.func_227861_a_(0.5, 2.3F, 0.5);
      matrixStack.func_227862_a_(scale, scale, scale);
      matrixStack.func_227863_a_(mc.func_175598_ae().func_229098_b_());
      matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
      fontRenderer.func_243247_a(text, offset, 0.0F, color, false, matrix4f, buffer, true, opacity, lightLevel);
      fontRenderer.func_243247_a(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, lightLevel);
      matrixStack.func_227865_b_();
   }

   public void renderPlayerModel(
      MatrixStack matrixStack, CryoChamberTileEntity tileEntity, float scale, float alpha, IVertexBuilder vertexBuilder, int combinedLight, int combinedOverlay
   ) {
      BlockState blockState = tileEntity.func_195044_w();
      Direction direction = (Direction)blockState.func_177229_b(CryoChamberBlock.FACING);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.5, 1.3, 0.5);
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
      PLAYER_MODEL.field_178720_f.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      PLAYER_MODEL.field_78116_c.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, alpha);
      matrixStack.func_227865_b_();
   }

   public void renderArmor(MatrixStack matrixStack, CryoChamberTileEntity tileEntity, IRenderTypeBuffer buffer, int combinedOverlay) {
      if (tileEntity.getEternalId() != null) {
         EternalDataSnapshot snapshot = ClientEternalData.getSnapshot(tileEntity.getEternalId());
         if (snapshot != null) {
            BlockState blockState = tileEntity.func_195044_w();
            Direction direction = (Direction)blockState.func_177229_b(CryoChamberBlock.FACING);
            int lightLevel = this.getLightAtPos(tileEntity.func_145831_w(), tileEntity.func_174877_v().func_177984_a());

            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
               ItemStack stack = snapshot.getEquipment(slot);
               if (!stack.func_190926_b()) {
                  this.renderItem(stack, matrixStack, buffer, combinedOverlay, lightLevel, direction, slot);
               }
            }
         }
      }
   }

   private void renderItem(
      ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedOverlay, int lightLevel, Direction direction, EquipmentSlotType slot
   ) {
      matrixStack.func_227860_a_();
      double[] rootTranslation = this.getRootTranslation(direction);
      double[] itemTranslation = this.getItemTranslation(slot);
      matrixStack.func_227863_a_(this.getRotationFromDirection(direction));
      matrixStack.func_227861_a_(rootTranslation[0], rootTranslation[1], rootTranslation[2]);
      matrixStack.func_227861_a_(itemTranslation[0], itemTranslation[1], itemTranslation[2]);
      matrixStack.func_227862_a_(0.25F, 0.25F, 0.25F);
      IBakedModel ibakedmodel = mc.func_175599_af().func_184393_a(stack, null, null);
      mc.func_175599_af().func_229111_a_(stack, TransformType.GROUND, false, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
      matrixStack.func_227865_b_();
   }

   private int getLightAtPos(World world, BlockPos pos) {
      int blockLight = world.func_226658_a_(LightType.BLOCK, pos);
      int skyLight = world.func_226658_a_(LightType.SKY, pos);
      return LightTexture.func_228451_a_(blockLight, skyLight);
   }

   private Quaternion getRotationFromDirection(Direction direction) {
      switch (direction) {
         case NORTH:
         case SOUTH:
            return Vector3f.field_229181_d_.func_229187_a_(direction.func_176734_d().func_185119_l());
         default:
            return Vector3f.field_229181_d_.func_229187_a_(direction.func_185119_l());
      }
   }

   private double[] getRootTranslation(Direction direction) {
      switch (direction) {
         case SOUTH:
            return new double[]{-1.0, 0.0, -1.0};
         case WEST:
            return new double[]{-1.0, 0.0, 0.0};
         case EAST:
            return new double[]{0.0, 0.0, -1.0};
         default:
            return new double[]{0.0, 0.0, 0.0};
      }
   }

   private double[] getItemTranslation(EquipmentSlotType slot) {
      double pixel = 0.0625;
      double width = 14.0 * pixel;
      double distance = width / 6.0;
      double start = pixel * 2.0;
      switch (slot) {
         case MAINHAND:
            return new double[]{start, 1.85, 1.0};
         case HEAD:
            return new double[]{start + distance, 1.85, 1.0};
         case CHEST:
            return new double[]{start + distance * 2.0, 1.85, 1.0};
         case LEGS:
            return new double[]{start + distance * 3.0, 1.85, 1.0};
         case FEET:
            return new double[]{start + distance * 4.0, 1.85, 1.0};
         case OFFHAND:
            return new double[]{start + distance * 5.0, 1.85, 1.0};
         default:
            return new double[3];
      }
   }

   private void renderLiquid(MatrixStack matrixStack, CryoChamberTileEntity tileEntity, IRenderTypeBuffer buffer, float partialTicks) {
      if (tileEntity.getMaxCores() != 0) {
         IVertexBuilder builder = buffer.getBuffer(RenderType.func_228645_f_());
         TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.func_71410_x()
            .func_228015_a_(PlayerContainer.field_226615_c_)
            .apply(Fluids.field_204546_a.getAttributes().getStillTexture());
         BlockState blockState = tileEntity.func_195044_w();
         Direction direction = (Direction)blockState.func_177229_b(CryoChamberBlock.FACING);
         float max = tileEntity.getMaxCores();
         float difference = tileEntity.getCoreCount() - tileEntity.lastCoreCount;
         tileEntity.lastCoreCount += difference * 0.02F;
         float scale = tileEntity.lastCoreCount / max;
         this.updateIndex(mc.field_71439_g.field_70173_aa);
         this.updateColor(partialTicks, tileEntity);
         float r = this.currentColor.getRed() / 255.0F;
         float g = this.currentColor.getGreen() / 255.0F;
         float b = this.currentColor.getBlue() / 255.0F;
         float minU = sprite.func_94214_a(0.0);
         float maxU = sprite.func_94214_a(16.0);
         float minV = sprite.func_94207_b(0.0);
         float maxVBottom = sprite.func_94207_b(scale < 0.5 ? scale * 2.0F * 16.0 : 16.0);
         float maxVTop = sprite.func_94207_b(scale >= 0.5 ? (scale * 2.0F - 1.0F) * 16.0 : 0.0);
         float bottomHeight = scale < 0.5F ? scale * 2.0F : 1.0F;
         float topHeight = scale < 0.5F ? 0.0F : Math.min(scale * 2.0F, 1.9F);
         matrixStack.func_227860_a_();
         this.renderSides(matrixStack, builder, scale, r, g, b, minU, maxU, minV, maxVBottom, maxVTop, bottomHeight, topHeight, direction);
         this.renderTop(
            matrixStack,
            builder,
            scale,
            r,
            g,
            b,
            sprite.func_94209_e(),
            sprite.func_94212_f(),
            sprite.func_94206_g(),
            sprite.func_94210_h(),
            bottomHeight,
            topHeight
         );
         matrixStack.func_227865_b_();
      }
   }

   private void renderTop(
      MatrixStack matrixStack,
      IVertexBuilder builder,
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
      MatrixStack matrixStack,
      IVertexBuilder builder,
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
      matrixStack.func_227863_a_(this.getRotationFromDirection(direction));
      matrixStack.func_227861_a_(translation[0], translation[1], translation[2]);
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

   private void addVertex(IVertexBuilder renderer, MatrixStack stack, float x, float y, float z, float u, float v, float r, float g, float b, float a) {
      renderer.func_227888_a_(stack.func_227866_c_().func_227870_a_(), x, y, z)
         .func_227885_a_(r, g, b, 0.5F)
         .func_225583_a_(u, v)
         .func_225587_b_(0, 240)
         .func_225584_a_(1.0F, 0.0F, 0.0F)
         .func_181675_d();
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
      if (tileEntity.func_195044_w().func_177229_b(CryoChamberBlock.CHAMBER_STATE) == CryoChamberBlock.ChamberState.RUSTY) {
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
