package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.TransmogTableBlock;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DiscoveredModelSelectElement<E extends DiscoveredModelSelectElement<E>>
   extends ScrollableItemStackSelectorElement<E, DiscoveredModelSelectElement.TransmogModelEntry> {
   protected int columns;
   protected ObservableSupplier<Item> gearItem;
   protected ObservableSupplier<Set<ResourceLocation>> discoveredModelIds;

   public DiscoveredModelSelectElement(
      ISpatial spatial,
      int columns,
      Supplier<Item> gearItem,
      ObservableSupplier<Set<ResourceLocation>> discoveredModelIds,
      Consumer<ResourceLocation> onModelSelected
   ) {
      super(
         Spatials.copy(spatial),
         columns,
         new DiscoveredModelSelectElement.DiscoveredModelSelectorModel(
            ObservableSupplier.of(gearItem, Objects::deepEquals), discoveredModelIds, onModelSelected
         )
      );
      this.columns = columns;
      this.gearItem = ObservableSupplier.of(gearItem, Objects::deepEquals);
      this.discoveredModelIds = discoveredModelIds;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      this.gearItem.ifChanged(item -> {
         this.verticalScrollBarElement.setValue(0.0F);
         this.refreshElements();
      });
      this.discoveredModelIds.ifChanged(models -> this.refreshElements());
   }

   public static class DiscoveredModelSelectorModel extends ScrollableItemStackSelectorElement.SelectorModel<DiscoveredModelSelectElement.TransmogModelEntry> {
      private final ObservableSupplier<Item> usedItem;
      private final ObservableSupplier<Set<ResourceLocation>> discoveredModelIds;
      private final Consumer<ResourceLocation> onModelSelected;

      public DiscoveredModelSelectorModel(
         ObservableSupplier<Item> usedItem, ObservableSupplier<Set<ResourceLocation>> discoveredModelIds, Consumer<ResourceLocation> onModelSelected
      ) {
         this.usedItem = usedItem;
         this.discoveredModelIds = discoveredModelIds;
         this.onModelSelected = onModelSelected;
      }

      @Override
      public List<DiscoveredModelSelectElement.TransmogModelEntry> getEntries() {
         Item item = this.usedItem.get();
         if (item instanceof VaultGearItem vaultGearItem) {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
               return Collections.emptyList();
            } else {
               Set<ResourceLocation> discoveredIds = this.discoveredModelIds.get();
               return ModDynamicModels.REGISTRIES
                  .getAssociatedRegistry(item)
                  .map(
                     modelRegistry -> {
                        List<ResourceLocation> modelIds = new ArrayList<>(modelRegistry.getIds());
                        modelIds.sort((id1, id2) -> {
                           boolean discovered1 = discoveredIds.contains(id1);
                           boolean discovered2 = discoveredIds.contains(id2);
                           if (discovered1 != discovered2) {
                              return discovered1 ? -1 : 1;
                           } else {
                              boolean special1 = ModConfigs.GEAR_MODEL_ROLL_RARITIES.canAppearNormally(vaultGearItem, id1);
                              boolean special2 = ModConfigs.GEAR_MODEL_ROLL_RARITIES.canAppearNormally(vaultGearItem, id2);
                              if (special1 != special2) {
                                 return special1 ? -1 : 1;
                              } else {
                                 VaultGearRarity rarity1 = ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRarityOf(vaultGearItem, id1);
                                 VaultGearRarity rarity2 = ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRarityOf(vaultGearItem, id2);
                                 if (rarity1 != rarity2) {
                                    return Integer.compare(rarity1.ordinal(), rarity2.ordinal());
                                 } else {
                                    String name1 = modelRegistry.get(id1).map(DynamicModel::getDisplayName).orElse("");
                                    String name2 = modelRegistry.get(id2).map(DynamicModel::getDisplayName).orElse("");
                                    return name1.compareTo(name2);
                                 }
                              }
                           }
                        });
                        return modelIds.stream()
                           .map(modelRegistry::get)
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .map(
                              model -> new DiscoveredModelSelectElement.TransmogModelEntry(
                                 this.makeModelItem(vaultGearItem, model.getId()),
                                 !TransmogTableBlock.canTransmogModel(player, discoveredIds, model.getId()),
                                 model
                              )
                           )
                           .toList();
                     }
                  )
                  .orElse(Collections.emptyList());
            }
         } else {
            return Collections.emptyList();
         }
      }

      private ItemStack makeModelItem(VaultGearItem item, ResourceLocation modelId) {
         ItemStack stack = item.defaultItem();
         VaultGearData gearData = VaultGearData.read(stack);
         gearData.setState(VaultGearState.IDENTIFIED);
         gearData.updateAttribute(ModGearAttributes.GEAR_MODEL, modelId);
         gearData.write(stack);
         return stack;
      }

      public void onSelect(FakeItemSlotElement<?> slot, DiscoveredModelSelectElement.TransmogModelEntry entry) {
         super.onSelect(slot, entry);
         this.onModelSelected.accept(entry.getModelId());
      }
   }

   public static class TransmogModelEntry extends ScrollableItemStackSelectorElement.ItemSelectorEntry {
      private final DynamicModel model;

      public TransmogModelEntry(ItemStack displayStack, boolean isDisabled, DynamicModel model) {
         super(displayStack, isDisabled);
         this.model = model;
      }

      public ResourceLocation getModelId() {
         return this.model.getId();
      }

      @Override
      public void adjustSlot(FakeItemSlotElement<?> slot) {
         slot.tooltip(
            (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
               if (this.getDisplayStack().getItem() instanceof VaultGearItem vaultGearItem) {
                  VaultGearRarity var9 = ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRarityOf(vaultGearItem, this.model.getId());
                  if (this.isDisabled()) {
                     tooltipRenderer.renderTooltip(
                        poseStack,
                        new TextComponent("Undiscovered").withStyle(Style.EMPTY.withColor(var9.getColor()).withItalic(true)),
                        mouseX,
                        mouseY,
                        TooltipDirection.RIGHT
                     );
                     return true;
                  } else {
                     MutableComponent cmp = new TextComponent(this.model.getDisplayName());
                     cmp.withStyle(Style.EMPTY.withColor(var9.getColor()));
                     tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                     return true;
                  }
               } else {
                  return true;
               }
            }
         );
      }
   }
}
