package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.antique.Antique;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.container.inventory.AntiqueCollectorBookContainer;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.world.data.PlayerStoredAntiquesData;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class AntiqueCollectorBookScreen extends AbstractElementContainerScreen<AntiqueCollectorBookContainer> {
   private final ButtonElement<?> btnPrev;
   private final ButtonElement<?> btnNext;

   public AntiqueCollectorBookScreen(AntiqueCollectorBookContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.setGuiSize(Spatials.size(288, 246));
      this.addElement(
         (TextureAtlasElement)new TextureAtlasElement(this.getGuiSpatial(), ScreenTextures.ANTIQUE_COLLECTOR_BOOK_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(Spatials.positionXY(63, 153), inventoryName, LabelTextStyle.defaultStyle())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
            this.btnPrev = new ButtonElement(
               Spatials.positionXY(25, 123),
               ScreenTextures.ANTIQUE_COLLECTOR_BOOK_NAV_LEFT,
               () -> this.setActivePage(((AntiqueCollectorBookContainer)this.getMenu()).getActivePage() - 1)
            )
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
      this.addElement(
            this.btnNext = new ButtonElement(
               Spatials.positionXY(245, 123),
               ScreenTextures.ANTIQUE_COLLECTOR_BOOK_NAV_RIGHT,
               () -> this.setActivePage(((AntiqueCollectorBookContainer)this.getMenu()).getActivePage() + 1)
            )
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
      this.addElement(
         new AntiqueCollectorBookScreen.AntiqueTitleElement(
               ((AntiqueCollectorBookContainer)this.getMenu()).getStoredAntiques(), ((AntiqueCollectorBookContainer)this.getMenu()).getAntiqueSlots()
            )
            .layout((screen, gui, parent, world) -> world.positionXY(gui))
      );
      this.setActivePage(0);
   }

   private void setActivePage(int activePage) {
      ((AntiqueCollectorBookContainer)this.getMenu()).setActivePage(activePage);
      boolean hasPreviousPage = activePage > 0;
      boolean hasNextPage = activePage < ((AntiqueCollectorBookContainer)this.getMenu()).getPages() - 1;
      this.btnPrev.setEnabled(hasPreviousPage);
      this.btnPrev.setVisible(hasPreviousPage);
      this.btnNext.setEnabled(hasNextPage);
      this.btnNext.setVisible(hasNextPage);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      Key key = InputConstants.getKey(keyCode, scanCode);
      if (keyCode != 256 && !Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
         return super.keyPressed(keyCode, scanCode, modifiers);
      } else {
         this.onClose();
         return true;
      }
   }

   public static class AntiqueTitleElement extends AbstractSpatialElement<AntiqueCollectorBookScreen.AntiqueTitleElement> implements IRenderedElement {
      private final PlayerStoredAntiquesData.StoredAntiques storedAntiques;
      private final List<AntiqueCollectorBookContainer.AntiqueCollectorBookSlot> antiqueSlots;
      private boolean visible = true;

      public AntiqueTitleElement(
         PlayerStoredAntiquesData.StoredAntiques storedAntiques, List<AntiqueCollectorBookContainer.AntiqueCollectorBookSlot> antiqueSlots
      ) {
         super(Spatials.zero());
         this.storedAntiques = storedAntiques;
         this.antiqueSlots = antiqueSlots;
      }

      @Override
      public void setVisible(boolean visible) {
         this.visible = visible;
      }

      @Override
      public boolean isVisible() {
         return this.visible;
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         TextureManager mgr = Minecraft.getInstance().getTextureManager();
         LabelTextStyle textStyle = LabelTextStyle.defaultStyle().center().wrap().build();
         int fontHeight = textStyle.getLabelHeight(new TextComponent("A"), 1) / 2;
         int textWidth = 30;
         int xOffset = (textWidth - 16) / 2;
         int yOffset = 4;

         for (AntiqueCollectorBookContainer.AntiqueCollectorBookSlot slot : this.antiqueSlots) {
            if (slot.isActive()) {
               boolean hasFoundAntique = this.storedAntiques.containsKey(slot.getAntique().getRegistryName());
               ResourceLocation antiqueTexture = ModDynamicModels.Antiques.getItemTextureId(slot.getAntique());
               if (!slot.hasItem() && hasFoundAntique) {
                  TextureAtlasSprite tas = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(antiqueTexture);
                  mgr.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
                  RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  float tasPxDim = (tas.getU1() - tas.getU0()) / 16.0F;
                  ScreenDrawHelper.draw(
                     Mode.QUADS,
                     DefaultVertexFormat.POSITION_TEX,
                     buf -> ScreenDrawHelper.rect(buf, poseStack)
                        .at(this.worldSpatial.x() + slot.x + 3, this.worldSpatial.y() + slot.y + 3)
                        .zLevel(this.worldSpatial.z() + 1)
                        .dim(10.0F, 10.0F)
                        .tex(tas.getU0() + tasPxDim * 3.0F, tas.getV0() + tasPxDim * 3.0F, tasPxDim * 10.0F, tasPxDim * 10.0F)
                        .drawGrayscale(0.9F, 0.7F)
                  );
               }

               Component text = Optional.of(slot.getAntique())
                  .map(Antique::getConfig)
                  .map(cfg -> cfg.getInfo().getName())
                  .<Component>map(TextComponent::new)
                  .orElse(new TextComponent(slot.getAntique().getRegistryName().getPath()));
               Component var19 = new TextComponent("").append(text).withStyle(ChatFormatting.GRAY);
               int lines = textStyle.calculateLines(var19, textWidth * 2);
               poseStack.pushPose();
               poseStack.translate(
                  slot.x + this.worldSpatial.x() - xOffset, slot.y + this.worldSpatial.y() - yOffset - lines * fontHeight, this.worldSpatial.z() + 1
               );
               poseStack.scale(0.5F, 0.5F, 0.5F);
               textStyle.textBorder().render(renderer, poseStack, var19, textStyle.textWrap(), textStyle.textAlign(), 0, 0, 0, textWidth * 2);
               poseStack.popPose();
            }
         }
      }
   }
}
