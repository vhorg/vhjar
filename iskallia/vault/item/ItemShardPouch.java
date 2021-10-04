package iskallia.vault.item;

import iskallia.vault.container.inventory.ShardPouchContainer;
import iskallia.vault.init.ModItems;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemShardPouch extends Item {
   public ItemShardPouch(ResourceLocation id) {
      super(new Properties().func_200917_a(1).func_200916_a(ModItems.VAULT_MOD_GROUP));
      this.setRegistryName(id);
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      ItemStack contained = getContainedStack(stack);
      if (!contained.func_190926_b()) {
         int count = contained.func_190916_E();
         tooltip.add(new StringTextComponent(count + " Shard" + (count > 1 ? "s" : "")).func_240699_a_(TextFormatting.GOLD));
      } else {
         tooltip.add(new StringTextComponent("Empty").func_240699_a_(TextFormatting.GOLD));
      }
   }

   public static int getShardCount(PlayerInventory playerInventory) {
      int shards = 0;

      for (int slot = 0; slot < playerInventory.func_70302_i_(); slot++) {
         ItemStack stack = playerInventory.func_70301_a(slot);
         if (stack.func_77973_b() instanceof ItemShardPouch) {
            shards += getContainedStack(stack).func_190916_E();
         } else if (stack.func_77973_b() == ModItems.SOUL_SHARD) {
            shards += stack.func_190916_E();
         }
      }

      return shards;
   }

   public static boolean reduceShardAmount(PlayerInventory playerInventory, int count, boolean simulate) {
      for (int slot = 0; slot < playerInventory.func_70302_i_(); slot++) {
         ItemStack stack = playerInventory.func_70301_a(slot);
         if (stack.func_77973_b() instanceof ItemShardPouch) {
            ItemStack shardStack = getContainedStack(stack);
            int toReduce = Math.min(count, shardStack.func_190916_E());
            if (!simulate) {
               shardStack.func_190920_e(shardStack.func_190916_E() - toReduce);
               setContainedStack(stack, shardStack);
            }

            count -= toReduce;
         } else if (stack.func_77973_b() == ModItems.SOUL_SHARD) {
            int toReduce = Math.min(count, stack.func_190916_E());
            if (!simulate) {
               stack.func_190918_g(toReduce);
               playerInventory.func_70299_a(slot, stack);
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
      CompoundNBT invTag = pouch.func_190925_c("Inventory");
      ItemStack stack = ItemStack.func_199557_a(invTag.func_74775_l("Stack"));
      stack.func_190920_e(invTag.func_74762_e("StackSize"));
      return stack;
   }

   public static void setContainedStack(ItemStack pouch, ItemStack contained) {
      CompoundNBT invTag = pouch.func_190925_c("Inventory");
      invTag.func_218657_a("Stack", contained.serializeNBT());
      invTag.func_74768_a("StackSize", contained.func_190916_E());
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      ItemStack stack = player.func_184586_b(hand);
      if (!world.func_201670_d() && player instanceof ServerPlayerEntity) {
         final int pouchSlot;
         if (hand == Hand.OFF_HAND) {
            pouchSlot = 40;
         } else {
            pouchSlot = player.field_71071_by.field_70461_c;
         }

         NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
            public ITextComponent func_145748_c_() {
               return new StringTextComponent("Shard Pouch");
            }

            @Nullable
            public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity playerx) {
               return new ShardPouchContainer(windowId, inventory, pouchSlot);
            }
         }, buf -> buf.writeInt(pouchSlot));
      }

      return ActionResult.func_226250_c_(stack);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return oldStack.func_77973_b() != newStack.func_77973_b();
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
   public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable CompoundNBT nbt) {
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
         return stack.func_77973_b() == ModItems.SOUL_SHARD;
      }
   }
}
