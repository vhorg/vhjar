package iskallia.vault.block.entity;

import iskallia.vault.Vault;
import iskallia.vault.config.EtchingConfig;
import iskallia.vault.entity.EtchingVendorEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.item.gear.EtchingItem;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.MathUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.server.ServerWorld;

public class EtchingVendorControllerTileEntity extends TileEntity implements ITickableTileEntity {
   private int monitoredEntityId = -1;
   private final List<EtchingVendorControllerTileEntity.EtchingTrade> trades = new ArrayList<>();

   public EtchingVendorControllerTileEntity() {
      super(ModBlocks.ETCHING_CONTROLLER_TILE_ENTITY);
   }

   public void func_73660_a() {
      if (!this.func_145831_w().func_201670_d() && this.func_145831_w() instanceof ServerWorld) {
         if (this.func_145831_w().func_234923_W_() == Vault.VAULT_KEY) {
            if (this.trades.isEmpty()) {
               this.generateTrades();
               this.sendUpdates();
            }

            Entity monitoredEntity;
            if (this.monitoredEntityId == -1) {
               monitoredEntity = this.createVendor();
            } else if ((monitoredEntity = this.field_145850_b.func_73045_a(this.monitoredEntityId)) == null) {
               monitoredEntity = this.createVendor();
            }

            monitoredEntity.func_70107_b(
               this.field_174879_c.func_177958_n() + 0.5, this.field_174879_c.func_177956_o(), this.field_174879_c.func_177952_p() + 0.5
            );
         }
      }
   }

   private Entity createVendor() {
      ServerWorld sWorld = (ServerWorld)this.func_145831_w();
      EtchingVendorEntity vendor = (EtchingVendorEntity)ModEntities.ETCHING_VENDOR
         .func_220349_b(sWorld, null, null, null, this.func_174877_v(), SpawnReason.STRUCTURE, false, false);
      vendor.setVendorPos(this.func_174877_v());
      sWorld.func_217376_c(vendor);
      this.monitoredEntityId = vendor.func_145782_y();
      return vendor;
   }

   private void generateTrades() {
      new Random();

      for (int i = 0; i < 3; i++) {
         VaultGear.Set etchingSet = ModConfigs.ETCHING.getRandomSet();
         EtchingConfig.Etching etching = ModConfigs.ETCHING.getFor(etchingSet);
         ItemStack etchingStack = EtchingItem.createEtchingStack(etchingSet);
         int amount = MathUtilities.getRandomInt(etching.minValue, etching.maxValue + 1);
         this.trades.add(new EtchingVendorControllerTileEntity.EtchingTrade(etchingStack, amount, false));
      }
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

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      super.func_230337_a_(state, nbt);
      ListNBT trades = nbt.func_150295_c("trades", 10);

      for (int i = 0; i < trades.size(); i++) {
         CompoundNBT tradeTag = trades.func_150305_b(i);
         this.trades.add(EtchingVendorControllerTileEntity.EtchingTrade.deserialize(tradeTag));
      }
   }

   public CompoundNBT func_189515_b(CompoundNBT compound) {
      CompoundNBT tag = super.func_189515_b(compound);
      ListNBT trades = new ListNBT();

      for (EtchingVendorControllerTileEntity.EtchingTrade trade : this.trades) {
         trades.add(trade.serialize());
      }

      compound.func_218657_a("trades", trades);
      return tag;
   }

   public CompoundNBT func_189517_E_() {
      return this.func_189515_b(new CompoundNBT());
   }

   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT tag = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), tag);
   }

   public void sendUpdates() {
      this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 11);
      this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
      this.func_70296_d();
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

      public CompoundNBT serialize() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_218657_a("stack", this.soldEtching.serializeNBT());
         nbt.func_74768_a("amount", this.requiredPlatinum);
         nbt.func_74757_a("sold", this.sold);
         return nbt;
      }

      public static EtchingVendorControllerTileEntity.EtchingTrade deserialize(CompoundNBT nbt) {
         ItemStack stack = ItemStack.func_199557_a(nbt.func_74775_l("stack"));
         int amount = nbt.func_74762_e("amount");
         boolean sold = nbt.func_74767_n("sold");
         return new EtchingVendorControllerTileEntity.EtchingTrade(stack, amount, sold);
      }
   }
}
