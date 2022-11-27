package iskallia.vault.item;

import iskallia.vault.container.inventory.ShardPouchContainer;
import iskallia.vault.init.ModItems;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

public class ItemShardPouch extends Item {
   public ItemShardPouch(ResourceLocation id) {
      super(new Properties().stacksTo(1).tab(ModItems.VAULT_MOD_GROUP));
      this.setRegistryName(id);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      ItemStack contained = getContainedStack(stack);
      if (!contained.isEmpty()) {
         int count = contained.getCount();
         tooltip.add(new TextComponent(count + " Shard" + (count > 1 ? "s" : "")).withStyle(ChatFormatting.GOLD));
      } else {
         tooltip.add(new TextComponent("Empty").withStyle(ChatFormatting.GOLD));
      }
   }

   public static int getShardCount(Player player) {
      return getShardCount(player.getInventory());
   }

   public static int getShardCount(Inventory playerInventory) {
      int shards = 0;

      for (int slot = 0; slot < playerInventory.getContainerSize(); slot++) {
         ItemStack stack = playerInventory.getItem(slot);
         if (stack.getItem() instanceof ItemShardPouch) {
            shards += getContainedStack(stack).getCount();
         } else if (stack.getItem() == ModItems.SOUL_SHARD) {
            shards += stack.getCount();
         }
      }

      return shards;
   }

   public static boolean reduceShardAmount(Inventory playerInventory, int count, boolean simulate) {
      for (int slot = 0; slot < playerInventory.getContainerSize(); slot++) {
         ItemStack stack = playerInventory.getItem(slot);
         if (stack.getItem() instanceof ItemShardPouch) {
            ItemStack shardStack = getContainedStack(stack);
            int toReduce = Math.min(count, shardStack.getCount());
            if (!simulate) {
               shardStack.setCount(shardStack.getCount() - toReduce);
               setContainedStack(stack, shardStack);
            }

            count -= toReduce;
         } else if (stack.getItem() == ModItems.SOUL_SHARD) {
            int toReduce = Math.min(count, stack.getCount());
            if (!simulate) {
               stack.shrink(toReduce);
               playerInventory.setItem(slot, stack);
            }

            count -= toReduce;
         }

         if (count <= 0) {
            return true;
         }
      }

      return false;
   }

   public static ItemStack getContainedStack(ItemStack pouch) {
      CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
      int count = invTag.getInt("StackSize");
      return count > 0 ? new ItemStack(ModItems.SOUL_SHARD, count) : ItemStack.EMPTY;
   }

   public static void setContainedStack(ItemStack pouch, ItemStack contained) {
      CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
      invTag.putInt("StackSize", contained.getCount());
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!world.isClientSide() && player instanceof ServerPlayer) {
         final int pouchSlot;
         if (hand == InteractionHand.OFF_HAND) {
            pouchSlot = 40;
         } else {
            pouchSlot = player.getInventory().selected;
         }

         NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
            public Component getDisplayName() {
               return new TextComponent("Shard Pouch");
            }

            @Nullable
            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player playerx) {
               return new ShardPouchContainer(windowId, inventory, pouchSlot);
            }
         }, buf -> buf.writeInt(pouchSlot));
      }

      return InteractionResultHolder.pass(stack);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return oldStack.getItem() != newStack.getItem();
   }

   public static NonNullSupplier<IItemHandler> getInventorySupplier(final ItemStack stack) {
      return new NonNullSupplier<IItemHandler>() {
         @Nonnull
         public IItemHandler get() {
            return new ItemShardPouch.Handler(stack);
         }
      };
   }

   @Nullable
   public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable CompoundTag nbt) {
      return new ICapabilityProvider() {
         @Nonnull
         public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
               ? LazyOptional.of(ItemShardPouch.getInventorySupplier(stack)).cast()
               : LazyOptional.empty();
         }
      };
   }

   public static class Handler extends ItemStackHandler {
      private final ItemStack delegate;

      public Handler(ItemStack delegate) {
         super(1);
         this.delegate = delegate;
         this.stacks.set(0, ItemShardPouch.getContainedStack(this.delegate));
      }

      protected void onContentsChanged(int slot) {
         super.onContentsChanged(slot);
         ItemShardPouch.setContainedStack(this.delegate, this.getStackInSlot(0));
      }

      public int getSlotLimit(int slot) {
         return 2147483582;
      }

      protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
         return this.getSlotLimit(slot);
      }

      public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
         return stack.getItem() == ModItems.SOUL_SHARD;
      }
   }
}
