package iskallia.vault.block.entity.base;

import iskallia.vault.container.oversized.OverSizedItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeBlockEntity;

public interface InventoryRetainerTileEntity extends IForgeBlockEntity {
   static InventoryRetainerTileEntity.ContentDisplayInfo displayContents(int maxDisplayCount, Consumer<List<ItemStack>> provideContentFn) {
      List<ItemStack> stacks = new ArrayList<>();
      provideContentFn.accept(stacks);
      return InventoryRetainerTileEntity.ContentDisplayInfo.makeStacks(stacks, maxDisplayCount);
   }

   static InventoryRetainerTileEntity.ContentDisplayInfo displayContentsOverSized(int maxDisplayCount, Consumer<List<OverSizedItemStack>> provideContentFn) {
      List<OverSizedItemStack> stacks = new ArrayList<>();
      provideContentFn.accept(stacks);
      return InventoryRetainerTileEntity.ContentDisplayInfo.makeStacks(stacks.stream().map(OverSizedItemStack::overSizedStack).toList(), maxDisplayCount);
   }

   void storeInventoryContents(CompoundTag var1);

   void loadInventoryContents(CompoundTag var1);

   void clearInventoryContents();

   public record ContentDisplayInfo(List<Component> display, int displayCount, Function<Integer, Component> hasMoreCmp) {
      public static InventoryRetainerTileEntity.ContentDisplayInfo makeStacks(List<ItemStack> display, int maxDisplay) {
         return make(convertStacks(display), maxDisplay);
      }

      public static InventoryRetainerTileEntity.ContentDisplayInfo make(List<Component> display, int maxDisplay) {
         return new InventoryRetainerTileEntity.ContentDisplayInfo(display, Math.min(display.size(), maxDisplay), defaultMoreComponent());
      }

      public InventoryRetainerTileEntity.ContentDisplayInfo append(InventoryRetainerTileEntity.ContentDisplayInfo additional) {
         return this.append(additional.display(), Math.max(additional.displayCount(), this.displayCount()));
      }

      public InventoryRetainerTileEntity.ContentDisplayInfo appendStacks(List<ItemStack> additional, int newMaxDisplay) {
         return this.append(convertStacks(additional), newMaxDisplay);
      }

      public InventoryRetainerTileEntity.ContentDisplayInfo append(List<Component> additional, int newMaxDisplay) {
         List<Component> newDisplay = new ArrayList<>(this.display());
         newDisplay.addAll(additional);
         return new InventoryRetainerTileEntity.ContentDisplayInfo(newDisplay, Math.max(newDisplay.size(), newMaxDisplay), defaultMoreComponent());
      }

      private static Function<Integer, Component> defaultMoreComponent() {
         return count -> count <= 0 ? null : new TextComponent("... " + count + " more").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC);
      }

      private static List<Component> convertStacks(List<ItemStack> display) {
         return display.stream()
            .map(
               stack -> {
                  Component displayCmp = stack.getHoverName();
                  MutableComponent countCmp = new TextComponent(stack.getCount() + "x ").withStyle(ChatFormatting.WHITE);
                  return new TextComponent("- ")
                     .withStyle(ChatFormatting.GRAY)
                     .append(countCmp)
                     .append(new TextComponent("").withStyle(ChatFormatting.RESET).append(displayCmp));
               }
            )
            .collect(Collectors.toList());
      }
   }
}
