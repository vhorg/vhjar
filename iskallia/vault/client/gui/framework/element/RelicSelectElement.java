package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RelicSelectElement<E extends RelicSelectElement<E>> extends ScrollableItemStackSelectorElement<E, RelicSelectElement.RelicEntry> {
   protected int columns;

   public RelicSelectElement(ISpatial spatial, int columns, Supplier<Set<ResourceLocation>> discoveredRelics, Consumer<ResourceLocation> onRelicSelect) {
      super(Spatials.copy(spatial), columns, new RelicSelectElement.RelicSelectorModel(discoveredRelics, onRelicSelect));
      this.columns = columns;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
   }

   public static class RelicEntry extends ScrollableItemStackSelectorElement.ItemSelectorEntry {
      private final boolean discovered;
      private final DynamicModel model;

      public RelicEntry(ItemStack displayStack, boolean discovered, DynamicModel model) {
         super(displayStack, false);
         this.discovered = discovered;
         this.model = model;
      }

      public ResourceLocation getModelId() {
         return this.model.getId();
      }

      @Override
      public void adjustSlot(FakeItemSlotElement<?> slot) {
         slot.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            List<Component> components = new LinkedList<>();
            components.add(new TextComponent(this.model.getDisplayName()).withStyle(Style.EMPTY.withColor(-2505149)));
            if (!this.discovered) {
               components.add(new TextComponent("Not Assembled Before"));
            }

            tooltipRenderer.renderTooltip(poseStack, components, mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.LEFT);
            return true;
         });
      }
   }

   public static class RelicSelectorModel extends ScrollableItemStackSelectorElement.SelectorModel<RelicSelectElement.RelicEntry> {
      private final Supplier<Set<ResourceLocation>> discoveredRelics;
      private final Consumer<ResourceLocation> onRelicSelected;

      public RelicSelectorModel(Supplier<Set<ResourceLocation>> discoveredRelics, Consumer<ResourceLocation> onRelicSelected) {
         this.discoveredRelics = discoveredRelics;
         this.onRelicSelected = onRelicSelected;
      }

      @Override
      public List<RelicSelectElement.RelicEntry> getEntries() {
         Set<ResourceLocation> discoveredRelics = this.discoveredRelics.get();
         List<ResourceLocation> relicIds = new ArrayList<>(ModDynamicModels.Relics.RELIC_REGISTRY.getIds());
         relicIds.sort((relic1, relic2) -> {
            boolean discovered1 = discoveredRelics.contains(relic1);
            boolean discovered2 = discoveredRelics.contains(relic2);
            if (discovered1 != discovered2) {
               return discovered1 ? -1 : 1;
            } else {
               String name1 = ModDynamicModels.Relics.RELIC_REGISTRY.get(relic1).map(DynamicModel::getDisplayName).orElse("");
               String name2 = ModDynamicModels.Relics.RELIC_REGISTRY.get(relic2).map(DynamicModel::getDisplayName).orElse("");
               return name1.compareTo(name2);
            }
         });
         return relicIds.stream()
            .map(ModDynamicModels.Relics.RELIC_REGISTRY::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(
               relicModel -> new RelicSelectElement.RelicEntry(
                  this.makeModelItem(relicModel.getId()), discoveredRelics.contains(relicModel.getId()), relicModel
               )
            )
            .toList();
      }

      private ItemStack makeModelItem(ResourceLocation relicId) {
         ItemStack stack = new ItemStack(ModItems.RELIC);
         DynamicModelItem.setGenericModelId(stack, relicId);
         return stack;
      }

      public void onSelect(FakeItemSlotElement<?> slot, RelicSelectElement.RelicEntry entry) {
         super.onSelect(slot, entry);
         this.onRelicSelected.accept(entry.getModelId());
      }
   }
}
