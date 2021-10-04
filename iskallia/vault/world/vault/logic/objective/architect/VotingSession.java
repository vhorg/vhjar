package iskallia.vault.world.vault.logic.objective.architect;

import iskallia.vault.block.entity.StabilizerTileEntity;
import iskallia.vault.util.CodecUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class VotingSession {
   private static final int VOTING_DURATION = 410;
   private final BlockPos stabilizerPos;
   private final Set<String> voted = new HashSet<>();
   private final List<DirectionChoice> directions = new ArrayList<>();
   private int voteTicks;

   VotingSession(BlockPos stabilizerPos, Collection<DirectionChoice> directions) {
      this.stabilizerPos = stabilizerPos;
      this.voteTicks = 410;
      this.directions.addAll(directions);
   }

   VotingSession(CompoundNBT tag) {
      this.stabilizerPos = CodecUtils.readNBT(BlockPos.field_239578_a_, tag, "pos", BlockPos.field_177992_a);
      this.voteTicks = tag.func_74762_e("voteTicks");
      ListNBT directions = tag.func_150295_c("directions", 10);

      for (int i = 0; i < directions.size(); i++) {
         this.directions.add(new DirectionChoice(directions.func_150305_b(i)));
      }
   }

   boolean acceptVote(String voter, Direction dir) {
      if (this.voted.add(voter)) {
         for (DirectionChoice choice : this.directions) {
            if (choice.getDirection() == dir) {
               choice.addVote();
               return true;
            }
         }
      }

      return false;
   }

   void tick(ServerWorld world) {
      if (!this.isFinished()) {
         this.voteTicks--;
         if (world.func_175667_e(this.getStabilizerPos())) {
            TileEntity tile = world.func_175625_s(this.getStabilizerPos());
            if (tile instanceof StabilizerTileEntity) {
               ((StabilizerTileEntity)tile).setActive();
            }
         }
      }
   }

   public BlockPos getStabilizerPos() {
      return this.stabilizerPos;
   }

   public boolean isFinished() {
      return this.voteTicks <= 0;
   }

   public int getTotalVoteTicks() {
      return 410;
   }

   public int getRemainingVoteTicks() {
      return this.voteTicks;
   }

   public List<DirectionChoice> getDirections() {
      return this.directions;
   }

   public float getChoicePercentage(DirectionChoice choice) {
      float total = 0.0F;

      for (DirectionChoice anyChoice : this.getDirections()) {
         total += anyChoice.getVotes();
      }

      return choice.getVotes() / total;
   }

   public DirectionChoice getVotedDirection() {
      List<DirectionChoice> choices = new ArrayList<>(this.getDirections());
      Collections.shuffle(choices);
      DirectionChoice votedChoice = null;

      for (DirectionChoice choice : choices) {
         if (votedChoice == null || choice.getVotes() > votedChoice.getVotes()) {
            votedChoice = choice;
         }
      }

      return votedChoice;
   }

   public CompoundNBT serialize() {
      CompoundNBT tag = new CompoundNBT();
      CodecUtils.writeNBT(BlockPos.field_239578_a_, this.stabilizerPos, tag, "pos");
      tag.func_74768_a("voteTicks", this.voteTicks);
      ListNBT directions = new ListNBT();

      for (DirectionChoice choice : this.directions) {
         directions.add(choice.serialize());
      }

      tag.func_218657_a("directions", directions);
      return tag;
   }

   public static VotingSession deserialize(CompoundNBT tag) {
      return new VotingSession(tag);
   }
}
