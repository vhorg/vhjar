package iskallia.vault.altar;

import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class AltarInfusionRecipe {
   private final UUID player;
   @Nonnull
   private List<RequiredItem> requiredItems;
   @Nonnull
   private List<RequiredItem> cachedItems;
   private boolean pogInfused;

   public AltarInfusionRecipe(UUID uuid, @Nonnull List<RequiredItem> items, List<RequiredItem> cachedItems, boolean pogInfused) {
      this.player = uuid;
      this.requiredItems = items;
      this.cachedItems = (List<RequiredItem>)(cachedItems == null ? new ArrayList<>() : cachedItems);
      this.pogInfused = pogInfused;
   }

   public AltarInfusionRecipe(UUID uuid, List<RequiredItem> items, boolean pogInfused) {
      this(uuid, items, null, pogInfused);
   }

   public AltarInfusionRecipe(ServerLevel world, BlockPos pos, ServerPlayer player) {
      this(player.getUUID(), ModConfigs.VAULT_ALTAR.getRequiredItemsFromConfig(world, pos, player), false);
   }

   public boolean isPogInfused() {
      return this.pogInfused;
   }

   public void setPogInfused(boolean pogInfused) {
      this.pogInfused = pogInfused;
   }

   public void cacheRequiredItems(List<RequiredItem> newRequirements) {
      this.cachedItems = this.requiredItems;
      this.requiredItems = newRequirements;
   }

   public void revertCache() {
      this.requiredItems = this.cachedItems;
      this.cachedItems = new ArrayList<>();
   }

   public static AltarInfusionRecipe deserialize(CompoundTag nbt) {
      UUID player = nbt.getUUID("player");
      ListTag requiredItemsNBT = nbt.getList("requiredItems", 10);
      ListTag cachedItemsNBT = nbt.getList("cachedItems", 10);
      List<RequiredItem> requiredItems = new ArrayList<>();
      List<RequiredItem> cachedItems = new ArrayList<>();

      for (Tag tag : requiredItemsNBT) {
         CompoundTag compound = (CompoundTag)tag;
         requiredItems.add(RequiredItem.deserializeNBT(compound));
      }

      for (Tag tag : cachedItemsNBT) {
         CompoundTag compound = (CompoundTag)tag;
         cachedItems.add(RequiredItem.deserializeNBT(compound));
      }

      boolean pogInfused = nbt.getBoolean("pogInfused");
      return new AltarInfusionRecipe(player, requiredItems, cachedItems, pogInfused);
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      ListTag requiredItems = new ListTag();
      ListTag cachedItems = new ListTag();

      for (RequiredItem item : this.getRequiredItems()) {
         requiredItems.add(RequiredItem.serializeNBT(item));
      }

      for (RequiredItem item : this.getCachedItems()) {
         cachedItems.add(RequiredItem.serializeNBT(item));
      }

      nbt.putUUID("player", this.getPlayer());
      nbt.put("requiredItems", requiredItems);
      nbt.put("cachedItems", cachedItems);
      nbt.putBoolean("pogInfused", this.pogInfused);
      return nbt;
   }

   public UUID getPlayer() {
      return this.player;
   }

   @Nonnull
   public List<RequiredItem> getRequiredItems() {
      return this.requiredItems;
   }

   @Nonnull
   public List<RequiredItem> getCachedItems() {
      return this.cachedItems;
   }

   public boolean isComplete() {
      if (this.requiredItems.isEmpty()) {
         return false;
      } else {
         for (RequiredItem item : this.requiredItems) {
            if (!item.reachedAmountRequired()) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean hasEqualQuantities(AltarInfusionRecipe other) {
      int equals = 0;

      for (int i = 0; i < this.getRequiredItems().size(); i++) {
         RequiredItem item = this.getRequiredItems().get(i);
         if (item.getCurrentAmount() == other.getRequiredItems().get(i).getCurrentAmount()) {
            equals++;
         }
      }

      return equals == 4;
   }
}
