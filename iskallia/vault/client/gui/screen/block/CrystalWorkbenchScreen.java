package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ClickableItemSlotElement;
import iskallia.vault.client.gui.framework.element.CrystalWorkbenchButtonElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.ToolItemSlotElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderFunction;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.CrystalWorkbenchContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.crystal.recipe.AnvilExecutor;
import iskallia.vault.network.message.CrystalWorkbenchMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;
import org.jetbrains.annotations.NotNull;

public class CrystalWorkbenchScreen extends AbstractElementContainerScreen<CrystalWorkbenchContainer> {
   private final Inventory playerInventory;
   ToolItemSlotElement<?> stackElement;
   CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement<?, ?> ingredientsElement;
   NineSliceElement<?> jewelsBackgroundElement;
   TooltipContainerElement tooltipContainerElement;
   boolean skipRelease = false;

   public CrystalWorkbenchScreen(CrystalWorkbenchContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      this.setGuiSize(Spatials.size(300, 172));
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).translateX(50).translateY(100).height(90).width(176))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      LabelElement<?> titleElement = this.addElement(
         new LabelElement(
               Spatials.positionXY(
                  139 - Minecraft.getInstance().font.width(((CrystalWorkbenchContainer)this.getMenu()).getEntity().getDisplayName().copy()) / 2, -24
               ),
               ((CrystalWorkbenchContainer)this.getMenu()).getEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.left()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      if (container.getEntity() != null) {
         this.stackElement = new ToolItemSlotElement(Spatials.positionXY(122, 28), () -> container.getEntity().getInput().getItem(0), () -> false, 32, 32)
            .setLabelStackCount()
            .layout((screen, gui, parent, world) -> world.translateXY(gui));
         this.addElement(this.stackElement);
      }

      this.addElement(
         this.ingredientsElement = new CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement(
               Spatials.positionXY(-12, -2).height(92),
               5,
               new CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.SelectorModel<CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.ItemSelectorEntry>(
                  
               ) {
                  @Override
                  public List<CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.ItemSelectorEntry> getEntries() {
                     return ((CrystalWorkbenchContainer)CrystalWorkbenchScreen.this.getMenu())
                        .getEntity()
                        .getIngredients()
                        .getContents()
                        .stream()
                        .map(CrystalWorkbenchScreen.ItemSelectorWithTooltipEntry::new)
                        .collect(Collectors.toList());
                  }
               }
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         this.jewelsBackgroundElement = new NineSliceElement(this.ingredientsElement.getWorldSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout(
               (screen, gui, parent, world) -> world.translateXY(gui)
                  .translateX(-5)
                  .translateY(-5)
                  .translateZ(-10)
                  .width(this.ingredientsElement.width() + 10)
                  .height(this.ingredientsElement.height() + 10)
            )
      );
      Slot slot = ((CrystalWorkbenchContainer)this.getMenu()).getSlot(36);
      IMutableSpatial btnPosition = Spatials.positionXY(slot.x - 1, slot.y - 1).translateX(20);
      CrystalWorkbenchButtonElement<?> button = new CrystalWorkbenchButtonElement(
            btnPosition, () -> ModNetwork.CHANNEL.sendToServer(new CrystalWorkbenchMessage()), this
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
      button.setDisabled(() -> {
         ItemStack input = container.getEntity().getInput().getItem(0);
         if (input.isEmpty()) {
            return true;
         } else {
            AnvilExecutor.Result clientExecutor = container.getEntity().getClientExecutor();
            return clientExecutor == null ? true : ItemStack.matches(input, clientExecutor.getOutput());
         }
      });
      this.addElement(button);
      this.tooltipContainerElement = this.addElement(
         new TooltipContainerElement(Spatials.positionXY(181, -2).height(92).width(107), container.getEntity().getInput().getItem(0))
            .layout((screen, gui, parent, world) -> world.translateXYZ(gui))
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.tooltipContainerElement.getWorldSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout(
               (screen, gui, parent, world) -> world.translateXY(gui)
                  .translateX(-5)
                  .translateY(-5)
                  .translateZ(-10)
                  .width(this.tooltipContainerElement.width() + 10)
                  .height(this.tooltipContainerElement.height() + 10)
            )
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.stackElement.getWorldSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout(
               (screen, gui, parent, world) -> world.translateXY(gui)
                  .translateZ(-1)
                  .translateX(-5)
                  .translateY(-5)
                  .width(this.stackElement.width() + 10)
                  .height(this.stackElement.height() + 10)
            )
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(titleElement.getWorldSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout(
               (screen, gui, parent, world) -> world.translateXY(gui)
                  .translateZ(-1)
                  .translateX(-6)
                  .translateY(-6)
                  .translateZ(-10)
                  .width(titleElement.width() + 10)
                  .height(titleElement.height() + 10)
            )
      );
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXY(slot.x - 1, slot.y - 1).height(18).width(38), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).translateZ(-1).translateX(-5).translateY(-5).translateZ(-10).width(48).height(28))
      );
      Slot firstUniqueSlot = ((CrystalWorkbenchContainer)this.getMenu()).getSlot(97);
      this.addElement(
         (NineSliceElement)new NineSliceElement(
               Spatials.positionXYZ(firstUniqueSlot.x - 6, firstUniqueSlot.y - 6, -1).height(28).width(64), ScreenTextures.DEFAULT_WINDOW_BACKGROUND
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
   }

   @Override
   protected void init() {
      super.init();
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         ((CrystalWorkbenchContainer)this.menu).getEntity().onOpen(player);
      }
   }

   @Override
   public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(poseStack, mouseX, mouseY, partialTick);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      return super.mouseClicked(pMouseX, pMouseY, pButton);
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      if (this.skipRelease || this.jewelsBackgroundElement.contains(pMouseX, pMouseY)) {
         this.skipRelease = false;
         this.skipNextRelease = true;
         this.isQuickCrafting = false;
      }

      return super.mouseReleased(pMouseX, pMouseY, pButton);
   }

   public void mouseClicked(int buttonIndex, int slotClicked) {
      Key mouseKey = Type.MOUSE.getOrCreate(buttonIndex);
      Slot slot = ((CrystalWorkbenchContainer)this.menu).getSlot(slotClicked);
      this.skipRelease = false;
      int l = slot.index;
      if (l != -1 && !this.isQuickCrafting) {
         if (((CrystalWorkbenchContainer)this.menu).getCarried().isEmpty()) {
            if (this.minecraft != null && this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
               this.slotClicked(slot, l, buttonIndex, ClickType.CLONE);
            } else {
               boolean flag2 = l != -999
                  && (
                     InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340)
                        || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344)
                  );
               ClickType clicktype = ClickType.PICKUP;
               if (flag2) {
                  clicktype = ClickType.QUICK_MOVE;
               } else if (l == -999) {
                  clicktype = ClickType.THROW;
               }

               this.slotClicked(slot, l, buttonIndex, clicktype);
            }
         } else if (this.minecraft == null || !this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
            boolean flag2 = l != -999
               && (
                  InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340)
                     || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344)
               );
            ClickType clicktype = ClickType.PICKUP;
            if (flag2) {
               clicktype = ClickType.QUICK_MOVE;
            }

            this.slotClicked(slot, l, buttonIndex, clicktype);
         }

         this.skipRelease = true;
      }
   }

   protected void containerTick() {
      super.containerTick();
      if (((CrystalWorkbenchContainer)this.menu).getEntity() != null) {
         ItemStack output = ((CrystalWorkbenchContainer)this.menu).getEntity().getClientExecutor().getOutput();
         this.stackElement.setItemStack(() -> output);
         if (!output.isEmpty()) {
            this.stackElement
               .tooltip(
                  Tooltips.shift(
                     Tooltips.multi(() -> this.stackElement.getDisplayStack().getTooltipLines(Minecraft.getInstance().player, Default.NORMAL)),
                     Tooltips.multi(() -> this.stackElement.getDisplayStack().getTooltipLines(Minecraft.getInstance().player, Default.ADVANCED))
                  )
               );
         } else {
            this.stackElement.tooltip(ITooltipRenderFunction.NONE);
         }

         this.ingredientsElement.refreshElements((CrystalWorkbenchContainer)this.getMenu());
         this.tooltipContainerElement.refresh(output);
      }
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

   private static class ItemSelectorWithTooltipEntry extends CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.ItemSelectorEntry {
      public ItemSelectorWithTooltipEntry(ItemStack displayStack) {
         super(displayStack, false);
      }

      @Override
      public void adjustSlot(ClickableItemSlotElement<?> slot) {
         slot.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            if (!this.getDisplayStack().isEmpty()) {
               tooltipRenderer.renderTooltip(poseStack, this.getDisplayStack(), mouseX, mouseY, TooltipDirection.RIGHT);
            }

            return true;
         });
         slot.setLabelStackCount();
      }
   }

   public class MouseClickRunnable implements Runnable {
      int type = 0;
      int slot = 0;

      public MouseClickRunnable(int s) {
         this.slot = s;
      }

      public void setType(int type) {
         this.type = type;
      }

      @Override
      public void run() {
         CrystalWorkbenchScreen.this.mouseClicked(this.type, this.slot);
      }
   }

   public class ScrollableClickableItemStackSelectorElement<E extends CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement<E, S>, S extends CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.ItemSelectorEntry>
      extends VerticalScrollClipContainer<E> {
      private final int slotColumns;
      protected final CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.SelectorModel<S> selectorModel;
      protected final TextureAtlasRegion slotTexture;
      protected final TextureAtlasRegion disabledSlotTexture;
      private final CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement<E, S>.SelectorContainer<?> elementCt;

      public ScrollableClickableItemStackSelectorElement(
         ISpatial spatial, int slotColumns, CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.SelectorModel<S> selectorModel
      ) {
         this(spatial, slotColumns, selectorModel, ScreenTextures.INSET_ITEM_SLOT_BACKGROUND, ScreenTextures.INSET_DISABLED_ITEM_SLOT_BACKGROUND);
      }

      public ScrollableClickableItemStackSelectorElement(
         ISpatial spatial,
         int slotColumns,
         CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.SelectorModel<S> selectorModel,
         TextureAtlasRegion slotTexture,
         TextureAtlasRegion disabledSlotTexture
      ) {
         super(Spatials.copy(spatial).width(slotColumns * slotTexture.width() + 17));
         this.slotColumns = slotColumns;
         this.selectorModel = selectorModel;
         this.slotTexture = slotTexture;
         this.disabledSlotTexture = disabledSlotTexture;
         this.addElement(this.elementCt = new CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.SelectorContainer(spatial.width()));
      }

      public void refreshElements(CrystalWorkbenchContainer container) {
         this.elementCt.refresh(container);
      }

      protected CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.SelectorModel<S> getSelectorModel() {
         return this.selectorModel;
      }

      protected List<ClickableItemSlotElement<?>> getSelectorElements() {
         return Collections.unmodifiableList(this.elementCt.slots);
      }

      protected ClickableItemSlotElement<?> makeElementSlot(
         final int index,
         ISpatial spatial,
         Supplier<ItemStack> itemStack,
         TextureAtlasRegion slotTexture,
         TextureAtlasRegion disabledSlotTexture,
         Supplier<Boolean> disabled
      ) {
         return new ClickableItemSlotElement(spatial, itemStack, disabled, slotTexture, disabledSlotTexture) {
            @Override
            public boolean containsMouse(double x, double y) {
               return !CrystalWorkbenchScreen.this.ingredientsElement.contains(x, y)
                  ? false
                  : x < this.right() && x >= this.left() && y >= this.top() && y < this.bottom();
            }

            @Override
            public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
               for (IRenderedElement element : this.elementStore.getRenderedElementList()) {
                  if (element.isVisible()) {
                     element.render(renderer, poseStack, mouseX, mouseY, partialTick);
                  }
               }

               if (this.isDisabled()) {
                  renderer.render(this.disabledSlotTexture, poseStack, this.worldSpatial);
               } else {
                  renderer.render(this.slotTexture, poseStack, this.worldSpatial);
               }

               RenderSystem.enableDepthTest();
               if (!this.getDisplayStack().isEmpty() && index >= ((CrystalWorkbenchContainer)CrystalWorkbenchScreen.this.getMenu()).getFirstCursedIngredient()) {
                  renderSlotHighlight(poseStack, this.worldSpatial.x() + 1, this.worldSpatial.y() + 1, 20, -1670497536);
               }

               this.renderItemStack(this.getDisplayStack(), this.worldSpatial.x() + 1, this.worldSpatial.y() + 1, this.isDisabled());
               this.renderLabel(poseStack);
               if (this.containsMouse(mouseX, mouseY)) {
                  renderSlotHighlight(poseStack, this.worldSpatial.x() + 1, this.worldSpatial.y() + 1, 20, -2130706433);
               }

               if (!this.getDisplayStack().isEmpty() && index >= ((CrystalWorkbenchContainer)CrystalWorkbenchScreen.this.getMenu()).getFirstCursedIngredient()) {
                  poseStack.pushPose();
                  poseStack.translate(0.0, 0.0, 400.0);
                  CrystalWorkbenchScreen.this.font.draw(poseStack, "☠", this.worldSpatial.x() + 9, this.worldSpatial.y() + 9, -16777216);
                  poseStack.popPose();
               }
            }
         };
      }

      public static class ItemSelectorEntry {
         private final ItemStack displayStack;
         private final boolean isDisabled;

         public ItemSelectorEntry(ItemStack displayStack, boolean isDisabled) {
            this.displayStack = displayStack;
            this.isDisabled = isDisabled;
         }

         public ItemStack getDisplayStack() {
            return this.displayStack;
         }

         public boolean isDisabled() {
            return this.isDisabled;
         }

         public void adjustSlot(ClickableItemSlotElement<?> slot) {
         }
      }

      private class SelectorContainer<T extends CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement<E, S>.SelectorContainer<T>>
         extends ElasticContainerElement<T> {
         private final List<ClickableItemSlotElement<?>> slots = new ArrayList<>();

         private SelectorContainer(int inheritedWidth) {
            super(Spatials.positionXY(0, 0).width(inheritedWidth));
            this.buildElements();
         }

         public void refresh(CrystalWorkbenchContainer container) {
            for (int i = 0; i < this.slots.size(); i++) {
               int finalI = i;
               this.slots.get(i).setItemStack(() -> container.getEntity().getIngredients().getItem(finalI));
               if (!this.slots.get(finalI).getDisplayStack().isEmpty()) {
                  this.slots
                     .get(i)
                     .tooltip(
                        this.slots.get(finalI).getDisplayStack().isEmpty()
                           ? ITooltipRenderFunction.NONE
                           : Tooltips.shift(
                              Tooltips.multi(() -> this.slots.get(finalI).getDisplayStack().getTooltipLines(Minecraft.getInstance().player, Default.NORMAL)),
                              Tooltips.multi(() -> this.slots.get(finalI).getDisplayStack().getTooltipLines(Minecraft.getInstance().player, Default.ADVANCED))
                           )
                     );
               } else {
                  this.slots.get(i).tooltip(ITooltipRenderFunction.NONE);
               }
            }
         }

         private void buildElements() {
            this.slots.clear();
            List<S> entries = ScrollableClickableItemStackSelectorElement.this.selectorModel.getEntries();

            for (int i = 0; i < entries.size(); i++) {
               S entry = entries.get(i);
               int column = i % ScrollableClickableItemStackSelectorElement.this.slotColumns;
               int row = i / ScrollableClickableItemStackSelectorElement.this.slotColumns;
               ItemStack stack = entry.getDisplayStack();
               boolean disabled = entry.isDisabled();
               ClickableItemSlotElement<?> clickableSlot = ScrollableClickableItemStackSelectorElement.this.makeElementSlot(
                  i,
                  Spatials.positionXY(0, 0)
                     .translateX(column * ScrollableClickableItemStackSelectorElement.this.slotTexture.width())
                     .translateY(row * ScrollableClickableItemStackSelectorElement.this.slotTexture.height()),
                  () -> stack,
                  ScrollableClickableItemStackSelectorElement.this.slotTexture,
                  ScrollableClickableItemStackSelectorElement.this.disabledSlotTexture,
                  () -> disabled
               );
               clickableSlot.whenClicked(CrystalWorkbenchScreen.this.new MouseClickRunnable(37 + i));
               entry.adjustSlot(clickableSlot);
               this.addElement((T)clickableSlot);
               this.slots.add(clickableSlot);
            }
         }
      }

      public abstract static class SelectorModel<E extends CrystalWorkbenchScreen.ScrollableClickableItemStackSelectorElement.ItemSelectorEntry> {
         private Consumer<ClickableItemSlotElement<?>> onSlotSelect = slot -> {};
         private E selectedElement = (E)null;

         protected void onSlotSelect(Consumer<ClickableItemSlotElement<?>> onSlotSelect) {
            this.onSlotSelect = onSlotSelect;
         }

         public abstract List<E> getEntries();

         public void onSelect(ClickableItemSlotElement<?> slot, E entry) {
            this.selectedElement = entry;
            this.onSlotSelect.accept(slot);
         }

         @Nullable
         public E getSelectedElement() {
            return this.selectedElement;
         }
      }
   }
}
