package iskallia.vault.world.data;

import iskallia.vault.item.VaultCharmUpgrade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultCharmData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_VaultCharm";
   private final HashMap<UUID, VaultCharmData.VaultCharmInventory> whitelistedItems = new HashMap<>();

   public VaultCharmData() {
      super("the_vault_VaultCharm");
   }

   public void updateWhitelist(ServerPlayerEntity player, List<ResourceLocation> ids) {
      VaultCharmData.VaultCharmInventory inventory = this.getInventory(player);
      inventory.updateWhitelist(ids);
      this.func_76185_a();
   }

   public void upgradeInventorySize(ServerPlayerEntity player, int newSize) {
      this.getInventory(player).setSize(newSize);
      this.func_76185_a();
   }

   public List<ResourceLocation> getWhitelistedItems(ServerPlayerEntity player) {
      VaultCharmData.VaultCharmInventory inventory = this.getInventory(player);
      return inventory.getWhitelist();
   }

   public VaultCharmData.VaultCharmInventory getInventory(ServerPlayerEntity player) {
      if (this.whitelistedItems.containsKey(player.func_110124_au())) {
         return this.whitelistedItems.get(player.func_110124_au());
      } else {
         VaultCharmData.VaultCharmInventory inventory = new VaultCharmData.VaultCharmInventory();
         this.whitelistedItems.put(player.func_110124_au(), inventory);
         this.func_76185_a();
         return inventory;
      }
   }

   public void func_76184_a(CompoundNBT nbt) {
      for (String key : nbt.func_150296_c()) {
         UUID playerId;
         try {
            playerId = UUID.fromString(key);
         } catch (IllegalArgumentException var7) {
            continue;
         }

         CompoundNBT inventoryNbt = nbt.func_74775_l(key);
         VaultCharmData.VaultCharmInventory inventory = new VaultCharmData.VaultCharmInventory();
         inventory.deserializeNBT(inventoryNbt);
         this.whitelistedItems.put(playerId, inventory);
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT compound) {
      this.whitelistedItems.forEach((uuid, inventory) -> compound.func_218657_a(uuid.toString(), inventory.serializeNBT()));
      return compound;
   }

   public static VaultCharmData get(ServerWorld world) {
      return (VaultCharmData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(VaultCharmData::new, "the_vault_VaultCharm");
   }

   public static class VaultCharmInventory implements INBTSerializable<CompoundNBT> {
      private int size;
      private List<ResourceLocation> whitelist = new ArrayList<>();

      public VaultCharmInventory() {
         this(9);
      }

      public VaultCharmInventory(int size) {
         this.size = size;
      }

      public int getSize() {
         return this.size;
      }

      public void setSize(int size) {
         this.size = size;
      }

      public List<ResourceLocation> getWhitelist() {
         return this.whitelist;
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         ListNBT whitelistNbt = new ListNBT();
         this.whitelist.forEach(id -> whitelistNbt.add(StringNBT.func_229705_a_(id.toString())));
         nbt.func_74768_a("InventorySize", this.size);
         nbt.func_218657_a("Whitelist", whitelistNbt);
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.size = nbt.func_74762_e("InventorySize");
         ListNBT itemList = nbt.func_150295_c("Whitelist", 8);

         for (int i = 0; i < itemList.size(); i++) {
            this.whitelist.add(new ResourceLocation(itemList.func_150307_f(i)));
         }
      }

      private void updateWhitelist(List<ResourceLocation> whitelist) {
         this.whitelist = new ArrayList<>(whitelist);
      }

      public static VaultCharmData.VaultCharmInventory fromNbt(CompoundNBT nbt) {
         VaultCharmData.VaultCharmInventory inventory = new VaultCharmData.VaultCharmInventory();
         inventory.deserializeNBT(nbt);
         return inventory;
      }

      public boolean canUpgrade(int newSize) {
         VaultCharmUpgrade.Tier current = VaultCharmUpgrade.Tier.getTierBySize(this.size);
         System.out.println(current);
         VaultCharmUpgrade.Tier potential = VaultCharmUpgrade.Tier.getTierBySize(newSize);
         System.out.println(potential);
         if (potential == null) {
            return false;
         } else if (current == null) {
            return potential == VaultCharmUpgrade.Tier.ONE;
         } else {
            VaultCharmUpgrade.Tier next = current.getNext();
            return next == null ? false : next == potential;
         }
      }
   }
}
