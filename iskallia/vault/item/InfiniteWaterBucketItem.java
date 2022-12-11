package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class InfiniteWaterBucketItem extends BucketItem {
   public InfiniteWaterBucketItem(ResourceLocation id) {
      super(() -> Fluids.WATER, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
      this.setRegistryName(id);
   }

   public Fluid getFluid() {
      return Fluids.WATER;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      BlockHitResult rayTraceResult = getPlayerPOVHitResult(world, player, net.minecraft.world.level.ClipContext.Fluid.NONE);
      InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, itemStack, rayTraceResult);
      if (ret != null) {
         return ret;
      } else if (rayTraceResult.getType() == Type.MISS) {
         return InteractionResultHolder.pass(itemStack);
      } else if (rayTraceResult.getType() != Type.BLOCK) {
         return InteractionResultHolder.pass(itemStack);
      } else {
         BlockPos pos = rayTraceResult.getBlockPos();
         Direction direction = rayTraceResult.getDirection();
         if (world.mayInteract(player, pos) && player.mayUseItemAt(pos, direction, itemStack)) {
            BlockState state = world.getBlockState(pos);
            if (state.is(Blocks.CAULDRON)) {
               int cauldronLevel = (Integer)state.getValue(BlockStateProperties.LEVEL_CAULDRON);
               if (cauldronLevel < 3) {
                  player.awardStat(Stats.FILL_CAULDRON);
                  world.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.LEVEL_CAULDRON, 3), 3);
                  world.updateNeighbourForOutputSignal(pos, state.getBlock());
                  world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               }

               return InteractionResultHolder.success(itemStack);
            } else {
               InteractionResultHolder<ItemStack> result = super.use(world, player, hand);
               return result.getResult() != InteractionResult.SUCCESS && result.getResult() != InteractionResult.CONSUME
                  ? result
                  : InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
            }
         } else {
            return InteractionResultHolder.fail(itemStack);
         }
      }
   }

   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   public ItemStack getContainerItem(ItemStack itemStack) {
      return new ItemStack(ModItems.INFINITE_WATER_BUCKET);
   }

   public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
      return new InfiniteWaterBucketItem.InfiniteWaterBucketHandler(stack);
   }

   public static class InfiniteWaterBucketHandler implements IFluidHandlerItem, ICapabilityProvider {
      private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);
      protected ItemStack container;

      public InfiniteWaterBucketHandler(@Nonnull ItemStack container) {
         this.container = container;
      }

      @Nonnull
      public ItemStack getContainer() {
         return this.container;
      }

      public int getTanks() {
         return 1;
      }

      @Nonnull
      public FluidStack getFluidInTank(int tank) {
         return new FluidStack(Fluids.WATER, 1000);
      }

      public int getTankCapacity(int tank) {
         return 1000;
      }

      public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
         return false;
      }

      public int fill(FluidStack resource, FluidAction action) {
         return 0;
      }

      @Nonnull
      public FluidStack drain(FluidStack resource, FluidAction action) {
         return !resource.isEmpty() && resource.getFluid() == Fluids.WATER ? new FluidStack(Fluids.WATER, resource.getAmount()) : FluidStack.EMPTY;
      }

      @Nonnull
      public FluidStack drain(int maxDrain, FluidAction action) {
         return new FluidStack(Fluids.WATER, maxDrain);
      }

      @Nonnull
      public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
         return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, this.holder);
      }
   }
}
