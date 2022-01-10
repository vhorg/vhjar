package iskallia.vault.util.calc;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.archetype.CommanderTalent;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.influence.VaultAttributeInfluence;
import iskallia.vault.world.vault.modifier.StatModifier;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class CooldownHelper {
   public static float getCooldownMultiplier(ServerPlayerEntity player, @Nullable AbilityGroup<?, ?> abilityGroup) {
      return MathHelper.func_76131_a(getCooldownMultiplierUnlimited(player, abilityGroup), 0.0F, AttributeLimitHelper.getCooldownReductionLimit(player));
   }

   public static float getCooldownMultiplierUnlimited(ServerPlayerEntity player, @Nullable AbilityGroup<?, ?> abilityGroup) {
      float multiplier = 0.0F;

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = player.func_184582_a(slot);
         if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
            multiplier += ModAttributes.COOLDOWN_REDUCTION.get(stack).map(attribute -> attribute.getValue(stack)).orElse(0.0F);
         }
      }

      if (abilityGroup == ModConfigs.ABILITIES.SUMMON_ETERNAL) {
         TalentTree talents = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);
         multiplier = (float)(
            multiplier
               + talents.getLearnedNodes(CommanderTalent.class)
                  .stream()
                  .mapToDouble(node -> node.getTalent().getSummonEternalAdditionalCooldownReduction())
                  .sum()
         );
      }

      VaultRaid vault = VaultRaidData.get(player.func_71121_q()).getActiveFor(player);
      if (vault != null) {
         for (VaultAttributeInfluence influence : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
            if (influence.getType() == VaultAttributeInfluence.Type.COOLDOWN_REDUCTION && !influence.isMultiplicative()) {
               multiplier += influence.getValue();
            }
         }

         for (StatModifier modifier : vault.getActiveModifiersFor(PlayerFilter.of(player), StatModifier.class)) {
            if (modifier.getStat() == StatModifier.Statistic.COOLDOWN_REDUCTION) {
               multiplier *= modifier.getMultiplier();
            }
         }

         for (VaultAttributeInfluence influencex : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
            if (influencex.getType() == VaultAttributeInfluence.Type.COOLDOWN_REDUCTION && influencex.isMultiplicative()) {
               multiplier *= influencex.getValue();
            }
         }
      }

      return multiplier;
   }
}
