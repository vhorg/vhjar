package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
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

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      BlockHitResult blockhitresult = getPlayerPOVHitResult(pLevel, pPlayer, net.minecraft.world.level.ClipContext.Fluid.NONE);
      BlockPos blockpos = blockhitresult.getBlockPos();
      BlockState state = pLevel.getBlockState(blockpos);
      if (!(state.getBlock() instanceof CauldronBlock) && !(state.getBlock() instanceof LayeredCauldronBlock)) {
         InteractionResultHolder<ItemStack> result = super.use(pLevel, pPlayer, pHand);
         return new InteractionResultHolder(result.getResult(), new ItemStack(ModItems.INFINITE_WATER_BUCKET));
      } else {
         pLevel.setBlock(blockpos, (BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), 3);
         if (pPlayer instanceof ServerPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)pPlayer, blockpos, itemstack);
         }

         pLevel.playSound(null, blockpos, this.getFluid().getAttributes().getEmptySound(), SoundSource.BLOCKS, 1.0F, 1.0F);
         pPlayer.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.success(new ItemStack(ModItems.INFINITE_WATER_BUCKET));
      }
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
