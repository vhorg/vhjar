package iskallia.vault.item.tool;

import net.minecraft.core.BlockPos;

public class HammerTile {
   public boolean isDestroyingBlock;
   public int destroyProgressStart;
   public BlockPos destroyPos;
   public int lastSentState;

   public HammerTile(boolean isDestroyingBlock, int destroyProgressStart, BlockPos destroyPos, int lastSentState) {
      this.isDestroyingBlock = isDestroyingBlock;
      this.destroyProgressStart = destroyProgressStart;
      this.destroyPos = destroyPos;
      this.lastSentState = lastSentState;
   }
}
