package iskallia.vault.gear.modification;

import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public record GearModificationAction(
   int slotIndex, VaultArtisanStationContainer.Tab tab, GearModification modification, VaultArtisanStationContainer.ButtonSide side
) {
   private static final Random rand = new Random();

   @Nullable
   public Slot getCorrespondingSlot(VaultArtisanStationContainer container) {
      for (Slot slot : container.slots) {
         if (slot.index == this.slotIndex()) {
            return slot;
         }
      }

      return null;
   }

   public void apply(VaultArtisanStationContainer container, ServerPlayer player) {
      if (this.canApply(container, player)) {
         ItemStack gear = container.getGearInputSlot().getItem();
         VaultGearData data = VaultGearData.read(gear);
         Optional<Integer> potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL);
         if (!potential.isEmpty()) {
            Optional<Integer> maxPotential = data.getFirstValue(ModGearAttributes.MAX_CRAFTING_POTENTIAL);
            if (!maxPotential.isEmpty()) {
               Slot inSlot = this.getCorrespondingSlot(container);
               if (inSlot != null) {
                  ItemStack input = inSlot.getItem();
                  ItemStack material = input.copy();
                  if (!player.isCreative()) {
                     input.shrink(1);
                  }

                  inSlot.set(input);
                  GearModificationCost cost = GearModificationCost.getCost(potential.get(), maxPotential.get().intValue(), this.modification());
                  ItemStack bronze = container.getBronzeSlot().getItem();
                  if (!player.isCreative()) {
                     bronze.shrink(cost.costBronze());
                  }

                  container.getBronzeSlot().set(bronze);
                  ItemStack plating = container.getPlatingSlot().getItem();
                  if (!player.isCreative()) {
                     plating.shrink(cost.costPlating());
                  }

                  container.getPlatingSlot().set(plating);
                  this.modification().apply(gear, material, player, rand);
               }
            }
         }
      }
   }

   public boolean canApply(VaultArtisanStationContainer container, Player player) {
      Slot inSlot = this.getCorrespondingSlot(container);
      if (inSlot == null) {
         return false;
      } else {
         ItemStack gear = container.getGearInputSlot().getItem();
         ItemStack in = inSlot.getItem();
         if (!in.isEmpty() && !gear.isEmpty() && this.modification().getStackFilter().test(in)) {
            VaultGearData data = VaultGearData.read(gear);
            Optional<Integer> potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL);
            if (potential.isEmpty()) {
               return false;
            } else {
               Optional<Integer> maxPotential = data.getFirstValue(ModGearAttributes.MAX_CRAFTING_POTENTIAL);
               if (maxPotential.isEmpty()) {
                  return false;
               } else {
                  GearModificationCost cost = GearModificationCost.getCost(potential.get(), maxPotential.get().intValue(), this.modification());
                  ItemStack bronze = container.getBronzeSlot().getItem();
                  ItemStack plating = container.getPlatingSlot().getItem();
                  return bronze.getCount() >= cost.costBronze() && plating.getCount() >= cost.costPlating()
                     ? this.modification().canApply(gear, in, player, rand).success()
                     : false;
               }
            }
         } else {
            return false;
         }
      }
   }
}
