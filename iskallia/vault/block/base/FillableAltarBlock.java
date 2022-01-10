package iskallia.vault.block.base;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.world.data.PlayerFavourData;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class FillableAltarBlock<T extends FillableAltarTileEntity> extends FacedBlock {
   protected static final Random rand = new Random();
   public static final float FAVOUR_CHANCE = 0.05F;
   public static final VoxelShape SHAPE = Block.func_208617_a(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);

   public FillableAltarBlock() {
      super(Properties.func_200945_a(Material.field_151576_e).func_200948_a(-1.0F, 3600000.0F).func_222380_e().func_226896_b_());
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   public abstract T createTileEntity(BlockState var1, IBlockReader var2);

   public abstract IParticleData getFlameParticle();

   public abstract PlayerFavourData.VaultGodType getAssociatedVaultGod();

   public abstract ActionResultType rightClicked(BlockState var1, ServerWorld var2, BlockPos var3, T var4, ServerPlayerEntity var5, ItemStack var6);

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (world.field_72995_K) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity tileEntity = world.func_175625_s(pos);
         ItemStack heldStack = player.func_184586_b(hand);
         if (tileEntity != null) {
            try {
               if (((FillableAltarTileEntity)tileEntity).isMaxedOut()) {
                  world.func_175656_a(pos, this.getSuccessChestState(state));
                  return ActionResultType.SUCCESS;
               }

               return this.rightClicked(state, (ServerWorld)world, pos, (T)tileEntity, (ServerPlayerEntity)player, heldStack);
            } catch (ClassCastException var10) {
            }
         }

         return ActionResultType.FAIL;
      }
   }

   protected BlockState getSuccessChestState(BlockState altarState) {
      return (BlockState)ModBlocks.VAULT_ALTAR_CHEST.func_176223_P().func_206870_a(ChestBlock.field_176459_a, altarState.func_177229_b(FACING));
   }

   public static float getFavourChance(PlayerEntity player, PlayerFavourData.VaultGodType favourType) {
      ItemStack offHand = player.func_184586_b(Hand.OFF_HAND);
      if (offHand.func_190926_b() || !(offHand.func_77973_b() instanceof IdolItem)) {
         return 0.05F;
      } else if (favourType != ((IdolItem)offHand.func_77973_b()).getType()) {
         return 0.05F;
      } else {
         int multiplier = 2;
         if (ModAttributes.IDOL_AUGMENTED.exists(offHand)) {
            multiplier = 3;
         }

         return 0.05F * multiplier;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_180655_c(BlockState stateIn, World world, BlockPos pos, Random rand) {
      this.addFlameParticle(world, pos, 1.0, 17.0, 15.0);
      this.addFlameParticle(world, pos, 15.0, 17.0, 15.0);
      this.addFlameParticle(world, pos, 15.0, 17.0, 1.0);
      this.addFlameParticle(world, pos, 1.0, 17.0, 1.0);
   }

   @OnlyIn(Dist.CLIENT)
   public void addFlameParticle(World world, BlockPos pos, double xOffset, double yOffset, double zOffset) {
      double x = pos.func_177958_n() + xOffset / 16.0;
      double y = pos.func_177956_o() + yOffset / 16.0;
      double z = pos.func_177952_p() + zOffset / 16.0;
      world.func_195594_a(this.getFlameParticle(), x, y, z, 0.0, 0.0, 0.0);
   }
}
