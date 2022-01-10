package iskallia.vault.util.calc;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.set.PorcupineSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.ThornsChanceTalent;
import iskallia.vault.skill.talent.type.ThornsDamageTalent;
import iskallia.vault.skill.talent.type.ThornsTalent;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class ThornsHelper {
   public static float getPlayerThornsChance(ServerPlayerEntity player) {
      float chance = 0.0F;
      TalentTree tree = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

      for (ThornsTalent talent : tree.getTalents(ThornsTalent.class)) {
         chance += talent.getThornsChance();
      }

      for (ThornsChanceTalent talent : tree.getTalents(ThornsChanceTalent.class)) {
         chance += talent.getAdditionalThornsChance();
      }

      SetTree sets = PlayerSetsData.get(player.func_71121_q()).getSets(player);

      for (SetNode<?> node : sets.getNodes()) {
         if (node.getSet() instanceof PorcupineSet) {
            PorcupineSet set = (PorcupineSet)node.getSet();
            chance += set.getAdditionalThornsChance();
         }
      }

      return chance + getThornsChance(player);
   }

   public static float getThornsChance(LivingEntity entity) {
      float chance = 0.0F;

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = entity.func_184582_a(slot);
         if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
            chance += ModAttributes.THORNS_CHANCE.getOrDefault(stack, 0.0F).getValue(stack);
         }
      }

      return chance;
   }

   public static float getPlayerThornsDamage(ServerPlayerEntity player) {
      float additionalMultiplier = 0.0F;
      TalentTree tree = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

      for (ThornsTalent talent : tree.getTalents(ThornsTalent.class)) {
         additionalMultiplier += talent.getThornsDamage();
      }

      for (ThornsDamageTalent talent : tree.getTalents(ThornsDamageTalent.class)) {
         additionalMultiplier += talent.getAdditionalThornsDamage();
      }

      SetTree sets = PlayerSetsData.get(player.func_71121_q()).getSets(player);

      for (SetNode<?> node : sets.getNodes()) {
         if (node.getSet() instanceof PorcupineSet) {
            PorcupineSet set = (PorcupineSet)node.getSet();
            additionalMultiplier += set.getAdditionalThornsDamage();
         }
      }

      return additionalMultiplier + getThornsDamage(player);
   }

   public static float getThornsDamage(LivingEntity entity) {
      float additionalMultiplier = 0.0F;

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = entity.func_184582_a(slot);
         if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
            additionalMultiplier += ModAttributes.THORNS_DAMAGE.getOrDefault(stack, 0.0F).getValue(stack);
         }
      }

      return additionalMultiplier;
   }
}
