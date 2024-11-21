package iskallia.vault.block.entity.challenge.elite;

import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EliteControllerProxyBlockEntity extends BlockEntity {
   private BlockPos controller;
   private List<ChallengeAction<?>> actions = new ArrayList<>();

   public EliteControllerProxyBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.ELITE_CONTROLLER_PROXY_TILE_ENTITY, pos, state);
   }

   public BlockPos getController() {
      return this.controller;
   }

   public void setController(BlockPos controller) {
      this.controller = controller;
      this.sendUpdates();
   }

   public List<ChallengeAction<?>> getActions() {
      return this.actions;
   }

   public void setActions(List<ChallengeAction<?>> actions) {
      this.actions = actions;
      this.sendUpdates();
   }

   public static void tick(Level world, BlockPos pos, BlockState state, EliteControllerProxyBlockEntity entity) {
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      Adapters.BLOCK_POS.writeNbt(this.controller).ifPresent(tag -> nbt.put("controller", tag));
      ListTag actions = new ListTag();

      for (ChallengeAction<?> action : this.actions) {
         Adapters.RAID_ACTION.writeNbt(action).ifPresent(actions::add);
      }

      nbt.put("actions", actions);
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.controller = Adapters.BLOCK_POS.readNbt(nbt.get("controller")).orElse(null);
      ListTag actions = nbt.getList("actions", 10);
      this.actions.clear();

      for (Tag tag : actions) {
         Adapters.RAID_ACTION.readNbt(tag).ifPresent(action -> this.actions.add((ChallengeAction<?>)action));
      }
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }
}
