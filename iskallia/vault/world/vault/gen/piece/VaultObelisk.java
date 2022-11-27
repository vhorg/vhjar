package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.VaultMod;
import iskallia.vault.block.ObeliskBlock;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class VaultObelisk extends VaultPiece {
   public static final ResourceLocation ID = VaultMod.id("obelisk");

   public VaultObelisk() {
      super(ID);
   }

   public VaultObelisk(ResourceLocation template, BoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   public boolean isCompleted(Level world) {
      return BlockPos.betweenClosedStream(this.getBoundingBox())
         .<BlockState>map(world::getBlockState)
         .filter(state -> state.getBlock() instanceof ObeliskBlock)
         .anyMatch(blockState -> (Boolean)blockState.getValue(ObeliskBlock.FILLED));
   }

   @Override
   public void tick(ServerLevel world, VaultRaid vault) {
   }
}
