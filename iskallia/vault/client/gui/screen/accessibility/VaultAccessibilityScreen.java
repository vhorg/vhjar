package iskallia.vault.client.gui.screen.accessibility;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ColorSquareElement;
import iskallia.vault.client.gui.framework.element.NineSliceButtonElement;
import iskallia.vault.client.gui.framework.element.SliderElement;
import iskallia.vault.client.gui.framework.element.ToggleButtonElement;
import iskallia.vault.client.gui.framework.element.spi.ILayoutStrategy;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.render.IVaultOptions;
import iskallia.vault.util.ColorOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class VaultAccessibilityScreen extends AbstractElementScreen {
   private ColorOption selectedColorOption;
   private SliderElement redSliderElement;
   private SliderElement greenSliderElement;
   private SliderElement blueSliderElement;
   private NineSliceButtonElement<?> defaultColorButton;
   private ColorSquareElement colorSquareElement;

   public VaultAccessibilityScreen() {
      super(new TextComponent("Vault Hunters Accessibility Options"), ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      Window window = Minecraft.getInstance().getWindow();
      this.setGuiSize(Spatials.size(window.getGuiScaledWidth(), window.getGuiScaledHeight()));
      int padding = 4;
      int widgetWidth = 150;
      int widgetHeight = 20;
      int startX = 0;
      int startY = 0;
      IVaultOptions options = (IVaultOptions)Minecraft.getInstance().options;
      this.selectedColorOption = options.getChestHunterSpec();
      this.addElement(
         new ToggleButtonElement(
               Spatials.positionXY(startX, startY).size(widgetWidth, widgetHeight),
               new TextComponent("Vault Potion Effects"),
               () -> options.doVanillaPotionDamageEffects() ? "OFF" : "ON",
               () -> options.setVanillaPotionDamageEffects(!options.doVanillaPotionDamageEffects())
            )
            .layout(this.translateWorldSpatial())
      );
      int x = startX + widgetWidth + padding;
      this.addElement(
         new ToggleButtonElement(
               Spatials.positionXY(x, startY).size(widgetWidth, widgetHeight),
               new TextComponent("Custom Hunter Colors"),
               () -> options.isHunterCustomColorsEnabled() ? "ON" : "OFF",
               () -> options.setHunterCustomColorsEnabled(!options.isHunterCustomColorsEnabled())
            )
            .layout(this.translateWorldSpatial())
      );
      int y = startY + widgetHeight + padding;
      ColorOption.HunterSpec[] specs = ColorOption.HunterSpec.values();
      this.addElement(
         new ToggleButtonElement(
               Spatials.positionXY(startX, y).size(widgetWidth, widgetHeight),
               new TextComponent("Hunter Target"),
               () -> this.selectedColorOption.getHunterSpec().name(),
               () -> {
                  int index = this.selectedColorOption.getHunterSpec().ordinal();
                  int next = index + 1 >= specs.length ? 0 : index + 1;
                  options.setColorOption(specs[index], this.selectedColorOption);
                  this.selectedColorOption = options.getBySpec(specs[next]);
                  this.updateSliderValues();
               }
            )
            .layout(this.translateWorldSpatial())
      );
      x = startX + widgetWidth + padding;
      this.colorSquareElement = new ColorSquareElement(
         Spatials.positionXY(x, y).size(widgetWidth, widgetHeight * 3 + padding * 2), () -> this.selectedColorOption.getColor()
      );
      y += widgetHeight + padding;
      this.redSliderElement = new SliderElement(
         Spatials.positionXY(startX, y).size(widgetWidth, widgetHeight),
         () -> new TextComponent("Red"),
         () -> this.selectedColorOption.getRed(),
         value -> this.selectedColorOption.setRed(value)
      );
      y += widgetHeight + padding;
      this.greenSliderElement = new SliderElement(
         Spatials.positionXY(startX, y).size(widgetWidth, widgetHeight),
         () -> new TextComponent("Green"),
         () -> this.selectedColorOption.getGreen(),
         value -> this.selectedColorOption.setGreen(value)
      );
      y += widgetHeight + padding;
      this.blueSliderElement = new SliderElement(
         Spatials.positionXY(startX, y).size(widgetWidth, widgetHeight),
         () -> new TextComponent("Blue"),
         () -> this.selectedColorOption.getBlue(),
         value -> this.selectedColorOption.setBlue(value)
      );
      x = startX + widgetWidth + padding;
      this.defaultColorButton = new NineSliceButtonElement(
            Spatials.positionXY(x, y).size(widgetWidth, widgetHeight), ScreenTextures.BUTTON_EMPTY_TEXTURES, () -> {
               this.selectedColorOption = options.resetColorOption(this.selectedColorOption.getHunterSpec());
               this.updateSliderValues();
            }
         )
         .label(() -> new TextComponent("Default Color"), LabelTextStyle.shadow().center());
      y += widgetHeight + padding;
      this.addElement(
         new ToggleButtonElement(
               Spatials.positionXY(startX, y).size(widgetWidth, widgetHeight),
               new TextComponent("Ability Scrolling"),
               () -> options.isAbilityScrollingEnabled() ? "ON" : "OFF",
               () -> options.setAbilityScrollingEnabled(!options.isAbilityScrollingEnabled())
            )
            .layout(this.translateWorldSpatial())
      );
      x = startX + widgetWidth + padding;
      this.addElement(
         new ToggleButtonElement(
               Spatials.positionXY(x, y).size(widgetWidth, widgetHeight),
               new TextComponent("Cooldown GUI"),
               () -> options.getCooldownGuiOption().getSerializedNameUpper(),
               options::cycleCooldownGuiOption
            )
            .layout(this.translateWorldSpatial())
      );
      y += widgetHeight + padding;
      this.addElement(
         new ToggleButtonElement(
               Spatials.positionXY(startX, y).size(widgetWidth, widgetHeight),
               new TextComponent("Show Point Messages"),
               () -> options.showPointMessages() ? "ON" : "OFF",
               () -> options.setShowPointMessages(!options.showPointMessages())
            )
            .layout(this.translateWorldSpatial())
      );
      x = startX + widgetWidth + padding;
      this.addElement(
         new ToggleButtonElement(
               Spatials.positionXY(x, y).size(widgetWidth, widgetHeight),
               new TextComponent("Show Rarity Names"),
               () -> options.showRarityNames() ? "ON" : "OFF",
               () -> options.setShowRarityNames(!options.showRarityNames())
            )
            .layout(this.translateWorldSpatial())
      );
      this.addElement(this.colorSquareElement).layout(this.translateWorldSpatial());
      this.addElement(this.redSliderElement).layout(this.translateWorldSpatial());
      this.addElement(this.greenSliderElement).layout(this.translateWorldSpatial());
      this.addElement(this.blueSliderElement).layout(this.translateWorldSpatial());
      this.addElement(this.defaultColorButton.layout(this.translateWorldSpatial()));
   }

   private void updateSliderValues() {
      this.redSliderElement.setValue(this.selectedColorOption.getRed());
      this.greenSliderElement.setValue(this.selectedColorOption.getGreen());
      this.blueSliderElement.setValue(this.selectedColorOption.getBlue());
   }

   @NotNull
   private ILayoutStrategy translateWorldSpatial() {
      return (screen, gui, parent, world) -> {
         Window window = Minecraft.getInstance().getWindow();
         world.translateXY(window.getGuiScaledWidth() / 2 - 150 - 4, 36);
      };
   }

   @Override
   public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.renderDirtBackground(0);
      drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 16777215);
      super.render(poseStack, mouseX, mouseY, partialTick);
   }

   @Override
   protected void renderBackgroundFill(@NotNull PoseStack poseStack) {
   }

   public void renderDirtBackground(int pVOffset) {
      super.renderDirtBackground(pVOffset);
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      bufferbuilder.vertex(0.0, this.height - 32.0, 0.0).uv(0.0F, this.height / 32.0F + pVOffset).color(32, 32, 32, 255).endVertex();
      bufferbuilder.vertex(this.width, this.height - 32.0, 0.0).uv(this.width / 32.0F, this.height / 32.0F + pVOffset).color(32, 32, 32, 255).endVertex();
      bufferbuilder.vertex(this.width, 32.0, 0.0).uv(this.width / 32.0F, pVOffset).color(32, 32, 32, 255).endVertex();
      bufferbuilder.vertex(0.0, 32.0, 0.0).uv(0.0F, pVOffset).color(32, 32, 32, 255).endVertex();
      tesselator.end();
      RenderSystem.depthFunc(515);
      RenderSystem.disableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
      RenderSystem.disableTexture();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      bufferbuilder.vertex(0.0, 36.0, 0.0).color(0, 0, 0, 0).endVertex();
      bufferbuilder.vertex(this.width, 36.0, 0.0).color(0, 0, 0, 0).endVertex();
      bufferbuilder.vertex(this.width, 32.0, 0.0).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex(0.0, 32.0, 0.0).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex(0.0, this.height - 32, 0.0).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex(this.width, this.height - 32, 0.0).color(0, 0, 0, 255).endVertex();
      bufferbuilder.vertex(this.width, this.height - 36, 0.0).color(0, 0, 0, 0).endVertex();
      bufferbuilder.vertex(0.0, this.height - 36, 0.0).color(0, 0, 0, 0).endVertex();
      tesselator.end();
   }
}
