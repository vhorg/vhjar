package iskallia.vault.item;

import iskallia.vault.container.oversized.OverSizedInvWrapper;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.container.provider.VaultKeyringProvider;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemVaultKeyring extends Item {
   public ItemVaultKeyring(ResourceLocation id) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
      this.setRegistryName(id);
   }

   public void fillItemCategory(CreativeModeTab cat, NonNullList<ItemStack> items) {
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      return InteractionResultHolder.pass(player.getItemInHand(hand));
   }

   public static void openKeyring(ServerPlayer player, int slot, ItemStack keyringStack) {
      VaultKeyringProvider provider = new VaultKeyringProvider(player, slot, keyringStack);
      NetworkHooks.openGui(player, provider, provider.extraDataWriter());
   }

   public static OverSizedInventory getInventory(ItemStack stack) {
      return getInventory(stack, player -> true);
   }

   public static OverSizedInventory getInventory(ItemStack stack, Predicate<Player> stillValidCheck) {
      if (!(stack.getItem() instanceof ItemVaultKeyring)) {
         return OverSizedInventory.EMPTY;
      } else {
         List<OverSizedItemStack> stored = getStoredStacks(stack);
         NonNullList<OverSizedItemStack> nStored = NonNullList.withSize(stored.size(), OverSizedItemStack.EMPTY);
         nStored.addAll(stored);
         return new OverSizedInventory(nStored, stacks -> setStoredStacks(stack, stacks), stillValidCheck);
      }
   }

   public static List<OverSizedItemStack> getStoredStacks(ItemStack stack) {
      if (!(stack.getItem() instanceof ItemVaultKeyring)) {
         return new ArrayList<>();
      } else {
         int keyCount = (int)ForgeRegistries.ITEMS
            .getValues()
            .stream()
            .filter(item -> item instanceof ItemVaultKey)
            .map(item -> (ItemVaultKey)item)
            .filter(ItemVaultKey::isActive)
            .count();
         CompoundTag tag = stack.getOrCreateTag();
         ListTag stacksTag = tag.getList("stacks", 10);
         List<OverSizedItemStack> stacks = new ArrayList<>();

         for (int i = 0; i < Math.max(keyCount, stacksTag.size()); i++) {
            CompoundTag stackTag = stacksTag.getCompound(i);
            stacks.add(OverSizedItemStack.deserialize(stackTag));
         }

         return stacks;
      }
   }

   public static void setStoredStacks(ItemStack stack, List<OverSizedItemStack> stacks) {
      if (stack.getItem() instanceof ItemVaultKeyring) {
         CompoundTag tag = stack.getOrCreateTag();
         ListTag stacksTag = new ListTag();
         stacks.forEach(s -> stacksTag.add(s.serialize()));
         tag.put("stacks", stacksTag);
      }
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return slotChanged || oldStack.getItem() != newStack.getItem();
   }

   public static NonNullSupplier<IItemHandler> getInventorySupplier(final ItemStack stack) {
      return new NonNullSupplier<IItemHandler>() {
         @Nonnull
         public IItemHandler get() {
            return new OverSizedInvWrapper(ItemVaultKeyring.getInventory(stack));
         }
      };
   }

   @Nullable
   public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable CompoundTag nbt) {
      return new ICapabilityProvider() {
         @Nonnull
         public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
               ? LazyOptional.of(ItemVaultKeyring.getInventorySupplier(stack)).cast()
               : LazyOptional.empty();
         }
      };
   }
}
