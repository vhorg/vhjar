package iskallia.vault.task.renderer.context;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.renderer.VaultArmorRenderProperties;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.task.renderer.Vec2d;
import iskallia.vault.util.EntityHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent.DrawScreenEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber({Dist.CLIENT})
public class RendererContext {
   private PoseStack matrices;
   private float tickDelta;
   private final BufferSource bufferSource;
   protected final Font font;
   private final Stack<Vec2d> mouse = new Stack<>();
   private static final List<RendererContext.TooltipRender> tooltips = new ArrayList<>();

   public RendererContext(PoseStack matrices, float tickDelta, BufferSource bufferSource, Font font) {
      this.matrices = matrices;
      this.tickDelta = tickDelta;
      this.bufferSource = bufferSource;
      this.font = font;
      this.mouse.push(Vec2d.ZERO);
   }

   public PoseStack getMatrices() {
      return this.matrices;
   }

   public BufferSource getBufferSource() {
      return this.bufferSource;
   }

   public long getTick() {
      return ClientScheduler.INSTANCE.getTick();
   }

   public double getPartialTick() {
      return (float)this.getTick() + this.getTickDelta();
   }

   public float getTickDelta() {
      return this.tickDelta;
   }

   public Vec2d getMouse() {
      return this.mouse.peek();
   }

   public void setMatrices(PoseStack matrices) {
      this.matrices = matrices;
   }

   public void setTickDelta(float tickDelta) {
      this.tickDelta = tickDelta;
   }

   public void setMouse(Vec2d mouse) {
      this.mouse.pop();
      this.mouse.push(mouse);
   }

   public void push() {
      this.matrices.pushPose();
      this.mouse.push(this.getMouse());
   }

   public void pop() {
      this.matrices.popPose();
      this.mouse.pop();
   }

   public void pushMouse(Vec2d mouse) {
      this.mouse.push(mouse);
   }

   public void popMouse() {
      this.mouse.pop();
   }

   public void translate(double x, double y, double z) {
      this.matrices.translate(x, y, z);
      Vec2d current = this.mouse.isEmpty() ? Vec2d.ZERO : this.mouse.pop();
      this.mouse.push(current.subtract(x, y));
   }

   public void translate(Vec3 vector) {
      this.translate(vector.x, vector.y, vector.z);
   }

   public void translate(Vector3d vector) {
      this.translate(vector.x, vector.y, vector.z);
   }

   public void scale(double x, double y, double z) {
      this.matrices.scale((float)x, (float)y, (float)z);
   }

   public void scale(Vec3 vector) {
      this.scale(vector.x, vector.y, vector.z);
   }

   public void scale(Vector3d vector) {
      this.scale(vector.x, vector.y, vector.z);
   }

   public int setShaderTexture(ResourceLocation texture) {
      int previous = RenderSystem.getShaderTexture(0);
      RenderSystem.setShaderTexture(0, texture);
      return previous;
   }

   public int setShaderTexture(int texture) {
      int previous = RenderSystem.getShaderTexture(0);
      RenderSystem.setShaderTexture(0, texture);
      return previous;
   }

   public void setShaderColor(float alpha, float red, float green, float blue) {
      RenderSystem.setShaderColor(red, green, blue, alpha);
   }

   public void setShaderColor(int alpha, int red, int green, int blue) {
      this.setShaderColor(alpha / 255.0F, red / 255.0F, green / 255.0F, blue / 255.0F);
   }

   public void setShaderColor(int color) {
      this.setShaderColor(color >> 24 & 0xFF, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF);
   }

   public void setShaderColor(ChatFormatting formatting) {
      this.setShaderColor(formatting.getColor() == null ? 16777215 : formatting.getColor());
   }

   public void drawColoredRect(float x, float y, float width, float height, int color) {
      RenderSystem.enableBlend();
      ScreenDrawHelper.draw(
         Mode.QUADS,
         DefaultVertexFormat.POSITION_COLOR,
         buf -> ScreenDrawHelper.rect(buf, this.matrices).at(x, y).dim(width, height).color(color).drawColored()
      );
   }

   public void blit(ResourceLocation id, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
      int previous = this.setShaderTexture(id);
      this.blit(x, y, u, v, width, height, textureWidth, textureHeight);
      this.setShaderTexture(previous);
   }

   public void blit(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
      this.push();
      this.translate(x, y, 0.0);
      GuiComponent.blit(this.matrices, 0, 0, u, v, width, height, textureWidth, textureHeight);
      this.pop();
   }

