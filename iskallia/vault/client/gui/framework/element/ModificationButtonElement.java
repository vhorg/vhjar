package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationAction;
import iskallia.vault.gear.modification.GearModificationCost;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.gear.tooltip.VaultGearTooltipItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ModificationButtonElement<E extends ModificationButtonElement<E>> extends ButtonElement<E> {
   private static final Random rand = new Random();

   public ModificationButtonElement(IPosition position, Runnable onClick, VaultArtisanStationContainer container, GearModification modification) {
      super(position, ScreenTextures.BUTTON_CRAFT_TEXTURES, onClick);
      this.tooltip(
         Tooltips.multi(
            () -> {
               GearModificationAction action = container.getModificationAction(modification);
               if (action == null) {
                  return Collections.emptyList();
               } else if (!this.isVisible()) {
                  return Collections.emptyList();
               } else {
                  ItemStack inputItem = ItemStack.EMPTY;
                  Slot inputSlot = action.getCorrespondingSlot(container);
                  if (inputSlot != null && !inputSlot.getItem().isEmpty()) {
                     inputItem = inputSlot.getItem();
                  }

                  ItemStack gearStack = container.getGearInputSlot().getItem();
                  AttributeGearData itemData = AttributeGearData.read(gearStack);
                  int potential = itemData.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL).orElse(Integer.MIN_VALUE);
                  boolean hasInput = !gearStack.isEmpty() && potential != Integer.MIN_VALUE;
                  boolean failedModification = false;
                  List<Component> tooltip = new ArrayList<>(modification.getDescription(inputItem));
                  if (hasInput && !itemData.isModifiable()) {
                     return List.of(new TranslatableComponent("the_vault.gear_modification.unmodifiable").withStyle(ChatFormatting.RED));
                  } else {
                     if (hasInput && !inputItem.isEmpty()) {
                        GearModification.Result result = action.modification().canApply(gearStack, inputItem, container.getPlayer(), rand);
                        if (!result.success()) {
                           tooltip.add(result.getError(action.modification()));
                           failedModification = true;
                        }
                     }

                     if (!failedModification && hasInput) {
                        MutableComponent focusCmp;
                        if (!inputItem.isEmpty()) {
                           focusCmp = new TextComponent("- ")
                              .append(modification.getDisplayStack().getHoverName())
                              .append(" x1")
                              .append(" [%s]".formatted(inputItem.getCount()));
                        } else {
                           focusCmp = new TextComponent("Requires ").append(modification.getDisplayStack().getHoverName());
                        }

                        focusCmp.withStyle(inputItem.isEmpty() ? ChatFormatting.RED : ChatFormatting.GREEN);
                        tooltip.add(focusCmp);
                     }

                     if (hasInput) {
                        if (!failedModification && !inputItem.isEmpty()) {
                           VaultGearData data = VaultGearData.read(gearStack);
                           String rollType = data.get(ModGearAttributes.GEAR_ROLL_TYPE, VaultGearAttributeTypeMerger.firstNonNull());
                           GearModificationCost cost = GearModificationCost.getCost(data.getRarity(), rollType, data.getItemLevel(), potential, modification);
                           ItemStack plating = container.getPlatingSlot().getItem();
                           ItemStack bronze = container.getBronzeSlot().getItem();
                           tooltip.add(
                              new TextComponent("- ")
                                 .append(new ItemStack(ModItems.VAULT_PLATING).getHoverName())
                                 .append(" x" + cost.costPlating())
                                 .append(" [%s]".formatted(plating.getCount()))
                                 .withStyle(cost.costPlating() > plating.getCount() ? ChatFormatting.RED : ChatFormatting.GREEN)
                           );
                           tooltip.add(
                              new TextComponent("- ")
                                 .append(new ItemStack(ModBlocks.VAULT_BRONZE).getHoverName())
                                 .append(" x" + cost.costBronze())
                                 .append(" [%s]".formatted(bronze.getCount()))
                                 .withStyle(cost.costBronze() > bronze.getCount() ? ChatFormatting.RED : ChatFormatting.GREEN)
                           );
                        }

                        tooltip.add(TextComponent.EMPTY);
                        tooltip.add(gearStack.getHoverName());
                        if (gearStack.getItem() instanceof VaultGearTooltipItem gearTooltipItem) {
                           tooltip.addAll(gearTooltipItem.createTooltip(gearStack, GearTooltip.craftingView()));
                        }
                     }

                     return tooltip;
                  }
               }
            }
         )
      );
   }
}
