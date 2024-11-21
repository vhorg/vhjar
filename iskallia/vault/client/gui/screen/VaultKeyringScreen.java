package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.inventory.VaultKeyringContainer;
import iskallia.vault.container.slot.TreasureKeySlot;
import iskallia.vault.init.ModItems;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VaultKeyringScreen extends AbstractElementContainerScreen<VaultKeyringContainer> {
   public static Map<Item, TextureAtlasRegion> KEY_MAP = new HashMap<Item, TextureAtlasRegion>() {
      {
         this.put(ModItems.ISKALLIUM_KEY, ScreenTextures.KEY_ISKALLIUM);
         this.put(ModItems.GORGINITE_KEY, ScreenTextures.KEY_GORGINITE);
         this.put(ModItems.SPARKLETINE_KEY, ScreenTextures.KEY_SPARKLETINE);
         this.put(ModItems.ASHIUM_KEY, ScreenTextures.KEY_ASHIUM);
         this.put(ModItems.BOMIGNITE_KEY, ScreenTextures.KEY_BOMIGNITE);
         this.put(ModItems.TUBIUM_KEY, ScreenTextures.KEY_TUBIUM);
         this.put(ModItems.UPALINE_KEY, ScreenTextures.KEY_UPALINE);
         this.put(ModItems.PETZANITE_KEY, ScreenTextures.KEY_PETZANITE);
         this.put(ModItems.XENIUM_KEY, ScreenTextures.KEY_XENIUM);
      }
   };

   public VaultKeyringScreen(VaultKeyringContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.setGuiSize(Spatials.size(176, 164));
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(Spatials.positionXY(8, 71), inventoryName, LabelTextStyle.defaultStyle())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         new VaultKeyringScreen.TreasureKeyBackgroundElement(container, this.getTooltipRenderer())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
   }

   @Override
   protected boolean renderHoveredSlotTooltips(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
      if (this.hoveredSlot != null && this.hoveredSlot instanceof TreasureKeySlot keySlot && !this.hoveredSlot.hasItem()) {
         this.renderTooltip(poseStack, keySlot.getKeyStack(), mouseX, mouseY);
      }

      return super.renderHoveredSlotTooltips(poseStack, mouseX, mouseY);
   }

   public static class TreasureKeyBackgroundElement extends AbstractSpatialElement<VaultKeyringScreen.TreasureKeyBackgroundElement> implements IRenderedElement {
      private final ITooltipRenderer tooltipRenderer;
      private final VaultKeyringContainer container;

      public TreasureKeyBackgroundElement(VaultKeyringContainer container, ITooltipRenderer tooltipRenderer) {
         super(Spatials.zero());
         this.tooltipRenderer = tooltipRenderer;
         this.container = container;
      }

      @Override
      public void setVisible(boolean visible) {
      }

      @Override
      public boolean isVisible() {
         return true;
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         TextureManager mgr = Minecraft.getInstance().getTextureManager();
         mgr.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

         for (TreasureKeySlot keySlot : this.container.getKeySlots()) {
            if (!keySlot.hasItem()) {
               ItemStack emptyStack = keySlot.getKeyStack();
               TextureAtlasRegion tar = VaultKeyringScreen.KEY_MAP.get(emptyStack.getItem());
               if (tar != null) {
                  ISpatial pos = Spatials.positionXY(this.worldSpatial.x() + keySlot.x + 1, this.worldSpatial.y() + keySlot.y + 1);
                  renderer.render(tar, poseStack, pos);
               }
            }
         }
      }
   }
}
