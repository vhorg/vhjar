package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.TextInputElement;
import iskallia.vault.client.gui.framework.element.WorkbenchCraftSelectorElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.container.ModifierWorkbenchContainer;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ModifierWorkbenchHelper;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.gear.tooltip.VaultGearTooltipItem;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ModifierWorkbenchCraftMessage;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ModifierWorkbenchScreen extends AbstractElementContainerScreen<ModifierWorkbenchContainer> {
   private final Inventory playerInventory;
   private final WorkbenchCraftSelectorElement<?, ?> selectorElement;
   private final TextInputElement<?> searchInput;
   private ModifierWorkbenchHelper.CraftingOption selectedOption;

   public ModifierWorkbenchScreen(ModifierWorkbenchContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      this.setGuiSize(Spatials.size(176, 212));
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 7),
               ((ModifierWorkbenchContainer)this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)),
               LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(Spatials.positionXY(8, 120), inventoryName, LabelTextStyle.defaultStyle())
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      this.addElement(
         this.searchInput = new TextInputElement(Spatials.positionXY(110, 5).size(60, 12), Minecraft.getInstance().font)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement(
         this.selectorElement = new WorkbenchCraftSelectorElement(
               Spatials.positionXY(8, 19).height(97),
               ObservableSupplier.ofIdentity(() -> ((ModifierWorkbenchContainer)this.getMenu()).getInput()),
               this.searchInput::getInput
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      ButtonElement<?> craftButton;
      this.addElement(
         craftButton = new ButtonElement(Spatials.positionXY(142, 48), ScreenTextures.BUTTON_CRAFT_TEXTURES, this::tryCraft)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      craftButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
         if (this.selectedOption == null) {
            return false;
         } else {
            ItemStack gear = ((ModifierWorkbenchContainer)this.getMenu()).getInput();
            if (gear.isEmpty()) {
               return false;
            } else if (AttributeGearData.hasData(gear) && !AttributeGearData.<AttributeGearData>read(gear).isModifiable()) {
               Component cmp = new TranslatableComponent("the_vault.gear_modification.unmodifiable").withStyle(ChatFormatting.RED);
               tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
               return true;
            } else {
               List<ItemStack> inputs = this.selectedOption.getCraftingCost(gear);
               List<ItemStack> missing = InventoryUtil.getMissingInputs(inputs, this.playerInventory);
               if (missing.isEmpty()) {
                  List<Component> tooltip = new ArrayList<>();
                  tooltip.add(gear.getHoverName());
                  if (gear.getItem() instanceof VaultGearTooltipItem gearTooltipItem) {
                     tooltip.addAll(gearTooltipItem.createTooltip(gear, GearTooltip.craftingView()));
                  }

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
         ItemStack gear = ((ModifierWorkbenchContainer)this.getMenu()).getInput();
         if (gear.isEmpty()) {
            return true;
         } else if (this.selectedOption != null) {
            List<ItemStack> inputs = this.selectedOption.getCraftingCost(gear);
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
         ItemStack gear = ((ModifierWorkbenchContainer)this.getMenu()).getInput();
         if (!gear.isEmpty()) {
            ItemStack gearCopy = gear.copy();
            VaultGearWorkbenchConfig.CraftableModifierConfig cfg = this.selectedOption.cfg();
            if (cfg != null) {
               if (VaultGearData.read(gearCopy).getItemLevel() < cfg.getMinLevel()) {
                  return;
               }

               VaultGearModifier<?> modifier = cfg.createModifier().orElse(null);
               if (modifier != null) {
                  if (ModifierWorkbenchHelper.hasCraftedModifier(gearCopy) && !ModifierWorkbenchHelper.removeCraftedModifiers(gearCopy)) {
                     return;
                  }

                  VaultGearData data = VaultGearData.read(gearCopy);
                  Set<String> modGroups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
                  if (modGroups.contains(modifier.getModifierGroup())) {
                     return;
                  }
               }
            }

            List<ItemStack> inputs = this.selectedOption.getCraftingCost(gear);
            List<ItemStack> missing = InventoryUtil.getMissingInputs(inputs, this.playerInventory);
            if (missing.isEmpty()) {
               ResourceLocation craftKey = cfg == null ? null : cfg.getWorkbenchCraftIdentifier();
               ModNetwork.CHANNEL.sendToServer(new ModifierWorkbenchCraftMessage(((ModifierWorkbenchContainer)this.getMenu()).getTilePos(), craftKey));
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
