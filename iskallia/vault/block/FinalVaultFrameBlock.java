package iskallia.vault.block;

import iskallia.vault.block.entity.FinalVaultFrameTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class FinalVaultFrameBlock extends Block {
   public static final DirectionProperty FACING = HorizontalBlock.field_185512_D;

   public FinalVaultFrameBlock() {
      super(Properties.func_200945_a(Material.field_151576_e).func_200947_a(SoundType.field_185851_d).func_200948_a(2.0F, 3600000.0F).func_226896_b_());
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING});
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f().func_176734_d());
   }

   @SubscribeEvent
   public static void onBlockHit(LeftClickBlock event) {
      if (event.isCancelable()) {
         PlayerEntity player = event.getPlayer();
         if (!player.func_184812_l_()) {
            FinalVaultFrameTileEntity tileEntity = FinalVaultFrameTileEntity.get(player.field_70170_p, event.getPos());
            if (tileEntity != null) {
               if (!tileEntity.getOwnerUUID().equals(player.func_110124_au())) {
                  event.setCanceled(true);
               }
            }
         }
      }
   }

   public boolean isToolEffective(BlockState state, ToolType tool) {
      return tool == ToolType.PICKAXE;
   }

   @Nonnull
   public PushReaction func_149656_h(@Nonnull BlockState state) {
      return PushReaction.BLOCK;
   }

   public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
      ItemStack itemStack = new ItemStack(this.getBlock());
      FinalVaultFrameTileEntity tileEntity = FinalVaultFrameTileEntity.get(world, pos);
      CompoundNBT entityNBT = new CompoundNBT();
      if (tileEntity != null) {
         tileEntity.writeToEntityTag(entityNBT);
      }

      itemStack.func_196082_o().func_218657_a("BlockEntityTag", entityNBT);
      return itemStack;
   }

   public void func_180633_a(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
      if (!world.func_201670_d()) {
         CompoundNBT tag = stack.func_179543_a("BlockEntityTag");
         if (tag != null) {
            FinalVaultFrameTileEntity tileEntity = FinalVaultFrameTileEntity.get(world, pos);
            if (tileEntity != null) {
               tileEntity.loadFromNBT(tag);
               super.func_180633_a(world, pos, state, placer, stack);
            }
         }
      }
   }

   public void func_176208_a(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity player) {
      if (!world.field_72995_K && !player.func_184812_l_()) {
         FinalVaultFrameTileEntity tileEntity = FinalVaultFrameTileEntity.get(world, pos);
         if (tileEntity != null) {
            ItemStack itemStack = new ItemStack(this.getBlock());
            CompoundNBT entityNBT = new CompoundNBT();
            tileEntity.writeToEntityTag(entityNBT);
            itemStack.func_196082_o().func_218657_a("BlockEntityTag", entityNBT);
            ItemEntity itemEntity = new ItemEntity(world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, itemStack);
            itemEntity.func_174869_p();
            world.func_217376_c(itemEntity);
         }
      }

      super.func_176208_a(world, pos, state, player);
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.FINAL_VAULT_FRAME_TILE_ENTITY.func_200968_a();
   }
}
