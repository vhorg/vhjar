package iskallia.vault.fluid;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModFluids;
import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidAttributes.Water;

public abstract class VoidFluid extends FlowingFluid {
   @Nonnull
   public Fluid getFlowing() {
      return (Fluid)ModFluids.FLOWING_VOID_LIQUID.get();
   }

   @Nonnull
   public Fluid getSource() {
      return (Fluid)ModFluids.VOID_LIQUID.get();
   }

   @Nonnull
   public Item getBucket() {
      return ModItems.VOID_LIQUID_BUCKET;
   }

   protected boolean canConvertToSource() {
      return false;
   }

   protected void beforeDestroyingBlock(@Nonnull LevelAccessor world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
   }

   protected int getSlopeFindDistance(@Nonnull LevelReader world) {
      return 2;
   }

   protected int getDropOff(@Nonnull LevelReader world) {
      return 2;
   }

   protected boolean canBeReplacedWith(
      FluidState fluidState, @Nonnull BlockGetter blockReader, @Nonnull BlockPos pos, @Nonnull Fluid fluid, @Nonnull Direction direction
   ) {
      return fluidState.getHeight(blockReader, pos) >= 0.44444445F;
   }

   public int getTickDelay(@Nonnull LevelReader world) {
      return 30;
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public int getSpreadDelay(@Nonnull Level world, BlockPos pos, FluidState p_215667_3_, FluidState p_215667_4_) {
      int i = this.getTickDelay(world);
      if (!p_215667_3_.isEmpty()
         && !p_215667_4_.isEmpty()
         && !(Boolean)p_215667_3_.getValue(FALLING)
         && !(Boolean)p_215667_4_.getValue(FALLING)
         && p_215667_4_.getHeight(world, pos) > p_215667_3_.getHeight(world, pos)
         && world.getRandom().nextInt(4) != 0) {
         i *= 4;
      }

      return i;
   }

   public boolean isSame(Fluid fluid) {
      return fluid == ModFluids.VOID_LIQUID.get() || fluid == ModFluids.FLOWING_VOID_LIQUID.get();
   }

   @Nonnull
   protected BlockState createLegacyBlock(@Nonnull FluidState state) {
      return (BlockState)ModBlocks.VOID_LIQUID_BLOCK.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
   }

   @Nonnull
   protected FluidAttributes createAttributes() {
      return Water.builder(VaultMod.id("block/fluid/void_liquid"), VaultMod.id("block/fluid/flowing_void_liquid"))
         .overlay(new ResourceLocation("block/water_overlay"))
         .translationKey("block.the_vault.void_liquid")
         .density(3000)
         .viscosity(6000)
         .temperature(1300)
         .color(16777215)
         .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
         .build(this);
   }

   public static class Flowing extends VoidFluid {
      protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder) {
         super.createFluidStateDefinition(builder);
         builder.add(new Property[]{LEVEL});
      }

      public int getAmount(FluidState state) {
         return (Integer)state.getValue(LEVEL);
      }

      public boolean isSource(FluidState state) {
         return false;
      }
   }

   public static class Source extends VoidFluid {
      public int getAmount(FluidState state) {
         return 8;
      }

      public boolean isSource(FluidState state) {
         return true;
      }
   }
}
