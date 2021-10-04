package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
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
      super(() -> Fluids.field_204546_a, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
      this.setRegistryName(id);
   }

   public Fluid getFluid() {
      return Fluids.field_204546_a;
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.func_184586_b(hand);
      BlockRayTraceResult rayTraceResult = func_219968_a(world, player, FluidMode.NONE);
      ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, itemStack, rayTraceResult);
      if (ret != null) {
         return ret;
      } else if (rayTraceResult.func_216346_c() == Type.MISS) {
         return ActionResult.func_226250_c_(itemStack);
      } else if (rayTraceResult.func_216346_c() != Type.BLOCK) {
         return ActionResult.func_226250_c_(itemStack);
      } else {
         BlockPos pos = rayTraceResult.func_216350_a();
         Direction direction = rayTraceResult.func_216354_b();
         if (world.func_175660_a(player, pos) && player.func_175151_a(pos, direction, itemStack)) {
            BlockState state = world.func_180495_p(pos);
            if (state.func_203425_a(Blocks.field_150383_bp)) {
               int cauldronLevel = (Integer)state.func_177229_b(CauldronBlock.field_176591_a);
               if (cauldronLevel < 3) {
                  player.func_195066_a(Stats.field_188077_K);
                  world.func_180501_a(pos, (BlockState)state.func_206870_a(CauldronBlock.field_176591_a, 3), 3);
                  world.func_175666_e(pos, state.func_177230_c());
                  world.func_184133_a(null, pos, SoundEvents.field_187624_K, SoundCategory.BLOCKS, 1.0F, 1.0F);
               }

               return ActionResult.func_226248_a_(itemStack);
            } else {
               return super.func_77659_a(world, player, hand);
            }
         } else {
            return ActionResult.func_226251_d_(itemStack);
         }
      }
   }

   public boolean func_77616_k(ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   protected ItemStack func_203790_a(ItemStack stack, PlayerEntity player) {
      return stack;
   }

   public ItemStack getContainerItem(ItemStack itemStack) {
      return new ItemStack(ModItems.INFINITE_WATER_BUCKET);
   }

   public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
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
         return new FluidStack(Fluids.field_204546_a, 1000);
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
         return !resource.isEmpty() && resource.getFluid() == Fluids.field_204546_a
            ? new FluidStack(Fluids.field_204546_a, resource.getAmount())
            : FluidStack.EMPTY;
      }

      @Nonnull
      public FluidStack drain(int maxDrain, FluidAction action) {
         return new FluidStack(Fluids.field_204546_a, maxDrain);
      }

      @Nonnull
      public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
         return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, this.holder);
      }
   }
}
