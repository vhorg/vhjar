package iskallia.vault.block;

import iskallia.vault.block.entity.StatueCauldronTileEntity;
import iskallia.vault.client.gui.screen.StatueCauldronScreen;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class StatueCauldronBlock extends CauldronBlock {
   public StatueCauldronBlock() {
      super(Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151648_G).func_235861_h_().func_200948_a(3.0F, 3600000.0F).func_226896_b_());
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.STATUE_CAULDRON_TILE_ENTITY.func_200968_a();
   }

   public ActionResultType func_225533_a_(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      ItemStack itemstack = player.func_184586_b(handIn);
      if (itemstack.func_190926_b()) {
         if (worldIn.field_72995_K && handIn == Hand.MAIN_HAND) {
            this.openStatueScreen(worldIn, pos);
         }

         return ActionResultType.PASS;
      } else {
         int i = (Integer)state.func_177229_b(field_176591_a);
         Item item = itemstack.func_77973_b();
         if (item instanceof BucketItem && ((BucketItem)item).getFluid() != Fluids.field_204541_a) {
            if (i < 3 && !worldIn.field_72995_K) {
               if (!player.func_184812_l_()) {
                  LazyOptional<IFluidHandlerItem> providerOptional = itemstack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
                  providerOptional.ifPresent(provider -> provider.drain(1000, FluidAction.EXECUTE));
               }

               player.func_195066_a(Stats.field_188077_K);
               worldIn.func_180501_a(pos, (BlockState)state.func_206870_a(field_176591_a, 3), 3);
               worldIn.func_175666_e(pos, this);
               worldIn.func_184133_a(null, pos, SoundEvents.field_187624_K, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return ActionResultType.func_233537_a_(worldIn.field_72995_K);
         } else {
            return super.func_225533_a_(state, worldIn, pos, player, handIn, hit);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void openStatueScreen(World worldIn, BlockPos pos) {
      Minecraft mc = Minecraft.func_71410_x();
      mc.func_147108_a(new StatueCauldronScreen((ClientWorld)worldIn, pos));
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      BlockState toPlace = this.func_176223_P();
      ItemStack stack = context.func_195996_i();
      if (stack.func_77942_o() && stack.func_77978_p().func_150297_b("BlockEntityTag", 10)) {
         int cauldronLevel = stack.func_179543_a("BlockEntityTag").func_74762_e("Level");
         return (BlockState)toPlace.func_206870_a(field_176591_a, cauldronLevel);
      } else {
         return toPlace;
      }
   }

   public void func_180633_a(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (!worldIn.field_72995_K && placer instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)placer;
         TileEntity te = worldIn.func_175625_s(pos);
         if (te instanceof StatueCauldronTileEntity) {
            StatueCauldronTileEntity cauldron = (StatueCauldronTileEntity)te;
            if (stack.func_196082_o().func_74764_b("BlockEntityTag")) {
               CompoundNBT cauldronNbt = stack.func_179543_a("BlockEntityTag");
               cauldron.setOwner(cauldronNbt.func_186857_a("Owner"));
               cauldron.setRequiredAmount(cauldronNbt.func_74762_e("RequiredAmount"));
               cauldron.setStatueCount(cauldronNbt.func_74762_e("StatueCount"));
               cauldron.setNames(cauldronNbt.func_150295_c("NameList", 10));
            } else {
               cauldron.setOwner(player.func_110124_au());
               cauldron.setRequiredAmount(ModConfigs.STATUE_RECYCLING.getPlayerRequirement(player.func_145748_c_().getString()));
            }

            cauldron.sendUpdates();
            cauldron.func_70296_d();
         }
      }
   }

   public void func_176208_a(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!world.field_72995_K) {
         TileEntity tileEntity = world.func_175625_s(pos);
         ItemStack itemStack = new ItemStack(this.getBlock());
         if (tileEntity instanceof StatueCauldronTileEntity) {
            StatueCauldronTileEntity cauldron = (StatueCauldronTileEntity)tileEntity;
            CompoundNBT statueNBT = cauldron.serializeNBT();
            CompoundNBT stackNBT = itemStack.func_196082_o();
            statueNBT.func_74768_a("Level", (Integer)state.func_177229_b(field_176591_a));
            stackNBT.func_218657_a("BlockEntityTag", statueNBT);
            itemStack.func_77982_d(stackNBT);
         }

         ItemEntity itemEntity = new ItemEntity(world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, itemStack);
         itemEntity.func_174869_p();
         world.func_217376_c(itemEntity);
      }

      super.func_176208_a(world, pos, state, player);
   }
}
