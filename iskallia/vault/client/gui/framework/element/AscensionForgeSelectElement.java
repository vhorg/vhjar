package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AscensionForgeSelectElement<E extends AscensionForgeSelectElement<E>>
   extends ScrollableItemStackSelectorElement<E, AscensionForgeSelectElement.AscencionForgeModelEntry> {
   protected int columns;
   protected ObservableSupplier<Set<ResourceLocation>> discoveredModelIds;

   public AscensionForgeSelectElement(
      ISpatial spatial, int columns, ObservableSupplier<Set<ResourceLocation>> discoveredModelIds, BiConsumer<ResourceLocation, ItemStack> onItemSelected
   ) {
      super(Spatials.copy(spatial), columns, new AscensionForgeSelectElement.DiscoveredModelSelectorModel(discoveredModelIds, onItemSelected));
      this.columns = columns;
      this.discoveredModelIds = discoveredModelIds;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      this.discoveredModelIds.ifChanged(models -> this.refreshElements());
   }

   public static class AscencionForgeModelEntry extends ScrollableItemStackSelectorElement.ItemSelectorEntry {
      @Nullable
      private final ResourceLocation modelId;
      private final Component tooltip;

      public AscencionForgeModelEntry(ItemStack displayStack, boolean isDisabled, @Nullable ResourceLocation modelId, Component tooltip) {
         super(displayStack, isDisabled);
         this.modelId = modelId;
         this.tooltip = tooltip;
      }

      @Nullable
      public ResourceLocation getModelId() {
         return this.modelId;
      }

      public Component getTooltip() {
         return this.tooltip;
      }

      @Override
      public void adjustSlot(FakeItemSlotElement<?> slot) {
         slot.tooltip(
            (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
               if (this.isDisabled()) {
                  tooltipRenderer.renderTooltip(
                     poseStack,
                     new TextComponent("Discovered").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)),
                     mouseX,
                     mouseY,
                     TooltipDirection.RIGHT
                  );
                  return true;
               } else {
                  tooltipRenderer.renderTooltip(poseStack, this.tooltip, mouseX, mouseY, TooltipDirection.RIGHT);
                  return true;
               }
            }
         );
      }
   }

   public static class DiscoveredModelSelectorModel
      extends ScrollableItemStackSelectorElement.SelectorModel<AscensionForgeSelectElement.AscencionForgeModelEntry> {
      private final ObservableSupplier<Set<ResourceLocation>> discoveredModelIds;
      private final BiConsumer<ResourceLocation, ItemStack> onItemSelected;

      public DiscoveredModelSelectorModel(ObservableSupplier<Set<ResourceLocation>> discoveredModelIds, BiConsumer<ResourceLocation, ItemStack> onItemSelected) {
         this.discoveredModelIds = discoveredModelIds;
         this.onItemSelected = onItemSelected;
      }

      @Override
      public List<AscensionForgeSelectElement.AscencionForgeModelEntry> getEntries() {
         Player player = Minecraft.getInstance().player;
         if (player == null) {
            return Collections.emptyList();
         } else {
            Set<ResourceLocation> discoveredIds = this.discoveredModelIds.get();
            List<AscensionForgeSelectElement.AscencionForgeModelEntry> entries = new ArrayList<>();
            ModConfigs.ASCENSION_FORGE
               .getListings()
               .forEach(
                  listing -> {
                     if (listing.modelId() != null) {
                        ModDynamicModels.REGISTRIES
                           .getModelAndAssociatedItem(listing.modelId())
                           .ifPresent(
                              pair -> {
                                 ItemStack stack = new ItemStack((ItemLike)pair.getSecond());
                                 if (stack.getItem() instanceof VaultGearItem vaultGearItem) {
                                    VaultGearData gearData = VaultGearData.read(stack);
                                    gearData.setState(VaultGearState.IDENTIFIED);
                                    gearData.createOrReplaceAttributeValue(ModGearAttributes.GEAR_MODEL, listing.modelId());
                                    gearData.write(stack);
                                    VaultGearRarity rarity = ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRarityOf(stack, listing.modelId());
                                    MutableComponent cmp = new TextComponent(((DynamicModel)pair.getFirst()).getDisplayName());
                                    cmp.withStyle(Style.EMPTY.withColor(rarity.getColor()));
                                    entries.add(
                                       new AscensionForgeSelectElement.AscencionForgeModelEntry(
                                          stack, discoveredIds.contains(listing.modelId()), listing.modelId(), cmp
                                       )
                                    );
                                 }
                              }
                           );
                     } else {
                        entries.add(new AscensionForgeSelectElement.AscencionForgeModelEntry(listing.stack(), false, null, listing.stack().getHoverName()));
                     }
                  }
               );
            Comparator<AscensionForgeSelectElement.AscencionForgeModelEntry> comparator = Comparator.<AscensionForgeSelectElement.AscencionForgeModelEntry, Integer>comparing(
                  entry -> entry.getModelId() != null ? -1 : 1
               )
               .thenComparing(entry -> discoveredIds.contains(entry.getModelId()) ? 1 : -1)
               .thenComparing(entry -> entry.getTooltip().getString());
            entries.sort(comparator);
            return entries;
         }
      }

      public void onSelect(FakeItemSlotElement<?> slot, AscensionForgeSelectElement.AscencionForgeModelEntry entry) {
         super.onSelect(slot, entry);
         this.onItemSelected.accept(entry.getModelId(), entry.getDisplayStack());
      }
   }
}
