package iskallia.vault.world.data;

import iskallia.vault.item.VaultCharmUpgrade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultCharmData extends SavedData {
   protected static final String DATA_NAME = "the_vault_VaultCharm";
   private final HashMap<UUID, VaultCharmData.VaultCharmInventory> whitelistedItems = new HashMap<>();

   public void updateWhitelist(ServerPlayer player, List<ResourceLocation> ids) {
      VaultCharmData.VaultCharmInventory inventory = this.getInventory(player);
      inventory.updateWhitelist(ids);
      this.setDirty();
   }

   public void upgradeInventorySize(ServerPlayer player, int newSize) {
      this.getInventory(player).setSize(newSize);
      this.setDirty();
   }

   public List<ResourceLocation> getWhitelistedItems(ServerPlayer player) {
      VaultCharmData.VaultCharmInventory inventory = this.getInventory(player);
      return inventory.getWhitelist();
   }

   public VaultCharmData.VaultCharmInventory getInventory(ServerPlayer player) {
      if (this.whitelistedItems.containsKey(player.getUUID())) {
         return this.whitelistedItems.get(player.getUUID());
      } else {
         VaultCharmData.VaultCharmInventory inventory = new VaultCharmData.VaultCharmInventory();
         this.whitelistedItems.put(player.getUUID(), inventory);
         this.setDirty();
         return inventory;
      }
   }

   private static VaultCharmData create(CompoundTag tag) {
      VaultCharmData data = new VaultCharmData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag nbt) {
      for (String key : nbt.getAllKeys()) {
         UUID playerId;
         try {
            playerId = UUID.fromString(key);
         } catch (IllegalArgumentException var7) {
            continue;
         }

         CompoundTag inventoryNbt = nbt.getCompound(key);
         VaultCharmData.VaultCharmInventory inventory = new VaultCharmData.VaultCharmInventory();
         inventory.deserializeNBT(inventoryNbt);
         this.whitelistedItems.put(playerId, inventory);
      }
   }

   public CompoundTag save(CompoundTag compound) {
      this.whitelistedItems.forEach((uuid, inventory) -> compound.put(uuid.toString(), inventory.serializeNBT()));
      return compound;
   }

   public static VaultCharmData get(ServerLevel world) {
      return (VaultCharmData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(VaultCharmData::create, VaultCharmData::new, "the_vault_VaultCharm");
   }

   public static class VaultCharmInventory implements INBTSerializable<CompoundTag> {
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

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         ListTag whitelistNbt = new ListTag();
         this.whitelist.forEach(id -> whitelistNbt.add(StringTag.valueOf(id.toString())));
         nbt.putInt("InventorySize", this.size);
         nbt.put("Whitelist", whitelistNbt);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.size = nbt.getInt("InventorySize");
         ListTag itemList = nbt.getList("Whitelist", 8);

         for (int i = 0; i < itemList.size(); i++) {
            this.whitelist.add(new ResourceLocation(itemList.getString(i)));
         }
      }

      private void updateWhitelist(List<ResourceLocation> whitelist) {
         this.whitelist = new ArrayList<>(whitelist);
      }

      public static VaultCharmData.VaultCharmInventory fromNbt(CompoundTag nbt) {
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
