package iskallia.vault.util.calc;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.set.AssassinSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.FatalStrikeChanceTalent;
import iskallia.vault.skill.talent.type.FatalStrikeDamageTalent;
import iskallia.vault.skill.talent.type.FatalStrikeTalent;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.influence.VaultAttributeInfluence;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class FatalStrikeHelper {
   public static float getPlayerFatalStrikeChance(ServerPlayerEntity player) {
      float chance = 0.0F;
      TalentTree tree = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

      for (FatalStrikeTalent talent : tree.getTalents(FatalStrikeTalent.class)) {
         chance += talent.getFatalStrikeChance();
      }

      for (FatalStrikeChanceTalent talent : tree.getTalents(FatalStrikeChanceTalent.class)) {
         chance += talent.getAdditionalFatalStrikeChance();
      }

      SetTree sets = PlayerSetsData.get(player.func_71121_q()).getSets(player);

      for (SetNode<?> node : sets.getNodes()) {
         if (node.getSet() instanceof AssassinSet) {
            AssassinSet set = (AssassinSet)node.getSet();
            chance += set.getIncreasedFatalStrikeChance();
         }
      }

      VaultRaid vault = VaultRaidData.get(player.func_71121_q()).getActiveFor(player);
      if (vault != null) {
         for (VaultAttributeInfluence influence : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
            if (influence.getType() == VaultAttributeInfluence.Type.FATAL_STRIKE_CHANCE && !influence.isMultiplicative()) {
               chance += influence.getValue();
            }
         }
      }

      chance += getFatalStrikeChance(player);
      if (vault != null) {
         for (VaultAttributeInfluence influencex : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
            if (influencex.getType() == VaultAttributeInfluence.Type.FATAL_STRIKE_CHANCE && influencex.isMultiplicative()) {
               chance *= influencex.getValue();
            }
         }
      }

      return chance;
   }

   public static float getFatalStrikeChance(LivingEntity entity) {
      float chance = 0.0F;

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = entity.func_184582_a(slot);
         if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
            chance += ModAttributes.FATAL_STRIKE_CHANCE.getOrDefault(stack, 0.0F).getValue(stack);
         }
      }

      return chance;
   }

   public static float getPlayerFatalStrikeDamage(ServerPlayerEntity player) {
      float additionalMultiplier = 0.0F;
      TalentTree tree = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

      for (FatalStrikeTalent talent : tree.getTalents(FatalStrikeTalent.class)) {
         additionalMultiplier += talent.getFatalStrikeDamage();
      }

      for (FatalStrikeDamageTalent talent : tree.getTalents(FatalStrikeDamageTalent.class)) {
         additionalMultiplier += talent.getAdditionalFatalStrikeDamage();
      }

      VaultRaid vault = VaultRaidData.get(player.func_71121_q()).getActiveFor(player);
      if (vault != null) {
         for (VaultAttributeInfluence influence : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
            if (influence.getType() == VaultAttributeInfluence.Type.FATAL_STRIKE_DAMAGE && !influence.isMultiplicative()) {
               additionalMultiplier += influence.getValue();
            }
         }
      }

      additionalMultiplier += getFatalStrikeDamage(player);
      if (vault != null) {
         for (VaultAttributeInfluence influencex : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
            if (influencex.getType() == VaultAttributeInfluence.Type.FATAL_STRIKE_DAMAGE && influencex.isMultiplicative()) {
               additionalMultiplier *= influencex.getValue();
            }
         }
      }

      return additionalMultiplier;
   }

   public static float getFatalStrikeDamage(LivingEntity entity) {
      float additionalMultiplier = 0.0F;

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = entity.func_184582_a(slot);
         if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
            additionalMultiplier += ModAttributes.FATAL_STRIKE_DAMAGE.getOrDefault(stack, 0.0F).getValue(stack);
         }
      }

      return additionalMultiplier;
   }
}
