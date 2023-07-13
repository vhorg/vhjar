package iskallia.vault.gear.item;

import iskallia.vault.gear.GearRollHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IdentifiableItem {
   default VaultGearState getState(ItemStack stack) {
      AttributeGearData data = AttributeGearData.read(stack);
      return data instanceof VaultGearData gearData ? gearData.getState() : data.getFirstValue(ModGearAttributes.STATE).orElse(VaultGearState.UNIDENTIFIED);
   }

   default void setState(ItemStack stack, VaultGearState state) {
      AttributeGearData data = AttributeGearData.read(stack);
      if (data instanceof VaultGearData gearData) {
         gearData.setState(state);
         gearData.write(stack);
      } else {
         data.updateAttribute(ModGearAttributes.STATE, state);
         data.write(stack);
      }
   }

   default boolean tryStartIdentification(Player player, ItemStack stack) {
      if (this.getState(stack) != VaultGearState.UNIDENTIFIED) {
         return false;
      } else if (player.getItemInHand(InteractionHand.OFF_HAND).is(ModItems.IDENTIFICATION_TOME)) {
         this.instantIdentify(player, stack);
         return true;
      } else {
         this.setState(stack, VaultGearState.ROLLING);
         return true;
      }
   }

   default void inventoryIdentificationTick(Player player, ItemStack stack) {
      if (this.getState(stack) == VaultGearState.ROLLING) {
         GearRollHelper.tickToll(stack, player, this::tickRoll, finishStack -> this.tickFinishRoll(finishStack, player));
      }
   }

   default void instantIdentify(Player player, ItemStack stack) {
      if (this.getState(stack) == VaultGearState.UNIDENTIFIED) {
         this.tickRoll(stack, player);
         this.tickFinishRoll(stack, player);
      }
   }

   void tickRoll(ItemStack var1, Player var2);

   void tickFinishRoll(ItemStack var1, Player var2);
}