   public void drawNineSlice(NineSlice.TextureRegion textureRegion, int x, int y, int width, int height) {
      this.push();
      this.translate(x, y, 0.0);
      textureRegion.blit(this.matrices, 0, 0, 0, width, height);
      this.pop();
   }

   public void fill(Vec2d position, int width, int height, int color) {
      this.fill((int)position.getX(), (int)position.getY(), width, height, color);
   }

   public void fill(int x, int y, int width, int height, int color) {
      this.push();
      this.translate(x, y, 0.0);
      GuiComponent.fill(this.matrices, 0, 0, width, height, color);
      this.pop();
   }

   public void drawOutline(int x, int y, int width, int height, int size, int color) {
      this.fill(x, y, x + width, Math.min(size, height), color);
      this.fill(x + width - Math.min(size, width), y, Math.min(size, width), height, color);
      this.fill(x, y, Math.min(size, width), height, color);
      this.fill(x, height - Math.min(size, height), width, Math.min(size, height), color);
   }

   public void renderText(Component text, float x, float y, boolean centeredX, boolean centeredY) {
      this.renderText(text, x, y, 9000, centeredX, centeredY, -1, false);
   }

   public void renderText(Component text, float x, float y, boolean centeredX, boolean centeredY, int color, boolean shadow) {
      this.renderText(text, x, y, 9000, centeredX, centeredY, color, shadow);
   }

   public void renderText(Component text, float x, float y, int maxWidth, boolean centeredX, boolean centeredY, int color, boolean shadow) {
      this.push();
      this.translate(x, y, 0.0);
      List<FormattedCharSequence> formatted = this.font.split(text, maxWidth);
      int lineY = 0;

      for (FormattedCharSequence sequence : formatted) {
         float offsetX = centeredX ? (float)(-this.font.width(sequence) / 2.0) : 0.0F;
         float offsetY = centeredY ? (float)(-9 / 2.0) : 0.0F;
         this.font.draw(this.matrices, sequence, offsetX, offsetY + lineY, color);
         if (shadow) {
            this.font.drawShadow(this.matrices, sequence, offsetX, offsetY + lineY, -16777216);
         }

         lineY += 9;
      }

      RenderSystem.enableDepthTest();
      this.bufferSource.endBatch();
      this.pop();
   }

   public void renderTextRight(Component text, float x, float y, boolean centeredY) {
      this.push();
      this.translate(x, y, 0.0);
      FormattedCharSequence formatted = (FormattedCharSequence)this.font.split(text, 9000).get(0);
      float offsetX = -this.font.width(formatted);
      float offsetY = centeredY ? (float)(-9 / 2.0) : 0.0F;
      this.font.drawInBatch(formatted, offsetX, offsetY, 16777215, false, this.matrices.last().pose(), this.bufferSource, true, 0, 15728880);
      RenderSystem.enableDepthTest();
      this.bufferSource.endBatch();
      this.pop();
   }

   public void renderEntity(LivingEntity entity, int x, int y, float scale) {
      this.push();
      this.translate(x, y, 0.0);
      EntityHelper.renderEntityInUI(entity, this.matrices, this.bufferSource, 0, 0, scale, (int)this.getMouse().getX(), (int)this.getMouse().getY());
      this.pop();
   }

