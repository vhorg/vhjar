package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextAlign;
import iskallia.vault.client.gui.framework.text.TextWrap;
import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class EnchantBookElement<E extends EnchantBookElement<E>> extends ElasticContainerElement<E> {
   private static final ResourceLocation VANILLA_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   protected int imageWidth = 176;
   protected int imageHeight = 166;
   private ResourceLocation texture = VANILLA_BOOK_LOCATION;
   private final Random random = new Random();
   private BookModel bookModel = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   private boolean last;
   private LabelTextStyle topTextstyle;
   private LabelTextStyle bottomTextStyle;
   protected Supplier<Integer> levelCost;
   protected Supplier<Boolean> shouldOpen;

   public EnchantBookElement(IPosition position, ISize size, Supplier<Integer> levelCost, Supplier<Boolean> shouldOpen) {
      super(Spatials.positionXYZ(position).size(size));
      this.levelCost = levelCost;
      this.shouldOpen = shouldOpen;
      this.topTextstyle = LabelTextStyle.border4(TextColor.fromRgb(-14664184)).left().build();
      this.bottomTextStyle = LabelTextStyle.border8(TextColor.fromRgb(-14664184)).left().build();
   }

   public EnchantBookElement<E> withCustomTexture(ResourceLocation texture) {
      this.texture = texture;
      return this;
   }

   @Override
   public void render(IElementRenderer renderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
      int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
      int posX = this.getWorldSpatial().x();
      int posY = this.getWorldSpatial().y();
      Lighting.setupForFlatItems();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = (screenWidth - this.imageWidth) / 2;
      int j = (screenHeight - this.imageHeight) / 2;
      int k = (int)Minecraft.getInstance().getWindow().getGuiScale();
      RenderSystem.viewport(posX * k - 100 * k, -posY * k + screenHeight * k - 145 * k, 320 * k, 240 * k);
      Matrix4f matrix4f = Matrix4f.createTranslateMatrix(-0.34F, 0.23F, 0.0F);
      matrix4f.multiply(Matrix4f.perspective(90.0, 1.3333334F, 9.0F, 80.0F));
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(matrix4f);
      poseStack.pushPose();
      Pose posestack$pose = poseStack.last();
      posestack$pose.pose().setIdentity();
      posestack$pose.normal().setIdentity();
      poseStack.translate(0.0, 0.0, 1984.0);
      float f = 4.0F;
      poseStack.scale(f, f, f);
      poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      poseStack.mulPose(Vector3f.XP.rotationDegrees(20.0F));
      float f1 = Mth.lerp(partialTick, this.oOpen, this.open);
      poseStack.translate((1.0F - f1) * 0.2F, (1.0F - f1) * 0.1F, (1.0F - f1) * 0.25F);
      float f2 = -(1.0F - f1) * 90.0F - 90.0F;
      poseStack.mulPose(Vector3f.YP.rotationDegrees(f2));
      poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      float f3 = Mth.lerp(partialTick, this.oFlip, this.flip) + 0.25F;
      float f4 = Mth.lerp(partialTick, this.oFlip, this.flip) + 0.75F;
      f3 = (f3 - Mth.fastFloor(f3)) * 1.6F - 0.3F;
      f4 = (f4 - Mth.fastFloor(f4)) * 1.6F - 0.3F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      this.bookModel.setupAnim(0.0F, f3, f4, f1);
      BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(this.bookModel.renderType(this.texture));
      this.bookModel.renderToBuffer(poseStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      multibuffersource$buffersource.endBatch();
      poseStack.popPose();
      RenderSystem.viewport(0, 0, Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
      RenderSystem.restoreProjectionMatrix();
      Integer cost = this.levelCost.get();
      if (this.shouldOpen.get() && cost != null && cost != 0) {
         this.topTextstyle
            .textBorder()
            .render(
               renderer,
               poseStack,
               new TextComponent(String.valueOf(cost)).withStyle(Style.EMPTY.withColor(-8455136)),
               TextWrap.wrap(),
               TextAlign.CENTER,
               posX - 45,
               posY - 10,
               300,
               100
            );
         float scl = 0.45F;
         poseStack.pushPose();
         poseStack.scale(scl, scl, scl);
         this.bottomTextStyle
            .textBorder()
            .render(
               renderer,
               poseStack,
               new TextComponent("Level Cost").withStyle(Style.EMPTY.withColor(-8455136)),
               TextWrap.wrap(),
               TextAlign.CENTER,
               (int)((posX - 44) / scl),
               (int)((posY - 1) / scl),
               300,
               (int)(100.0F / scl)
            );
         poseStack.popPose();
      }
   }

   public void tickBook() {
      boolean shouldOpen = this.shouldOpen.get();
      if (this.last != shouldOpen) {
         this.last = shouldOpen;

         do {
            this.flipT = this.flipT + (this.random.nextInt(4) - this.random.nextInt(4));
         } while (this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
      }

      this.time++;
      this.oFlip = this.flip;
      this.oOpen = this.open;
      if (shouldOpen) {
         this.open += 0.2F;
      } else {
         this.open -= 0.2F;
      }

      this.open = Mth.clamp(this.open, 0.0F, 1.0F);
      float f1 = (this.flipT - this.flip) * 0.4F;
      float f = 0.2F;
      f1 = Mth.clamp(f1, -0.2F, 0.2F);
      this.flipA = this.flipA + (f1 - this.flipA) * 0.9F;
      this.flip = this.flip + this.flipA;
   }
}
