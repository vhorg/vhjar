package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ConvertedSparkTileEntity extends BlockEntity {
   private int offsetTicks = new Random().nextInt(360);

   public ConvertedSparkTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.CONVERTED_SPARK_TILE_ENTITY, pWorldPosition, pBlockState);
   }

   public int getOffsetTicks() {
      return this.offsetTicks;
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, ConvertedSparkTileEntity tile) {
   }
}
