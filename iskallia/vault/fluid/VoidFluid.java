package iskallia.vault.fluid;

import iskallia.vault.Vault;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModFluids;
import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidAttributes.Water;

public abstract class VoidFluid extends FlowingFluid {
   @Nonnull
   public Fluid func_210197_e() {
      return (Fluid)ModFluids.FLOWING_VOID_LIQUID.get();
   }

   @Nonnull
   public Fluid func_210198_f() {
      return (Fluid)ModFluids.VOID_LIQUID.get();
   }

   @Nonnull
   public Item func_204524_b() {
      return ModItems.VOID_LIQUID_BUCKET;
   }

   protected boolean func_205579_d() {
      return false;
   }

   protected void func_205580_a(@Nonnull IWorld world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
   }

   protected int func_185698_b(@Nonnull IWorldReader world) {
      return 2;
   }

   protected int func_204528_b(@Nonnull IWorldReader world) {
      return 2;
   }

   protected boolean func_215665_a(
      FluidState fluidState, @Nonnull IBlockReader blockReader, @Nonnull BlockPos pos, @Nonnull Fluid fluid, @Nonnull Direction direction
   ) {
      return fluidState.func_215679_a(blockReader, pos) >= 0.44444445F;
   }

   public int func_205569_a(@Nonnull IWorldReader world) {
      return 30;
   }

   protected float func_210195_d() {
      return 100.0F;
   }

   public int func_215667_a(@Nonnull World world, BlockPos pos, FluidState p_215667_3_, FluidState p_215667_4_) {
      int i = this.func_205569_a(world);
      if (!p_215667_3_.func_206888_e()
         && !p_215667_4_.func_206888_e()
         && !(Boolean)p_215667_3_.func_177229_b(field_207209_a)
         && !(Boolean)p_215667_4_.func_177229_b(field_207209_a)
         && p_215667_4_.func_215679_a(world, pos) > p_215667_3_.func_215679_a(world, pos)
         && world.func_201674_k().nextInt(4) != 0) {
         i *= 4;
      }

      return i;
   }

   public boolean func_207187_a(Fluid fluid) {
      return fluid == ModFluids.VOID_LIQUID.get() || fluid == ModFluids.FLOWING_VOID_LIQUID.get();
   }

   @Nonnull
   protected BlockState func_204527_a(@Nonnull FluidState state) {
      return (BlockState)ModBlocks.VOID_LIQUID_BLOCK.func_176223_P().func_206870_a(FlowingFluidBlock.field_176367_b, func_207205_e(state));
   }

   @Nonnull
   protected FluidAttributes createAttributes() {
      return Water.builder(Vault.id("block/fluid/void_liquid"), Vault.id("block/fluid/flowing_void_liquid"))
         .overlay(new ResourceLocation("block/water_overlay"))
         .translationKey("block.the_vault.void_liquid")
         .density(3000)
         .viscosity(6000)
         .temperature(1300)
         .color(16777215)
         .sound(SoundEvents.field_187630_M, SoundEvents.field_187624_K)
         .build(this);
   }

   public static class Flowing extends VoidFluid {
      protected void func_207184_a(Builder<Fluid, FluidState> builder) {
         super.func_207184_a(builder);
         builder.func_206894_a(new Property[]{field_207210_b});
      }

      public int func_207192_d(FluidState state) {
         return (Integer)state.func_177229_b(field_207210_b);
      }

      public boolean func_207193_c(FluidState state) {
         return false;
      }
   }

   public static class Source extends VoidFluid {
      public int func_207192_d(FluidState state) {
         return 8;
      }

      public boolean func_207193_c(FluidState state) {
         return true;
      }
   }
}
