package iskallia.vault.util.calc;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.config.sub.RampageLeechConfig;
import iskallia.vault.skill.ability.effect.RampageAbility;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.set.VampirismSet;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.VampirismTalent;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class LeechHelper {
   public static float getPlayerLeechPercent(ServerPlayerEntity player) {
      float leech = 0.0F;
      TalentTree talents = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

      for (TalentNode<?> node : talents.getNodes()) {
         if (node.getTalent() instanceof VampirismTalent) {
            VampirismTalent vampirism = (VampirismTalent)node.getTalent();
            leech += vampirism.getLeechRatio();
         }
      }

      AbilityTree abilities = PlayerAbilitiesData.get(player.func_71121_q()).getAbilities(player);

      for (AbilityNode<?, ?> nodex : abilities.getNodes()) {
         if (nodex.isLearned() && nodex.getAbility() instanceof RampageAbility) {
            AbilityConfig cfg = nodex.getAbilityConfig();
            if (cfg instanceof RampageLeechConfig) {
               leech += ((RampageLeechConfig)cfg).getLeechPercent();
            }
         }
      }

      SetTree sets = PlayerSetsData.get(player.func_71121_q()).getSets(player);

      for (SetNode<?> nodexx : sets.getNodes()) {
         if (nodexx.getSet() instanceof VampirismSet) {
            VampirismSet set = (VampirismSet)nodexx.getSet();
            leech += set.getLeechRatio();
         }
      }

      return leech + getLeechPercent(player);
   }

   public static float getLeechPercent(LivingEntity entity) {
      float leech = 0.0F;

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = entity.func_184582_a(slot);
         if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
            leech += ModAttributes.EXTRA_LEECH_RATIO.getOrDefault(stack, 0.0F).getValue(stack);
            leech += ModAttributes.ADD_EXTRA_LEECH_RATIO.getOrDefault(stack, 0.0F).getValue(stack);
         }
      }

      return leech;
   }
}
