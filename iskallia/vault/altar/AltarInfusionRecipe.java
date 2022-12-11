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
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;

public class AltarInfusionRecipe implements INBTSerializable<CompoundTag> {
   private UUID player;
   @Nonnull
   private List<RequiredItems> requiredItems;
   @Nonnull
   private List<RequiredItems> cachedItems;
   private boolean pogInfused;

   public AltarInfusionRecipe(UUID uuid, @Nonnull List<RequiredItems> items, List<RequiredItems> cachedItems, boolean pogInfused) {
      this.player = uuid;
      this.requiredItems = items;
      this.cachedItems = (List<RequiredItems>)(cachedItems == null ? new ArrayList<>() : cachedItems);
      this.pogInfused = pogInfused;
   }

   public AltarInfusionRecipe(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public AltarInfusionRecipe(UUID uuid, List<RequiredItems> items, boolean pogInfused) {
      this(uuid, items, null, pogInfused);
   }

   public AltarInfusionRecipe(ServerPlayer player, BlockPos pos) {
      this(player.getUUID(), ModConfigs.VAULT_ALTAR_INGREDIENTS.getIngredients(player, pos), false);
   }

   public boolean isPogInfused() {
      return this.pogInfused;
   }

   public void setPogInfused(boolean pogInfused) {
      this.pogInfused = pogInfused;
   }

   public void cacheRequiredItems(List<RequiredItems> newRequirements) {
      this.cachedItems = this.requiredItems;
      this.requiredItems = newRequirements;
   }

   public void revertCache() {
      this.requiredItems = this.cachedItems;
      this.cachedItems = new ArrayList<>();
   }

   public UUID getPlayer() {
      return this.player;
   }

   @Nonnull
   public List<RequiredItems> getRequiredItems() {
      return this.requiredItems;
   }

   @Nonnull
   public List<RequiredItems> getCachedItems() {
      return this.cachedItems;
   }

   public boolean isComplete() {
      if (this.requiredItems.isEmpty()) {
         return false;
      } else {
         for (RequiredItems item : this.requiredItems) {
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
         RequiredItems item = this.getRequiredItems().get(i);
         if (item.getCurrentAmount() == other.getRequiredItems().get(i).getCurrentAmount()) {
            equals++;
         }
      }

      return equals == 4;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag requiredItems = new ListTag();
      ListTag cachedItems = new ListTag();

      for (RequiredItems item : this.getRequiredItems()) {
         requiredItems.add(item.serializeNBT());
      }

      for (RequiredItems item : this.getCachedItems()) {
         cachedItems.add(item.serializeNBT());
      }

      nbt.putUUID("player", this.getPlayer());
      nbt.put("requiredItems", requiredItems);
      nbt.put("cachedItems", cachedItems);
      nbt.putBoolean("pogInfused", this.pogInfused);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.player = nbt.getUUID("player");
      ListTag requiredItemsNBT = nbt.getList("requiredItems", 10);
      ListTag cachedItemsNBT = nbt.getList("cachedItems", 10);
      this.requiredItems = new ArrayList<>();
      this.cachedItems = new ArrayList<>();

      for (Tag tag : requiredItemsNBT) {
         CompoundTag compound = (CompoundTag)tag;
         this.requiredItems.add(new RequiredItems(compound));
      }

      for (Tag tag : cachedItemsNBT) {
         CompoundTag compound = (CompoundTag)tag;
         this.cachedItems.add(new RequiredItems(compound));
      }

      this.pogInfused = nbt.getBoolean("pogInfused");
   }
}
