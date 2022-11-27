package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.data.ServerVaults;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class VaultPortalTileEntity extends BlockEntity {
   private CrystalData data = new CrystalData();

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
      nbt.put("Data", this.data.serializeNBT());
   }

   public void load(@NotNull CompoundTag nbt) {
      super.load(nbt);
      if (nbt.contains("Data", 10)) {
         this.data.deserializeNBT(nbt.getCompound("Data"));
      }
   }

   public CrystalData getData() {
      return this.data;
   }

   public void setCrystalData(CrystalData data) {
      this.data = data;
      this.setChanged();
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
