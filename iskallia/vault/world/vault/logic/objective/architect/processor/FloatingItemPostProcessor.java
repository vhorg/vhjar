package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.entity.entity.FloatingItemEntity;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultObelisk;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FloatingItemPostProcessor extends VaultPieceProcessor {
   private final int blocksPerSpawn;
   private final WeightedList<ItemStack> itemList;

   public FloatingItemPostProcessor(int blocksPerSpawn, WeightedList<ItemStack> itemList) {
      this.blocksPerSpawn = blocksPerSpawn;
      this.itemList = itemList;
   }

   @Override
   public void postProcess(VaultRaid vault, ServerLevel world, VaultPiece piece, Direction generatedDirection) {
      if (!(piece instanceof VaultObelisk)) {
         AABB box = AABB.of(piece.getBoundingBox());
         float size = (float)((box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ));
         float runs = size / this.blocksPerSpawn;

         while (runs > 0.0F && (!(runs < 1.0F) || !(rand.nextFloat() >= runs))) {
            runs--;
            boolean placed = false;

            while (!placed) {
               BlockPos pos = MiscUtils.getRandomPos(box, rand);
               BlockState state = world.getBlockState(pos);
               if (state.isAir()) {
                  placed = true;
                  ItemStack stack = this.itemList.getRandom(rand);
                  if (stack != null) {
                     FloatingItemEntity itemEntity = FloatingItemEntity.create(world, pos, stack.copy());
                     world.addFreshEntity(itemEntity);
                  }
               }
            }
         }
      }
   }
}
