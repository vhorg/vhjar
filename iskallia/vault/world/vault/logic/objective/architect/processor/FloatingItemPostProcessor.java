package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.entity.FloatingItemEntity;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultObelisk;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class FloatingItemPostProcessor extends VaultPieceProcessor {
   private final int blocksPerSpawn;
   private final WeightedList<ItemStack> itemList;

   public FloatingItemPostProcessor(int blocksPerSpawn, WeightedList<ItemStack> itemList) {
      this.blocksPerSpawn = blocksPerSpawn;
      this.itemList = itemList;
   }

   @Override
   public void postProcess(VaultRaid vault, ServerWorld world, VaultPiece piece, Direction generatedDirection) {
      if (!(piece instanceof VaultObelisk)) {
         AxisAlignedBB box = AxisAlignedBB.func_216363_a(piece.getBoundingBox());
         float size = (float)((box.field_72336_d - box.field_72340_a) * (box.field_72337_e - box.field_72338_b) * (box.field_72334_f - box.field_72339_c));
         float runs = size / this.blocksPerSpawn;

         while (runs > 0.0F && (!(runs < 1.0F) || !(rand.nextFloat() >= runs))) {
            runs--;
            boolean placed = false;

            while (!placed) {
               BlockPos pos = MiscUtils.getRandomPos(box, rand);
               BlockState state = world.func_180495_p(pos);
               if (state.isAir(world, pos)) {
                  placed = true;
                  ItemStack stack = this.itemList.getRandom(rand);
                  if (stack != null) {
                     FloatingItemEntity itemEntity = FloatingItemEntity.create(world, pos, stack.func_77946_l());
                     world.func_217376_c(itemEntity);
                  }
               }
            }
         }
      }
   }
}
