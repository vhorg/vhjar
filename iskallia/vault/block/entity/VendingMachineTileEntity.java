package iskallia.vault.block.entity;

import com.google.common.collect.Iterables;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.TraderCore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;

public class VendingMachineTileEntity extends SkinnableTileEntity {
   private List<TraderCore> cores = new ArrayList<>();

   public VendingMachineTileEntity() {
      super(ModBlocks.VENDING_MACHINE_TILE_ENTITY);
   }

   public <T extends VendingMachineTileEntity> VendingMachineTileEntity(TileEntityType<T> type) {
      super(type);
      this.skin = new SkinProfile();
   }

   public List<TraderCore> getCores() {
      return this.cores;
   }

   public void addCore(TraderCore core) {
      this.cores.add(core);
      this.updateSkin();
      this.sendUpdates();
   }

   public TraderCore getLastCore() {
      return this.cores != null && this.cores.size() != 0 ? this.cores.get(this.cores.size() - 1) : null;
   }

   public ItemStack getTraderCoreStack() {
      TraderCore lastCore = this.getLastCore();
      if (lastCore == null) {
         return ItemStack.field_190927_a;
      } else {
         ItemStack stack = ItemTraderCore.getStackFromCore(lastCore);
         this.cores.remove(lastCore);
         return stack;
      }
   }

   public TraderCore getRenderCore() {
      return this.cores == null ? null : (TraderCore)Iterables.getFirst(this.cores, null);
   }

   @Override
   public void updateSkin() {
      TraderCore lastCore = this.getLastCore();
      if (lastCore != null) {
         this.skin.updateSkin(lastCore.getName());
      }
   }

   public CompoundNBT func_189515_b(CompoundNBT compound) {
      ListNBT list = new ListNBT();

      for (TraderCore core : this.cores) {
         try {
            list.add(NBTSerializer.serialize(core));
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

      compound.func_218657_a("coresList", list);
      return super.func_189515_b(compound);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      ListNBT list = nbt.func_150295_c("coresList", 10);
      this.cores = new LinkedList<>();

      for (INBT tag : list) {
         TraderCore core = null;

         try {
            core = NBTSerializer.deserialize(TraderCore.class, (CompoundNBT)tag);
         } catch (Exception var8) {
            var8.printStackTrace();
         }

         this.cores.add(core);
      }

      this.updateSkin();
      super.func_230337_a_(state, nbt);
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      ListNBT list = new ListNBT();

      for (TraderCore core : this.cores) {
         try {
            list.add(NBTSerializer.serialize(core));
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

      nbt.func_218657_a("coresList", list);
      return nbt;
   }

   @Override
   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
   }

   @Nullable
   @Override
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   @Override
   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT nbt = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), nbt);
   }
}
