package iskallia.vault.world.vault.logic.objective.architect;

import iskallia.vault.block.entity.StabilizerTileEntity;
import java.util.Collection;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class SummonAndKillBossesVotingSession extends VotingSession {
   private Direction votedDirection = null;

   SummonAndKillBossesVotingSession(BlockPos stabilizerPos, Collection<DirectionChoice> directions) {
      super(stabilizerPos, directions);
   }

   SummonAndKillBossesVotingSession(CompoundNBT tag) {
      super(tag);
      if (tag.func_150297_b("votedDirection", 3)) {
         this.votedDirection = Direction.values()[tag.func_74762_e("votedDirection")];
      }
   }

   @Override
   protected void setStabilizerActive(StabilizerTileEntity tile) {
      super.setStabilizerActive(tile);
      tile.setHighlightDirections(this.getDirections().stream().map(DirectionChoice::getDirection).collect(Collectors.toList()));
   }

   @Override
   public boolean isFinished() {
      return this.votedDirection != null;
   }

   @Override
   public float getChoicePercentage(DirectionChoice choice) {
      return 0.0F;
   }

   public void setVotedDirection(Direction votedDirection) {
      for (DirectionChoice dir : this.getDirections()) {
         if (dir.getDirection() == votedDirection) {
            this.votedDirection = votedDirection;
            break;
         }
      }
   }

   @Override
   public DirectionChoice getVotedDirection() {
      if (this.votedDirection != null) {
         for (DirectionChoice dir : this.getDirections()) {
            if (dir.getDirection() == this.votedDirection) {
               return dir;
            }
         }
      }

      return super.getVotedDirection();
   }

   @Override
   public CompoundNBT serialize() {
      CompoundNBT nbt = super.serialize();
      nbt.func_74757_a("isFinal", true);
      if (this.votedDirection != null) {
         nbt.func_74768_a("votedDirection", this.votedDirection.ordinal());
      }

      return nbt;
   }
}
