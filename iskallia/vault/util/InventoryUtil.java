package iskallia.vault.util;

import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.integration.IntegrationSB;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class InventoryUtil {
   private static final Set<Function<Player, List<InventoryUtil.ItemAccess>>> INVENTORY_ACCESS = Set.of(player -> {
      List<InventoryUtil.ItemAccess> items = new ArrayList<>();
      Inventory inv = player.getInventory();

      for (int slot = 0; slot < inv.getContainerSize(); slot++) {
         ItemStack stack = inv.getItem(slot);
         if (!stack.isEmpty()) {
            int finalSlot = slot;
            items.add(new InventoryUtil.ItemAccess(stack, newStack -> inv.setItem(finalSlot, newStack)));
         }
      }

      return items;
   }, player -> {
      List<InventoryUtil.ItemAccess> items = new ArrayList<>();
      IntegrationCurios.getCuriosItemStacks(player).forEach((slot, stackTpl) -> stackTpl.forEach(tpl -> {
         ItemStack stack = (ItemStack)tpl.getA();
         if (!stack.isEmpty()) {
            items.add(new InventoryUtil.ItemAccess(stack, newStack -> IntegrationCurios.setCurioItemStack(player, newStack, slot, (Integer)tpl.getB())));
         }
      }));
      return items;
   });
   private static final Set<Function<InventoryUtil.ItemAccess, List<InventoryUtil.ItemAccess>>> CONTENT_ACCESSORS = Set.of(
      IntegrationSB::getBackpackItemAccess,
      InventoryUtil::getShulkerBoxAccess,
      InventoryUtil::getBundleItemAccess,
      InventoryUtil::getSatchelItemAccess,
      InventoryUtil::getSupplementariesSafeAccess,
      InventoryUtil::getSupplementariesSackAccess,
      InventoryUtil::getBotaniaBaubleBoxAccess
   );

   public static List<InventoryUtil.ItemAccess> findAllItems(Player player) {
      List<InventoryUtil.ItemAccess> itemAccesses = new ArrayList<>();

      for (Function<Player, List<InventoryUtil.ItemAccess>> inventoryFn : INVENTORY_ACCESS) {
         inventoryFn.apply(player).forEach(inventoryStackAccess -> discoverContents(inventoryStackAccess, itemAccesses));
      }

      return itemAccesses;
   }

   private static void discoverContents(InventoryUtil.ItemAccess access, List<InventoryUtil.ItemAccess> out) {
      out.add(access);

      for (Function<InventoryUtil.ItemAccess, List<InventoryUtil.ItemAccess>> containerAccess : CONTENT_ACCESSORS) {
         containerAccess.apply(access).forEach(containedAccess -> discoverContents(containedAccess, out));
      }
   }

   public static List<ItemStack> findAllItems(List<ItemStack> items) {
      List<InventoryUtil.ItemAccess> itemAccesses = new ArrayList<>();

      for (ItemStack stack : items) {
         InventoryUtil.ItemAccess readOnlyStack = new InventoryUtil.ItemAccess(stack);
         itemAccesses.add(readOnlyStack);

         for (Function<InventoryUtil.ItemAccess, List<InventoryUtil.ItemAccess>> containerAccess : CONTENT_ACCESSORS) {
            containerAccess.apply(readOnlyStack).forEach(containedAccess -> discoverContents(containedAccess, itemAccesses));
         }
      }

      return itemAccesses.stream().map(InventoryUtil.ItemAccess::getStack).collect(Collectors.toList());
   }

   private static List<InventoryUtil.ItemAccess> getBotaniaBaubleBoxAccess(InventoryUtil.ItemAccess containerAccess) {
      List<InventoryUtil.ItemAccess> accesses = new ArrayList<>();
      ItemStack container = containerAccess.getStack();
      if ("botania:bauble_box".equals(container.getItem().getRegistryName().toString()) && container.hasTag()) {
         ListTag itemList = container.getOrCreateTag().getList("Items", 10);

         for (int slot = 0; slot < itemList.size(); slot++) {
            ItemStack storedItem = ItemStack.of(itemList.getCompound(slot));
            if (!storedItem.isEmpty()) {
               int finalSlot = slot;
               accesses.add(containerAccess.chain(storedItem, (containerCt, newStack) -> {
                  ListTag containerList = containerCt.getOrCreateTag().getList("Items", 10);
                  CompoundTag newItemTag = new CompoundTag();
                  newStack.save(newItemTag);
                  containerList.set(finalSlot, newItemTag);
                  containerCt.getOrCreateTag().put("Items", containerList);
               }));
            }
         }
      }

      return accesses;
   }

   private static List<InventoryUtil.ItemAccess> getSupplementariesSackAccess(InventoryUtil.ItemAccess containerAccess) {
      List<InventoryUtil.ItemAccess> accesses = new ArrayList<>();
      ItemStack container = containerAccess.getStack();
      if ("supplementaries:sack".equals(container.getItem().getRegistryName().toString()) && container.hasTag()) {
         CompoundTag tag = BlockItem.getBlockEntityData(container);
         if (tag != null) {
            NonNullList<ItemStack> contents = NonNullList.withSize(9, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, contents);

            for (int slot = 0; slot < contents.size(); slot++) {
               ItemStack stack = (ItemStack)contents.get(slot);
               if (!stack.isEmpty()) {
                  int finalSlot = slot;
                  accesses.add(containerAccess.chain(stack, (containerStack, newStack) -> {
                     NonNullList<ItemStack> ctContents = NonNullList.withSize(9, ItemStack.EMPTY);
                     CompoundTag ctTag = BlockItem.getBlockEntityData(containerStack);
                     if (ctTag != null) {
                        ContainerHelper.loadAllItems(ctTag, ctContents);
                        ctContents.set(finalSlot, newStack);
                        ContainerHelper.saveAllItems(ctTag, ctContents);
                        containerStack.addTagElement("BlockEntityTag", ctTag);
                     }
                  }));
               }
            }
         }
      }

      return accesses;
   }

   private static List<InventoryUtil.ItemAccess> getSupplementariesSafeAccess(InventoryUtil.ItemAccess containerAccess) {
      List<InventoryUtil.ItemAccess> accesses = new ArrayList<>();
      ItemStack container = containerAccess.getStack();
      if ("supplementaries:safe".equals(container.getItem().getRegistryName().toString()) && container.hasTag()) {
         CompoundTag tag = BlockItem.getBlockEntityData(container);
         if (tag != null) {
            NonNullList<ItemStack> contents = NonNullList.withSize(27, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, contents);

            for (int slot = 0; slot < contents.size(); slot++) {
               ItemStack stack = (ItemStack)contents.get(slot);
               if (!stack.isEmpty()) {
                  int finalSlot = slot;
                  accesses.add(containerAccess.chain(stack, (containerStack, newStack) -> {
                     NonNullList<ItemStack> ctContents = NonNullList.withSize(27, ItemStack.EMPTY);
                     CompoundTag ctTag = BlockItem.getBlockEntityData(containerStack);
                     if (ctTag != null) {
                        ContainerHelper.loadAllItems(ctTag, ctContents);
                        ctContents.set(finalSlot, newStack);
                        ContainerHelper.saveAllItems(ctTag, ctContents);
                        containerStack.addTagElement("BlockEntityTag", ctTag);
                     }
                  }));
               }
            }
         }
      }

      return accesses;
   }

   private static List<InventoryUtil.ItemAccess> getSatchelItemAccess(InventoryUtil.ItemAccess containerAccess) {
      List<InventoryUtil.ItemAccess> accesses = new ArrayList<>();
      ItemStack container = containerAccess.getStack();
      if ("thermal:satchel".equals(container.getItem().getRegistryName().toString()) && container.hasTag()) {
         CompoundTag invTag = container.getOrCreateTagElement("ItemInv");
         if (invTag.contains("ItemInv", 9)) {
            ListTag list = invTag.getList("ItemInv", 10);

            for (int slot = 0; slot < list.size(); slot++) {
               ItemStack stack = ItemStack.of(list.getCompound(slot));
               if (!stack.isEmpty()) {
                  int finalSlot = slot;
                  accesses.add(containerAccess.chain(stack, (containerCt, newStack) -> {
                     CompoundTag newInvTag = container.getOrCreateTagElement("ItemInv");
                     ListTag invList = newInvTag.getList("ItemInv", 10);
                     CompoundTag newItemTag = new CompoundTag();
                     newStack.save(newItemTag);
                     invList.set(finalSlot, newItemTag);
                     newInvTag.put("ItemInv", invList);
                  }));
               }
            }
         }
      }

      return accesses;
   }

   private static List<InventoryUtil.ItemAccess> getBundleItemAccess(InventoryUtil.ItemAccess containerAccess) {
      List<InventoryUtil.ItemAccess> accesses = new ArrayList<>();
      ItemStack container = containerAccess.getStack();
      if (container.getItem() instanceof BundleItem && container.hasTag()) {
         CompoundTag tag = container.getOrCreateTag();
         ListTag itemList = tag.getList("Items", 10);

         for (int slot = 0; slot < itemList.size(); slot++) {
            CompoundTag itemTag = itemList.getCompound(slot);
            ItemStack itemStack = ItemStack.of(itemTag);
            if (!itemStack.isEmpty()) {
               int finalSlot = slot;
               accesses.add(containerAccess.chain(itemStack, (containerCt, newStack) -> {
                  CompoundTag newTag = containerCt.getOrCreateTag();
                  ListTag newItemList = newTag.getList("Items", 10);
                  CompoundTag newItemTag = new CompoundTag();
                  newStack.save(newItemTag);
                  newItemList.set(finalSlot, newItemTag);
                  newTag.put("Items", newItemList);
               }));
            }
         }

         return accesses;
      } else {
         return accesses;
      }
   }

   private static List<InventoryUtil.ItemAccess> getShulkerBoxAccess(InventoryUtil.ItemAccess containerAccess) {
      List<InventoryUtil.ItemAccess> accesses = new ArrayList<>();
      ItemStack container = containerAccess.getStack();
      if (isShulkerBox(containerAccess.getStack().getItem())) {
         CompoundTag tag = BlockItem.getBlockEntityData(container);
         if (tag != null) {
            NonNullList<ItemStack> contents = NonNullList.withSize(27, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, contents);

            for (int slot = 0; slot < contents.size(); slot++) {
               ItemStack stack = (ItemStack)contents.get(slot);
               if (!stack.isEmpty()) {
                  int finalSlot = slot;
                  accesses.add(containerAccess.chain(stack, (containerStack, newStack) -> {
                     NonNullList<ItemStack> ctContents = NonNullList.withSize(27, ItemStack.EMPTY);
                     CompoundTag ctTag = BlockItem.getBlockEntityData(containerStack);
                     if (ctTag != null) {
                        ContainerHelper.loadAllItems(ctTag, ctContents);
                        ctContents.set(finalSlot, newStack);
                        ContainerHelper.saveAllItems(ctTag, ctContents);
                        containerStack.addTagElement("BlockEntityTag", ctTag);
                     }
                  }));
               }
            }
         }
      }

      return accesses;
   }

   private static boolean isShulkerBox(Item item) {
      return item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
   }

   public static class ItemAccess {
      private ItemStack stack;
      private final Consumer<ItemStack> setter;

      public ItemAccess(ItemStack stack) {
         this(stack, newStack -> {});
      }

      public ItemAccess(ItemStack stack, Consumer<ItemStack> setter) {
         this.stack = stack;
         this.setter = setter;
      }

      public InventoryUtil.ItemAccess chain(ItemStack containedItem, BiConsumer<ItemStack, ItemStack> containedItemSetter) {
         return new InventoryUtil.ItemAccess(containedItem, newStack -> {
            ItemStack container = this.getStack();
            containedItemSetter.accept(container, newStack);
            this.setStack(container);
         });
      }

      public ItemStack getStack() {
         return this.stack.copy();
      }

      public void setStack(ItemStack stack) {
         this.stack = stack;
         this.setter.accept(stack);
      }
   }
}