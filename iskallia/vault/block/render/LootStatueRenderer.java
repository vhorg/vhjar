package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.block.entity.TrophyStatueTileEntity;
import iskallia.vault.block.model.OmegaStatueModel;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.util.LightmapUtil;
import iskallia.vault.client.util.ShaderUtil;
import iskallia.vault.client.util.VBOUtil;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.StatueType;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.lwjgl.opengl.ARBShaderObjects;

public class LootStatueRenderer extends TileEntityRenderer<LootStatueTileEntity> {
   protected static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel(0.0F, false);
   protected static final OmegaStatueModel OMEGA_STATUE_MODEL = new OmegaStatueModel();
   protected static Map<Integer, VertexBuffer> lightVBOMap = new HashMap<>();
   private final Minecraft mc = Minecraft.func_71410_x();

   public LootStatueRenderer(TileEntityRendererDispatcher rendererDispatcher) {
      super(rendererDispatcher);
   }

   public void render(
      LootStatueTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay
   ) {
      BlockState blockState = tileEntity.func_195044_w();
      Direction direction = (Direction)blockState.func_177229_b(LootStatueBlock.FACING);
      Minecraft mc = Minecraft.func_71410_x();
      if (mc.field_71439_g != null) {
         if (tileEntity.getStatueType() == StatueType.OMEGA && tileEntity.isMaster()) {
            this.renderOmegaStatueCrystals(tileEntity, matrixStack, buffer, combinedLight, combinedOverlay);
            if (tileEntity.getChipCount() > 0) {
               ClientPlayerEntity player = mc.field_71439_g;
               int lightLevel = this.getLightAtPos(tileEntity.func_145831_w(), tileEntity.func_174877_v().func_177984_a());
               this.renderItemWithLabel(
                  new ItemStack(ModItems.ACCELERATION_CHIP),
                  this.getTranslation(direction),
                  Vector3f.field_229181_d_.func_229187_a_((direction != Direction.EAST && direction != Direction.WEST ? 180 : 270) - player.field_70177_z),
                  matrixStack,
                  buffer,
                  combinedOverlay,
                  lightLevel,
                  direction,
                  new StringTextComponent("" + tileEntity.getChipCount()),
                  -1
               );
            }
         } else if (tileEntity.getStatueType() == StatueType.OMEGA_VARIANT) {
            if (tileEntity.getChipCount() > 0) {
               ClientPlayerEntity player = mc.field_71439_g;
               int lightLevel = this.getLightAtPos(tileEntity.func_145831_w(), tileEntity.func_174877_v().func_177984_a());
               this.renderItemWithLabel(
                  new ItemStack(ModItems.ACCELERATION_CHIP),
                  this.getVariantTranslation(direction),
                  Vector3f.field_229181_d_.func_229187_a_((direction != Direction.EAST && direction != Direction.WEST ? 180 : 270) - player.field_70177_z),
                  matrixStack,
                  buffer,
                  combinedOverlay,
                  lightLevel,
                  direction,
                  new StringTextComponent(String.valueOf(tileEntity.getChipCount())),
                  -1
               );
            }

            ItemStack loot = tileEntity.getLootItem();
            if (!loot.func_190926_b()) {
               matrixStack.func_227860_a_();
               matrixStack.func_227861_a_(0.5, 0.4, 0.5);
               matrixStack.func_227861_a_(direction.func_82601_c() * -0.2, 0.0, direction.func_82599_e() * -0.2);
               matrixStack.func_227862_a_(1.6F, 1.6F, 1.6F);
               IBakedModel ibakedmodel = mc.func_175599_af().func_184393_a(loot, null, null);
               mc.func_175599_af().func_229111_a_(loot, TransformType.GROUND, false, matrixStack, buffer, combinedLight, combinedOverlay, ibakedmodel);
               matrixStack.func_227865_b_();
            }
         }

         String latestNickname = tileEntity.getSkin().getLatestNickname();
         if (!StringUtils.func_151246_b(latestNickname)) {
            this.drawPlayerModel(matrixStack, buffer, tileEntity, combinedLight, combinedOverlay);
            StatueType statueType = tileEntity.getStatueType();
            if (statueType == StatueType.GIFT_MEGA) {
               this.drawStatueBowHat(matrixStack, buffer, direction, combinedLight, combinedOverlay);
            }

            if (statueType == StatueType.TROPHY) {
               this.drawRecordDisplay(matrixStack, buffer, direction, tileEntity, combinedLight, combinedOverlay);
            }

            if (statueType == StatueType.OMEGA_VARIANT) {
               this.drawStatueNameplate(matrixStack, buffer, latestNickname, direction, tileEntity, combinedLight, combinedOverlay);
            }

            if (mc.field_71476_x != null && mc.field_71476_x.func_216346_c() == Type.BLOCK) {
               BlockRayTraceResult result = (BlockRayTraceResult)mc.field_71476_x;
               if (tileEntity.func_174877_v().equals(result.func_216350_a())) {
                  ITextComponent text = new StringTextComponent(latestNickname).func_240699_a_(TextFormatting.WHITE);
                  if (statueType.hasLimitedItems() && tileEntity.getItemsRemaining() <= 0) {
                     text = new StringTextComponent("☠ ").func_240699_a_(TextFormatting.RED).func_230529_a_(text);
                  }

                  this.renderLabel(matrixStack, buffer, combinedLight, text, -1);
               }
            }
         }
      }
   }

