package iskallia.vault.block.entity;

import iskallia.vault.entity.entity.EtchingVendorEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EtchingVendorControllerTileEntity extends BlockEntity {
   private int monitoredEntityId = -1;
   private final List<EtchingVendorControllerTileEntity.EtchingTrade> trades = new ArrayList<>();

   public EtchingVendorControllerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.ETCHING_CONTROLLER_TILE_ENTITY, pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, EtchingVendorControllerTileEntity tile) {
      if (!level.isClientSide()) {
         if (ServerVaults.isVaultWorld(level)) {
            if (tile.trades.isEmpty()) {
               tile.sendUpdates();
            }

            Entity monitoredEntity;
            if (tile.monitoredEntityId == -1) {
               monitoredEntity = tile.createVendor();
            } else if ((monitoredEntity = level.getEntity(tile.monitoredEntityId)) == null) {
               monitoredEntity = tile.createVendor();
            }

            monitoredEntity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
         }
      }
   }

   private Entity createVendor() {
      ServerLevel sWorld = (ServerLevel)this.getLevel();
      EtchingVendorEntity vendor = (EtchingVendorEntity)ModEntities.ETCHING_VENDOR
         .create(sWorld, null, null, null, this.getBlockPos(), MobSpawnType.STRUCTURE, false, false);
      vendor.setVendorPos(this.getBlockPos());
      sWorld.addFreshEntity(vendor);
      this.monitoredEntityId = vendor.getId();
      return vendor;
   }

   public int getMonitoredEntityId() {
      return this.monitoredEntityId;
   }

   public void setMonitoredEntityId(int id) {
      if (this.monitoredEntityId == -1) {
         this.monitoredEntityId = id;
      }
   }

   @Nullable
   public EtchingVendorControllerTileEntity.EtchingTrade getTrade(int id) {
      return id >= 0 && id < this.trades.size() ? this.trades.get(id) : null;
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      ListTag trades = nbt.getList("trades", 10);

      for (int i = 0; i < trades.size(); i++) {
         CompoundTag tradeTag = trades.getCompound(i);
         this.trades.add(EtchingVendorControllerTileEntity.EtchingTrade.deserialize(tradeTag));
      }
   }

   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      ListTag trades = new ListTag();

      for (EtchingVendorControllerTileEntity.EtchingTrade trade : this.trades) {
         trades.add(trade.serialize());
      }

      pTag.put("trades", trades);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithFullMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sendUpdates() {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 11);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      this.setChanged();
   }

   public static class EtchingTrade {
      private final ItemStack soldEtching;
      private final int requiredPlatinum;
      private boolean sold;

      public EtchingTrade(ItemStack soldEtching, int requiredPlatinum, boolean sold) {
         this.soldEtching = soldEtching;
         this.requiredPlatinum = requiredPlatinum;
         this.sold = sold;
      }

      public ItemStack getSoldEtching() {
         return this.soldEtching;
      }

      public int getRequiredPlatinum() {
         return this.requiredPlatinum;
      }

      public void setSold(boolean sold) {
         this.sold = sold;
      }

      public boolean isSold() {
         return this.sold;
      }

      public CompoundTag serialize() {
         CompoundTag nbt = new CompoundTag();
         nbt.put("stack", this.soldEtching.serializeNBT());
         nbt.putInt("amount", this.requiredPlatinum);
         nbt.putBoolean("sold", this.sold);
         return nbt;
      }

      public static EtchingVendorControllerTileEntity.EtchingTrade deserialize(CompoundTag nbt) {
         ItemStack stack = ItemStack.of(nbt.getCompound("stack"));
         int amount = nbt.getInt("amount");
         boolean sold = nbt.getBoolean("sold");
         return new EtchingVendorControllerTileEntity.EtchingTrade(stack, amount, sold);
      }
   }
}
