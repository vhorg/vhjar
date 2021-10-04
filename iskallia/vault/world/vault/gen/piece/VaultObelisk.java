package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
import iskallia.vault.block.ObeliskBlock;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VaultObelisk extends VaultPiece {
   public static final ResourceLocation ID = Vault.id("obelisk");

   public VaultObelisk() {
      super(ID);
   }

   public VaultObelisk(ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   public boolean isCompleted(World world) {
      return BlockPos.func_229383_a_(this.getBoundingBox())
         .<BlockState>map(world::func_180495_p)
         .filter(state -> state.func_177230_c() instanceof ObeliskBlock)
         .anyMatch(blockState -> (Integer)blockState.func_177229_b(ObeliskBlock.COMPLETION) == 4);
   }

   @Override
   public void tick(ServerWorld world, VaultRaid vault) {
   }
}
