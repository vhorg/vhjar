package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.data.ServerVaults;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class VaultPortalTileEntity extends BlockEntity {
   private CrystalData data = CrystalData.empty();

   public VaultPortalTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_PORTAL_TILE_ENTITY, pos, state);
   }

   public void sendUpdates() {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      this.setChanged();
   }

   protected void saveAdditional(@NotNull CompoundTag nbt) {
      super.saveAdditional(nbt);
      this.data.writeNbt().ifPresent(data -> nbt.put("Data", data));
   }

   public void load(@NotNull CompoundTag nbt) {
      super.load(nbt);
      this.data.readNbt(nbt.getCompound("Data"));
   }

   public CrystalData getData() {
      return this.data;
   }

   public void setCrystalData(CrystalData data) {
      this.data = data;
      this.setChanged();
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, VaultPortalTileEntity te) {
      UUID vaultId = te.getData().getVaultId();
      if (vaultId != null) {
         if (ServerVaults.get(vaultId).isEmpty()) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
         }
      }
   }
}