   public void renderInWorldStack(ItemStack stack, int x, int y, float scale, int packedLight, int packedOverlay) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
      int previous = this.setShaderTexture(TextureAtlas.LOCATION_BLOCKS);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      ItemRenderer itemRenderer = minecraft.getItemRenderer();
      Lighting.setupFor3DItems();
      this.push();
      this.translate(x + 0.0F, y + 0.0F, 0.0);
      this.matrices.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      this.scale(16.0F * scale, 16.0F * scale, 16.0);
      itemRenderer.renderStatic(stack, TransformType.GUI, packedLight, packedOverlay, this.matrices, this.bufferSource, 0);
      this.bufferSource.endBatch();
      this.pop();
      this.setShaderTexture(previous);
   }

   public void renderStack(ItemStack stack, int x, int y, float scale, boolean withSlotTexture, boolean withTooltip) {
      Minecraft minecraft = Minecraft.getInstance();
      if (withSlotTexture) {
         int previous = this.setShaderTexture(VaultMod.id("textures/gui/screen/inset_item_slot_background.png"));
         this.push();
         this.translate(x, y, 0.0);
         this.scale(scale, scale, 1.0);
         this.blit(0, 0, 0, 0, 18, 18, 18, 18);
         this.pop();
         this.setShaderTexture(previous);
      }

      minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
      int previous = this.setShaderTexture(TextureAtlas.LOCATION_BLOCKS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      ItemRenderer itemRenderer = minecraft.getItemRenderer();
      BakedModel model = itemRenderer.getModel(stack, null, null, 0);
      if (!model.usesBlockLight()) {
         Lighting.setupForFlatItems();
      } else {
         RenderSystem.setShaderLights(new Vector3f(-0.2F, -1.0F, 0.7F), new Vector3f(-0.2F, 1.0F, -0.7F));
      }

      this.push();
      float offset = withSlotTexture ? 1.0F * scale : 0.0F;
      this.translate(x + offset, y + offset, 0.0);
      this.translate(8.0F * scale, 8.0F * scale, 100.0);
      this.scale(1.0, -1.0, 1.0);
      this.scale(16.0F * scale, 16.0F * scale, 16.0);
      itemRenderer.render(
         stack, TransformType.GUI, false, this.matrices, this.bufferSource, LightmapHelper.getPackedFullbrightCoords(), OverlayTexture.NO_OVERLAY, model
      );
      this.bufferSource.endBatch();
      this.pop();
      RenderSystem.enableDepthTest();
      if (!model.usesBlockLight()) {
         Lighting.setupFor3DItems();
      }

      this.setShaderTexture(previous);
      if (!stack.isEmpty() && withTooltip && this.isItemHovered(x, y, scale)) {
         this.renderTooltip(List.of(stack.getHoverName()));
      }
   }

   private boolean isItemHovered(int x, int y, float scale) {
      double mouseX = this.getMouse().getX();
      double mouseY = this.getMouse().getY();
      return mouseX >= x && mouseX <= x + 17.99 * scale && mouseY >= y && mouseY <= y + 17.99 * scale;
   }

   public void renderTooltip(List<Component> lines) {
      int width = 0;
      int height = lines.size() == 1 ? -2 : 0;

      for (Component line : lines) {
         int k = this.font.width(line);
         if (k > width) {
            width = k;
         }

         height += 9;
      }

      int x = (int)(this.getMouse().getX() - width - 14.0);
      if (x <= 0) {
         x += width + 24;
      }

      int y = (int)(this.getMouse().getY() - height + 6.0);
      tooltips.add(new RendererContext.TooltipRender(this, lines, x, y, width, height));
   }

   private static void fillGradient(Matrix4f pose, BufferBuilder builder, int colorA, int colorB, int x1, int y1, int x2, int y2) {
      float f = (colorA >> 24 & 0xFF) / 255.0F;
      float f1 = (colorA >> 16 & 0xFF) / 255.0F;
      float f2 = (colorA >> 8 & 0xFF) / 255.0F;
      float f3 = (colorA & 0xFF) / 255.0F;
      float f4 = (colorB >> 24 & 0xFF) / 255.0F;
      float f5 = (colorB >> 16 & 0xFF) / 255.0F;
      float f6 = (colorB >> 8 & 0xFF) / 255.0F;
      float f7 = (colorB & 0xFF) / 255.0F;
      builder.vertex(pose, x2, y1, 200.0F).color(f1, f2, f3, f).endVertex();
      builder.vertex(pose, x1, y1, 200.0F).color(f1, f2, f3, f).endVertex();
      builder.vertex(pose, x1, y2, 200.0F).color(f5, f6, f7, f4).endVertex();
      builder.vertex(pose, x2, y2, 200.0F).color(f5, f6, f7, f4).endVertex();
   }

   public void renderArmorModel(ItemStack stack, int x, int y) {
      Item associatedItem = stack.getItem();
      VaultGearData gearData = VaultGearData.read(stack);
      if (associatedItem instanceof VaultArmorItem) {
         this.push();
         this.translate(x, y, 350.0);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         this.push();
         this.scale(16.0, 16.0, 16.0);
         double rotation = -90.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0);
         this.matrices.mulPose(Quaternion.fromXYZ(-0.3925F, (float)rotation, 0.0F));
         Optional<DynamicModelRegistry<?>> modelRegistry = ModDynamicModels.REGISTRIES.getAssociatedRegistry(stack.getItem());
         ArmorPieceModel armorPiece = null;
         if (modelRegistry.isPresent()) {
            Optional<ResourceLocation> firstValue = gearData.getFirstValue(ModGearAttributes.GEAR_MODEL);
            if (firstValue.isPresent()) {
               Optional<?> model = modelRegistry.get().get(firstValue.get());
               if (model.isPresent() && model.get() instanceof ArmorPieceModel armorPieceModel) {
                  armorPiece = armorPieceModel;
               }
            }

            if (armorPiece != null) {
               VaultArmorItem vaultArmorItem = VaultArmorItem.forSlot(armorPiece.getEquipmentSlot());
               ArmorLayers.BaseLayer baseLayer = VaultArmorRenderProperties.BAKED_LAYERS.get(armorPiece.getId());
               String baseTexture = vaultArmorItem.getArmorTexture(stack, null, armorPiece.getEquipmentSlot(), null);
               String overlayTexture = vaultArmorItem.getArmorTexture(stack, null, armorPiece.getEquipmentSlot(), "overlay");
               this.translate(0.0, -2.0, 0.0);
               this.scale(1.5, 1.5, 1.5);
               EquipmentSlot intendedSlot = vaultArmorItem.getGearType(stack).getEquipmentSlot();
               if (intendedSlot == EquipmentSlot.HEAD) {
                  this.translate(0.0, 0.25, 0.0);
               } else if (intendedSlot == EquipmentSlot.LEGS) {
                  this.translate(0.0, -0.75, 0.0);
               } else if (intendedSlot == EquipmentSlot.FEET) {
                  this.translate(0.0, -1.25, 0.0);
               }

               if (baseTexture != null) {
                  VertexConsumer baseVertexConsumer = this.bufferSource.getBuffer(baseLayer.renderType(new ResourceLocation(baseTexture)));
                  baseLayer.renderToBuffer(this.matrices, baseVertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
               }

               if (overlayTexture != null) {
                  VertexConsumer overlayVertexConsumer = this.bufferSource.getBuffer(baseLayer.renderType(new ResourceLocation(overlayTexture)));
                  baseLayer.renderToBuffer(this.matrices, overlayVertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
               }
            }
         }

         this.bufferSource.endBatch();
         RenderSystem.enableDepthTest();
         this.pop();
         this.pop();
      }
   }

   public void renderScrollbarWithHandle(int x, int y, int width, int height, float scrollValue, boolean enabled) {
      this.push();
      this.translate(x, y, 0.0);
      ScreenTextures.INSET_GREY_BACKGROUND.blit(this.matrices, 0, 0, 0, width, height);
      this.renderScrollbarHandle(x, y, height, scrollValue, enabled);
      this.pop();
   }

   public void renderScrollbarHandle(int x, int y, int height, float scrollValue, boolean enabled) {
      this.push();
      TextureAtlasRegion handle = enabled ? ScreenTextures.SCROLLBAR_HANDLE : ScreenTextures.SCROLLBAR_HANDLE_DISABLED;
      double minHeight = handle.height() / 2.0;
      double maxHeight = height - handle.height() / 2.0;
      double scrollHandleY = y + (maxHeight - minHeight) * scrollValue;
      scrollHandleY = Math.max(1.0, scrollHandleY);
      scrollHandleY = Math.min(maxHeight, scrollHandleY);
      this.translate(1.0, scrollHandleY, 0.0);
      handle.blit(this.matrices, 0, 0, 0, handle.width(), handle.height());
      this.pop();
   }

   @SubscribeEvent
   public static void onRenderScreenLast(Post event) {
      int mouseX = event.getMouseX();
      int mouseY = event.getMouseY();

      for (RendererContext.TooltipRender tooltip : tooltips) {
         renderTooltip(tooltip, mouseX, mouseY);
      }

      tooltips.clear();
   }

   private static void renderTooltip(RendererContext.TooltipRender tooltip, int mouseX, int mouseY) {
      Screen screen = Minecraft.getInstance().screen;
      List<FormattedCharSequence> components = new ArrayList<>();

      for (Component component : tooltip.components) {
         components.add(component.getVisualOrderText());
      }

      if (screen != null) {
         screen.renderTooltip(tooltip.context.getMatrices(), components, mouseX, mouseY, Minecraft.getInstance().font);
      }
   }

   public static class TooltipRender {
      int x;
      int y;
      int width;
      int height;
      List<Component> components;
      RendererContext context;

      public TooltipRender(RendererContext context, List<Component> components, int x, int y, int width, int height) {
         this.context = context;
         this.components = components;
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }
}
