package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class HeraldControllerTileEntity extends BlockEntity {
   public HeraldControllerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public HeraldControllerTileEntity(BlockPos pos, BlockState state) {
      this(ModBlocks.HERALD_CONTROLLER_TILE_ENTITY, pos, state);
   }

   public AABB getRenderBoundingBox() {
      return super.getRenderBoundingBox().inflate(2.0, 2.0, 2.0);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
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

   public void load(CompoundTag nbt) {
      super.load(nbt);
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, HeraldControllerTileEntity tile) {
      if (!(level instanceof ServerLevel world)) {
         ;
      }
   }
}
