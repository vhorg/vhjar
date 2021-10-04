package iskallia.vault.altar;

import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class AltarInfusionRecipe {
   private final UUID player;
   private List<RequiredItem> requiredItems = new ArrayList<>();

   public AltarInfusionRecipe(UUID uuid, List<RequiredItem> items) {
      this.player = uuid;
      this.requiredItems = items;
   }

   public AltarInfusionRecipe(ServerWorld world, BlockPos pos, ServerPlayerEntity player) {
      this(player.func_110124_au(), ModConfigs.VAULT_ALTAR.getRequiredItemsFromConfig(world, pos, player));
   }

   public AltarInfusionRecipe(UUID player) {
      this.player = player;
   }

   public static AltarInfusionRecipe deserialize(CompoundNBT nbt) {
      UUID player = nbt.func_186857_a("player");
      ListNBT list = nbt.func_150295_c("requiredItems", 10);
      List<RequiredItem> requiredItems = new ArrayList<>();

      for (INBT tag : list) {
         CompoundNBT compound = (CompoundNBT)tag;
         requiredItems.add(RequiredItem.deserializeNBT(compound));
      }

      return new AltarInfusionRecipe(player, requiredItems);
   }

   public static CompoundNBT serialize(AltarInfusionRecipe recipe) {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT list = new ListNBT();

      for (RequiredItem item : recipe.getRequiredItems()) {
         list.add(RequiredItem.serializeNBT(item));
      }

      nbt.func_186854_a("player", recipe.getPlayer());
      nbt.func_218657_a("requiredItems", list);
      return nbt;
   }

   public CompoundNBT serialize() {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT list = new ListNBT();

      for (RequiredItem item : this.getRequiredItems()) {
         list.add(RequiredItem.serializeNBT(item));
      }

      nbt.func_186854_a("player", this.getPlayer());
      nbt.func_218657_a("requiredItems", list);
      return nbt;
   }

   public UUID getPlayer() {
      return this.player;
   }

   public List<RequiredItem> getRequiredItems() {
      return this.requiredItems;
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
