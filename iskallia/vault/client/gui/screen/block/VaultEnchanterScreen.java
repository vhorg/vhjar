package iskallia.vault.client.gui.screen.block;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.EnchantBookElement;
import iskallia.vault.client.gui.framework.element.EnchanterEnchantSelectorElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.TextInputElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.container.VaultEnchanterContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultEnchanterEnchantMessage;
import iskallia.vault.util.EnchantmentEntry;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.util.RomanNumber;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class VaultEnchanterScreen extends AbstractElementContainerScreen<VaultEnchanterContainer> {
   public static ResourceLocation VAULT_ENCHANTER_BOOK_TEXTURE = VaultMod.id("textures/entity/vault_enchanter_book.png");
   private final Inventory playerInventory;
   private final EnchanterEnchantSelectorElement<?, ?> selectorElement;
   private final TextInputElement<?> searchInput;
   private final EnchantBookElement<?> bookElement;
   private ObservableSupplier<ItemStack> inputItemStack;
   private EnchantmentEntry selectedEnchantmentEntry;

   public VaultEnchanterScreen(VaultEnchanterContainer container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
      this.playerInventory = inventory;
      this.setGuiSize(Spatials.size(176, 212));
      this.inputItemStack = ObservableSupplier.of(container::getInput, Objects::equals);
      this.addElement(
         (NineSliceElement)new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui)))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 7),
               ((VaultEnchanterContainer)this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)),
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
         this.selectorElement = new EnchanterEnchantSelectorElement(
               Spatials.positionXY(8, 19).height(97),
               ObservableSupplier.ofIdentity(() -> ((VaultEnchanterContainer)this.getMenu()).getInput()),
               this.searchInput::getInput
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      ButtonElement<?> craftButton;
      this.addElement(
         craftButton = new ButtonElement(Spatials.positionXY(145, 68), ScreenTextures.BUTTON_CRAFT_TEXTURES, this::tryCraft)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      craftButton.tooltip(
         (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            if (this.selectedEnchantmentEntry == null) {
               return false;
            } else {
               ItemStack gear = ((VaultEnchanterContainer)this.getMenu()).getInput();
               if (gear.isEmpty()) {
                  return false;
               } else {
                  List<ItemStack> itemCost = this.selectedEnchantmentEntry.getCost().getItems();
                  List<ItemStack> missing = InventoryUtil.getMissingInputs(itemCost, this.playerInventory);
                  int levelCost = this.selectedEnchantmentEntry.getCost().getLevels();
                  boolean playerHasLevels = this.playerInventory.player.experienceLevel >= levelCost;
                  List<Component> tooltip = new LinkedList<>();
                  if (missing.isEmpty() && playerHasLevels) {
                     tooltip.add(new TextComponent("Enchant ").append(gear.getHoverName().copy()));
                     tooltip.add(
                        new TextComponent("with ")
                           .append(
                              new TranslatableComponent(this.selectedEnchantmentEntry.getEnchantment().getDescriptionId())
                                 .append(" " + RomanNumber.toRoman(this.selectedEnchantmentEntry.getLevel()))
                                 .withStyle(ChatFormatting.LIGHT_PURPLE)
                           )
                           .append(" !")
                     );
                  } else {
                     tooltip.add(new TextComponent("Missing ingredients.").withStyle(ChatFormatting.RED));
                     tooltip.add(new TextComponent(""));

                     for (ItemStack costStack : this.selectedEnchantmentEntry.getCost().getItems()) {
                        tooltip.add(
                           new TextComponent(costStack.getCount() + "x ")
                              .append(costStack.getHoverName().copy())
                              .withStyle(missing.contains(costStack) ? ChatFormatting.RED : ChatFormatting.GREEN)
                        );
                     }

                     if (levelCost != 0) {
                        tooltip.add(new TextComponent(levelCost + " EXP Levels").withStyle(playerHasLevels ? ChatFormatting.GREEN : ChatFormatting.RED));
                     }
                  }

                  tooltipRenderer.renderTooltip(poseStack, tooltip, mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT);
                  return true;
               }
            }
         }
      );
      craftButton.setDisabled(() -> {
         ItemStack gear = ((VaultEnchanterContainer)this.getMenu()).getInput();
         if (gear.isEmpty()) {
            return true;
         } else if (this.selectedEnchantmentEntry != null) {
            if (this.playerInventory.player.isCreative()) {
               return false;
            } else {
               List<ItemStack> inputs = this.selectedEnchantmentEntry.getCost().getItems();
               List<ItemStack> missing = InventoryUtil.getMissingInputs(inputs, this.playerInventory);
               int levelCost = this.selectedEnchantmentEntry.getCost().getLevels();
               return !missing.isEmpty() || this.playerInventory.player.experienceLevel < levelCost;
            }
         } else {
            return true;
         }
      });
      this.bookElement = this.addElement(
         new EnchantBookElement(
               Spatials.zero(),
               Spatials.size(100, 100),
               () -> this.selectedEnchantmentEntry == null ? 0 : this.selectedEnchantmentEntry.getCost().getLevels(),
               () -> !container.getInput().isEmpty() && this.selectedEnchantmentEntry != null
            )
            .withCustomTexture(VAULT_ENCHANTER_BOOK_TEXTURE)
            .layout((screen, gui, parent, world) -> world.translateXYZ(gui).translateX(149).translateY(45))
      );
      this.selectorElement.onSelect(option -> this.selectedEnchantmentEntry = option);
      this.searchInput.onTextChanged(text -> this.selectorElement.refreshElements());
   }

   private void tryCraft() {
      if (this.selectedEnchantmentEntry != null) {
         ItemStack gear = ((VaultEnchanterContainer)this.getMenu()).getInput();
         if (!gear.isEmpty()) {
            ModNetwork.CHANNEL
               .sendToServer(new VaultEnchanterEnchantMessage(((VaultEnchanterContainer)this.getMenu()).getTilePos(), this.selectedEnchantmentEntry));
         }
      }
   }

   @Override
   public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(poseStack, mouseX, mouseY, partialTick);
      this.inputItemStack.ifChanged(inputStack -> this.selectedEnchantmentEntry = null);
   }

   protected void containerTick() {
      super.containerTick();
      this.searchInput.tickEditBox();
      this.bookElement.tickBook();
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
