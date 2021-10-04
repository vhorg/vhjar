package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class VaultDoorBlock extends DoorBlock {
   public static final List<VaultDoorBlock> VAULT_DOORS = new ArrayList<>();
   protected Item keyItem;

   public VaultDoorBlock(Item keyItem) {
      super(
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151648_G)
            .func_200948_a(-1.0F, 3600000.0F)
            .func_200947_a(SoundType.field_185852_e)
            .func_226896_b_()
      );
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.func_176194_O().func_177621_b())
                        .func_206870_a(field_176520_a, Direction.NORTH))
                     .func_206870_a(field_176519_b, Boolean.FALSE))
                  .func_206870_a(field_176521_M, DoorHingeSide.LEFT))
               .func_206870_a(field_176522_N, Boolean.FALSE))
            .func_206870_a(field_176523_O, DoubleBlockHalf.LOWER)
      );
      this.keyItem = keyItem;
      VAULT_DOORS.add(this);
   }

   public Item getKeyItem() {
      return this.keyItem;
   }

   public PushReaction func_149656_h(BlockState state) {
      return PushReaction.BLOCK;
   }

   public void func_220069_a(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.VAULT_DOOR_TILE_ENTITY.func_200968_a();
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      ItemStack heldStack = player.func_184586_b(hand);
      Boolean isOpen = (Boolean)state.func_177229_b(field_176519_b);
      if (!isOpen && heldStack.func_77973_b() == this.getKeyItem()) {
         heldStack.func_190918_g(1);
         this.func_242663_a(world, state, pos, true);
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.SUCCESS;
      }
   }
}
