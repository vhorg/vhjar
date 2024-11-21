package iskallia.vault.block.base;

import iskallia.vault.block.entity.base.InventoryRetainerTileEntity;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeBlock;

public interface InventoryRetainerBlock<T extends BlockEntity & InventoryRetainerTileEntity> extends IForgeBlock {
   default void addInventoryTooltip(
      ItemStack stack, List<Component> tooltip, BiFunction<ItemStack, CompoundTag, InventoryRetainerTileEntity.ContentDisplayInfo> tooltipProviderFn
   ) {
      if (stack.hasTag()) {
         CompoundTag tag = stack.getOrCreateTag();
         if (tag.contains("StoredInventory")) {
            InventoryRetainerTileEntity.ContentDisplayInfo displayInfo = tooltipProviderFn.apply(stack, tag.getCompound("StoredInventory"));
            if (!displayInfo.display().isEmpty()) {
               tooltip.add(TextComponent.EMPTY);
               tooltip.add(new TextComponent("Contains:").withStyle(ChatFormatting.GRAY));
               tooltip.addAll(displayInfo.display().subList(0, Math.min(displayInfo.display().size(), displayInfo.displayCount())));
               Component moreCmp = displayInfo.hasMoreCmp().apply(displayInfo.display().size() - displayInfo.displayCount());
               if (moreCmp != null) {
                  tooltip.add(moreCmp);
               }
            }
         }
      }
   }

   default void onInventoryBlockDestroy(Level level, BlockPos pos) {
      if (this instanceof Block thisblock && level.getBlockEntity(pos) instanceof var tileEntity) {
         ItemStack drop = new ItemStack(thisblock);
         this.storeInventoryContents(drop.getOrCreateTag(), (T)tileEntity);
         tileEntity.clearInventoryContents();
         this.dropItem(level, pos, drop);
      }
   }

   default void onInventoryBlockPlace(Level level, BlockPos pos, ItemStack stack) {
      if (level.getBlockEntity(pos) instanceof var tileEntity && stack.hasTag()) {
         CompoundTag tag = stack.getOrCreateTag();
         this.loadInventoryContents(tag, (T)tileEntity);
      }
   }

   default void storeInventoryContents(CompoundTag tag, T tile) {
      CompoundTag storedInventory = new CompoundTag();
      tile.storeInventoryContents(storedInventory);
      tag.put("StoredInventory", storedInventory);
   }

   default void loadInventoryContents(CompoundTag tag, T tile) {
      CompoundTag storedInventory = tag.getCompound("StoredInventory");
      if (!storedInventory.isEmpty()) {
         tile.loadInventoryContents(storedInventory);
      }
   }

   default ItemEntity dropItem(Level level, BlockPos pos, ItemStack stack) {
      ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
      entity.setDefaultPickUpDelay();
      level.addFreshEntity(entity);
      return entity;
   }
}