   private void drawStatueNameplate(
      MatrixStack matrixStack,
      IRenderTypeBuffer buffer,
      String latestNickname,
      Direction direction,
      LootStatueTileEntity tileEntity,
      int combinedLight,
      int combinedOverlay
   ) {
      IReorderingProcessor text = new StringTextComponent(latestNickname).func_240699_a_(TextFormatting.BLACK).func_241878_f();
      FontRenderer fr = this.field_228858_b_.func_147548_a();
      int xOffset = fr.func_243245_a(text);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.5, 0.35, 0.5);
      matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(direction.func_185119_l() + 180.0F));
      matrixStack.func_227861_a_(0.0, 0.0, 0.51);
      matrixStack.func_227862_a_(0.01F, -0.01F, 0.01F);
      fr.func_238416_a_(text, -xOffset / 2.0F, 0.0F, -16777216, false, matrixStack.func_227866_c_().func_227870_a_(), buffer, false, 0, combinedLight);
      matrixStack.func_227865_b_();
   }

   private void drawRecordDisplay(
      MatrixStack matrixStack, IRenderTypeBuffer buffer, Direction direction, LootStatueTileEntity tileEntity, int combinedLight, int combinedOverlay
   ) {
      if (tileEntity instanceof TrophyStatueTileEntity) {
         TrophyStatueTileEntity trophyTile = (TrophyStatueTileEntity)tileEntity;
         WeekKey week = trophyTile.getWeek();
         PlayerVaultStatsData.PlayerRecordEntry recordEntry = trophyTile.getRecordEntry();
         FontRenderer fr = this.field_228858_b_.func_147548_a();
         LocalDateTime ldt = LocalDateTime.now();
         ldt = ldt.with(IsoFields.WEEK_BASED_YEAR, (long)week.getYear())
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, (long)week.getWeek())
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
         String from = ldt.getDayOfMonth() + "." + ldt.getMonthValue() + "." + ldt.getYear() + " -";
         ldt = ldt.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
         String to = ldt.getDayOfMonth() + "." + ldt.getMonthValue() + "." + ldt.getYear();
         IReorderingProcessor fromCmp = new StringTextComponent(from).func_241878_f();
         IReorderingProcessor toCmp = new StringTextComponent(to).func_241878_f();
         IReorderingProcessor timeStr = new StringTextComponent(UIHelper.formatTimeString(recordEntry.getTickCount())).func_241878_f();
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.5, 0.5, 0.5);
         matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(direction.func_185119_l() + 180.0F));
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.0, 0.24, 0.22);
         matrixStack.func_227862_a_(0.0055F, -0.0055F, 0.0055F);
         int xOffset = fr.func_243245_a(fromCmp);
         fr.func_238416_a_(fromCmp, -xOffset / 2.0F, 0.0F, -16777216, false, matrixStack.func_227866_c_().func_227870_a_(), buffer, false, 0, combinedLight);
         xOffset = fr.func_243245_a(toCmp);
         fr.func_238416_a_(toCmp, -xOffset / 2.0F, 10.0F, -16777216, false, matrixStack.func_227866_c_().func_227870_a_(), buffer, false, 0, combinedLight);
         matrixStack.func_227865_b_();
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.0, 0.1, 0.19);
         matrixStack.func_227862_a_(0.008F, -0.008F, 0.008F);
         xOffset = fr.func_243245_a(timeStr);
         fr.func_238416_a_(timeStr, -xOffset / 2.0F, 0.0F, -16777216, false, matrixStack.func_227866_c_().func_227870_a_(), buffer, false, 0, combinedLight);
         matrixStack.func_227865_b_();
         matrixStack.func_227865_b_();
      }
   }

   private void drawStatueBowHat(MatrixStack matrixStack, IRenderTypeBuffer buffer, Direction direction, int combinedLight, int combinedOverlay) {
      float hatScale = 3.0F;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.5, 1.1, 0.5);
      matrixStack.func_227862_a_(hatScale, hatScale, hatScale);
      matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(direction.func_185119_l() + 180.0F));
      ItemStack stack = new ItemStack(ModBlocks.BOW_HAT);
      IBakedModel ibakedmodel = this.mc.func_175599_af().func_184393_a(stack, null, null);
      this.mc.func_175599_af().func_229111_a_(stack, TransformType.GROUND, true, matrixStack, buffer, combinedLight, combinedOverlay, ibakedmodel);
      matrixStack.func_227865_b_();
   }

   private void drawPlayerModel(MatrixStack matrixStack, IRenderTypeBuffer buffer, LootStatueTileEntity tileEntity, int combinedLight, int combinedOverlay) {
      BlockState blockState = tileEntity.func_195044_w();
      Direction direction = (Direction)blockState.func_177229_b(LootStatueBlock.FACING);
      StatueType statueType = tileEntity.getStatueType();
      ResourceLocation skinLocation = tileEntity.getSkin().getLocationSkin();
      RenderType renderType = PLAYER_MODEL.func_228282_a_(skinLocation);
      IVertexBuilder vertexBuilder = buffer.getBuffer(renderType);
      float scale = 0.4F;
      float headScale = 1.75F;
      float yOffset = statueType.getPlayerRenderYOffset();
      float statueOffset = 0.0F;
      if (statueType.doGrayscaleShader()) {
         ShaderUtil.useShader(ShaderUtil.GRAYSCALE_SHADER, () -> {
            float factor = (float)tileEntity.getItemsRemaining() / tileEntity.getTotalItems();
            int grayScaleFactor = ShaderUtil.getUniformLocation(ShaderUtil.GRAYSCALE_SHADER, "grayFactor");
            ARBShaderObjects.glUniform1fARB(grayScaleFactor, factor);
            float brightness = LightmapUtil.getLightmapBrightness(combinedLight);
            int brightnessFactor = ShaderUtil.getUniformLocation(ShaderUtil.GRAYSCALE_SHADER, "brightness");
            ARBShaderObjects.glUniform1fARB(brightnessFactor, brightness);
         });
      }

      matrixStack.func_227860_a_();
      if (statueType == StatueType.OMEGA) {
         float playerScale = tileEntity.getPlayerScale();
         matrixStack.func_227861_a_(0.0, 1.0F + playerScale, 0.0);
         scale += playerScale;
      }

      if (statueType == StatueType.OMEGA_VARIANT) {
         matrixStack.func_227861_a_(0.0, 1.55F, 0.0);
         scale = 1.3F;
         headScale = 1.0F;
         statueOffset = 0.2F;
      }

      if (statueType == StatueType.TROPHY) {
         scale = (float)(scale - 0.04);
      }

      matrixStack.func_227861_a_(0.5, yOffset, 0.5);
      matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(direction.func_185119_l() + 180.0F));
      matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(180.0F));
      matrixStack.func_227861_a_(0.0, 0.0, statueOffset);
      matrixStack.func_227862_a_(scale, scale, scale);
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
      matrixStack.func_227862_a_(headScale, headScale, headScale);
      PLAYER_MODEL.field_178720_f.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      PLAYER_MODEL.field_78116_c.func_228309_a_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.func_227865_b_();
      if (buffer instanceof Impl) {
         ((Impl)buffer).func_228462_a_(renderType);
      }

      if (statueType.doGrayscaleShader()) {
         ShaderUtil.releaseShader();
      }
   }

   private void renderOmegaStatueCrystals(
      LootStatueTileEntity tileEntity, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay
   ) {
      RenderType omegaType = OMEGA_STATUE_MODEL.func_228282_a_(Vault.id("textures/block/mega_statue3.png"));
      VertexBuffer vbo = lightVBOMap.computeIfAbsent(
         combinedLight, lightLvl -> VBOUtil.batch(OMEGA_STATUE_MODEL, omegaType, lightLvl, OverlayTexture.field_229196_a_)
      );
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.5, 1.5, 0.5);
      BlockState blockState = tileEntity.func_195044_w();
      Direction direction = (Direction)blockState.func_177229_b(LootStatueBlock.FACING);
      matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(direction.func_185119_l() + 180.0F));
      matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(180.0F));
      omegaType.func_228547_a_();
      vbo.func_177359_a();
      omegaType.func_228663_p_().func_227892_a_(0L);
      vbo.func_227874_a_(matrixStack.func_227866_c_().func_227870_a_(), omegaType.func_228664_q_());
      omegaType.func_228663_p_().func_227895_d_();
      VertexBuffer.func_177361_b();
      omegaType.func_228549_b_();
      matrixStack.func_227865_b_();
   }

   private void renderLabel(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, ITextComponent text, int color) {
      FontRenderer fontRenderer = this.mc.field_71466_p;
      matrixStack.func_227860_a_();
      float scale = 0.02F;
      int opacity = 1711276032;
      float offset = -fontRenderer.func_238414_a_(text) / 2;
      Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
      matrixStack.func_227861_a_(0.5, 1.7F, 0.5);
      matrixStack.func_227862_a_(scale, scale, scale);
      matrixStack.func_227863_a_(this.mc.func_175598_ae().func_229098_b_());
      matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
      fontRenderer.func_243247_a(text, offset, 0.0F, color, false, matrix4f, buffer, true, opacity, lightLevel);
      fontRenderer.func_243247_a(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, lightLevel);
      matrixStack.func_227865_b_();
   }

   private void renderItemWithLabel(
      ItemStack stack,
      double[] translation,
      Quaternion rotation,
      MatrixStack matrixStack,
      IRenderTypeBuffer buffer,
      int combinedOverlay,
      int lightLevel,
      Direction direction,
      StringTextComponent text,
      int color
   ) {
      matrixStack.func_227860_a_();
      matrixStack.func_227863_a_(
         Vector3f.field_229180_c_.func_229187_a_(direction.func_185119_l() + (direction != Direction.NORTH && direction != Direction.EAST ? 180 : 0))
      );
      matrixStack.func_227861_a_(translation[0], translation[1], translation[2]);
      matrixStack.func_227863_a_(rotation);
      matrixStack.func_227862_a_(0.5F, 0.5F, 0.5F);
      IBakedModel ibakedmodel = this.mc.func_175599_af().func_184393_a(stack, null, null);
      this.mc.func_175599_af().func_229111_a_(stack, TransformType.GROUND, true, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
      float scale = -0.025F;
      float offset = -Minecraft.func_71410_x().field_71466_p.func_238414_a_(text) / 2;
      matrixStack.func_227861_a_(0.0, 0.75, 0.0);
      matrixStack.func_227862_a_(scale, scale, scale);
      Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
      int opacity = 1711276032;
      Minecraft.func_71410_x().field_71466_p.func_243247_a(text, offset, 0.0F, color, false, matrix4f, buffer, true, opacity, lightLevel);
      Minecraft.func_71410_x().field_71466_p.func_243247_a(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, lightLevel);
      matrixStack.func_227865_b_();
   }

   private int getLightAtPos(World world, BlockPos pos) {
      int blockLight = world.func_226658_a_(LightType.BLOCK, pos);
      int skyLight = world.func_226658_a_(LightType.SKY, pos);
      return LightTexture.func_228451_a_(blockLight, skyLight);
   }

   private double[] getTranslation(Direction direction) {
      switch (direction) {
         case NORTH:
            return new double[]{-0.5, 1.0, -1.5};
         case EAST:
            return new double[]{-0.5, 1.0, -0.5};
         case WEST:
            return new double[]{-0.5, 1.0, 1.5};
         default:
            return new double[]{-0.5, 1.0, 0.5};
      }
   }

   private double[] getVariantTranslation(Direction direction) {
      switch (direction) {
         case NORTH:
            return new double[]{-0.5, 1.1, -0.65};
         case EAST:
            return new double[]{-0.5, 1.1, 0.35};
         case WEST:
            return new double[]{-0.5, 1.1, 0.65};
         default:
            return new double[]{-0.5, 1.1, -0.35};
      }
   }
}
