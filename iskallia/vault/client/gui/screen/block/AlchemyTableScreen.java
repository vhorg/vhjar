package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.AlchemyCraftSelectorElement;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.TextInputElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.config.AlchemyTableConfig;
import iskallia.vault.container.AlchemyTableContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.bottle.BottleEffect;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.network.message.AlchemyTableEffectCraftMessage;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class AlchemyTableScreen extends AbstractElementContainerScreen<AlchemyTableContainer> {
   private final Inventory playerInventory;
   private final AlchemyCraftSelectorElement<?, ?> selectorElement;
   private final TextInputElement<?> searchInput;
   private AlchemyCraftSelectorElement.CraftingOption selectedOption;

   public AlchemyTableScreen(AlchemyTableContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      this.setGuiSize(Spatials.size(176, 221));
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 7),
               ((AlchemyTableContainer)this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(Spatials.positionXY(8, 129), inventoryName, LabelTextStyle.defaultStyle())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      this.addElement(
         this.searchInput = new TextInputElement(Spatials.positionXY(108, 5).size(60, 12), Minecraft.getInstance().font)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         this.selectorElement = new AlchemyCraftSelectorElement(
               Spatials.positionXY(7, 19).height(97),
               ObservableSupplier.ofIdentity(() -> ((AlchemyTableContainer)this.getMenu()).getInput()),
               this.searchInput::getInput
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      ButtonElement<?> craftButton;
      this.addElement(
         craftButton = new ButtonElement(Spatials.positionXY(131, 118), ScreenTextures.BUTTON_ALCHEMY_CRAFT_TEXTURES, this::tryCraft)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      craftButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
         if (this.selectedOption == null) {
            return false;
         } else {
            ItemStack bottle = ((AlchemyTableContainer)this.getMenu()).getInput();
            if (bottle.isEmpty()) {
               return false;
            } else {
               List<ItemStack> inputs = this.selectedOption.getCraftingCost(bottle);
               List<ItemStack> missing = InventoryUtil.getMissingInputs(inputs, this.playerInventory);
               if (missing.isEmpty()) {
                  List<Component> tooltip = new ArrayList<>();
                  tooltip.add(bottle.getHoverName());
                  BottleItem.getEffect(bottle).ifPresent(BottleEffect::getTooltip);
                  tooltipRenderer.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, TooltipDirection.RIGHT);
                  return true;
               } else {
                  Component cmp = new TranslatableComponent("the_vault.gear_workbench.missing_inputs").withStyle(ChatFormatting.RED);
                  tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                  return true;
               }
            }
         }
      });
      craftButton.setDisabled(() -> {
         ItemStack potion = ((AlchemyTableContainer)this.getMenu()).getInput();
         if (potion.isEmpty()) {
            return true;
         } else if (this.selectedOption != null) {
            List<ItemStack> inputs = this.selectedOption.getCraftingCost(potion);
            List<ItemStack> missing = InventoryUtil.getMissingInputs(inputs, this.playerInventory);
            return !missing.isEmpty();
         } else {
            return true;
         }
      });
      this.selectorElement.onSelect(option -> this.selectedOption = option);
      this.searchInput.onTextChanged(text -> this.selectorElement.refreshElements());
   }

   private void tryCraft() {
      if (this.selectedOption != null) {
         ItemStack potion = ((AlchemyTableContainer)this.getMenu()).getInput();
         if (!potion.isEmpty()) {
            AlchemyTableConfig.CraftableEffectConfig cfg = this.selectedOption.cfg();
            List<ItemStack> inputs = this.selectedOption.getCraftingCost(potion);
            List<ItemStack> missing = InventoryUtil.getMissingInputs(inputs, this.playerInventory);
            if (missing.isEmpty()) {
               String craftId = cfg == null ? null : cfg.getEffectId();
               ModNetwork.CHANNEL.sendToServer(new AlchemyTableEffectCraftMessage(((AlchemyTableContainer)this.getMenu()).getTilePos(), craftId));
            }
         }
      }
   }

   protected void containerTick() {
      super.containerTick();
      this.searchInput.tickEditBox();
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      Key key = InputConstants.getKey(keyCode, scanCode);
      if (this.searchInput.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
         if (!this.searchInput.isFocused()) {
            this.onClose();
         }

         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public boolean charTyped(char codePoint, int modifiers) {
      return this.searchInput.charTyped(codePoint, modifiers) ? true : super.charTyped(codePoint, modifiers);
   }
}
