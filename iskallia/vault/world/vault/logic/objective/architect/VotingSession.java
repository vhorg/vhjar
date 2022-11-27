package iskallia.vault.world.vault.logic.objective.architect;

import iskallia.vault.block.entity.StabilizerTileEntity;
import iskallia.vault.util.CodecUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

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

   VotingSession(CompoundTag tag) {
      this.stabilizerPos = CodecUtils.readNBT(BlockPos.CODEC, tag, "pos", BlockPos.ZERO);
      this.voteTicks = tag.getInt("voteTicks");
      ListTag directions = tag.getList("directions", 10);

      for (int i = 0; i < directions.size(); i++) {
         this.directions.add(new DirectionChoice(directions.getCompound(i)));
      }
   }

   void acceptVote(String voter, Direction dir) {
      if (this.voted.add(voter)) {
         for (DirectionChoice choice : this.directions) {
            if (choice.getDirection() == dir) {
               choice.addVote();
            }
         }
      }
   }

   void tick(ServerLevel world) {
      if (!this.isFinished()) {
         this.voteTicks--;
         if (world.hasChunkAt(this.getStabilizerPos())) {
            BlockEntity tile = world.getBlockEntity(this.getStabilizerPos());
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

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      CodecUtils.writeNBT(BlockPos.CODEC, this.stabilizerPos, tag, "pos");
      tag.putInt("voteTicks", this.voteTicks);
      ListTag directions = new ListTag();

      for (DirectionChoice choice : this.directions) {
         directions.add(choice.serialize());
      }

      tag.put("directions", directions);
      return tag;
   }

   public static VotingSession deserialize(CompoundTag tag) {
      return new VotingSession(tag);
   }
}
