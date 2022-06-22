package iskallia.vault.altar;

import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

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

   public AltarInfusionRecipe(ServerWorld world, BlockPos pos, ServerPlayerEntity player) {
      this(player.func_110124_au(), ModConfigs.VAULT_ALTAR.getRequiredItemsFromConfig(world, pos, player), false);
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

   public static AltarInfusionRecipe deserialize(CompoundNBT nbt) {
      UUID player = nbt.func_186857_a("player");
      ListNBT requiredItemsNBT = nbt.func_150295_c("requiredItems", 10);
      ListNBT cachedItemsNBT = nbt.func_150295_c("cachedItems", 10);
      List<RequiredItem> requiredItems = new ArrayList<>();
      List<RequiredItem> cachedItems = new ArrayList<>();

      for (INBT tag : requiredItemsNBT) {
         CompoundNBT compound = (CompoundNBT)tag;
         requiredItems.add(RequiredItem.deserializeNBT(compound));
      }

      for (INBT tag : cachedItemsNBT) {
         CompoundNBT compound = (CompoundNBT)tag;
         cachedItems.add(RequiredItem.deserializeNBT(compound));
      }

      boolean pogInfused = nbt.func_74767_n("pogInfused");
      return new AltarInfusionRecipe(player, requiredItems, cachedItems, pogInfused);
   }

   public CompoundNBT serialize() {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT requiredItems = new ListNBT();
      ListNBT cachedItems = new ListNBT();

      for (RequiredItem item : this.getRequiredItems()) {
         requiredItems.add(RequiredItem.serializeNBT(item));
      }

      for (RequiredItem item : this.getCachedItems()) {
         cachedItems.add(RequiredItem.serializeNBT(item));
      }

      nbt.func_186854_a("player", this.getPlayer());
      nbt.func_218657_a("requiredItems", requiredItems);
      nbt.func_218657_a("cachedItems", cachedItems);
      nbt.func_74757_a("pogInfused", this.pogInfused);
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
