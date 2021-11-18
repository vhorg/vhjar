package iskallia.vault.util.calc;

import iskallia.vault.aura.ActiveAura;
import iskallia.vault.aura.AuraManager;
import iskallia.vault.aura.type.ParryAuraConfig;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEffects;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.GhostWalkParryConfig;
import iskallia.vault.skill.ability.config.sub.TankParryConfig;
import iskallia.vault.skill.set.AssassinSet;
import iskallia.vault.skill.set.NinjaSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.archetype.ArchetypeTalent;
import iskallia.vault.skill.talent.type.archetype.WardTalent;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.influence.ParryInfluence;
import iskallia.vault.world.vault.modifier.StatModifier;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ParryHelper {
   public static float getPlayerParryChance(ServerPlayerEntity player) {
      return MathHelper.func_76131_a(getPlayerParryChanceUnlimited(player), 0.0F, AttributeLimitHelper.getParryLimit(player));
   }

   public static float getPlayerParryChanceUnlimited(ServerPlayerEntity player) {
      float totalParryChance = 0.0F;
      totalParryChance += getParryChance(player);
      TalentTree talents = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

      for (TalentNode<?> talentNode : talents.getLearnedNodes()) {
         if (talentNode.getTalent() instanceof WardTalent && ArchetypeTalent.isEnabled(player.func_71121_q())) {
            totalParryChance += ((WardTalent)talentNode.getTalent()).getAdditionalParryChance();
         }
      }

      SetTree sets = PlayerSetsData.get(player.func_71121_q()).getSets(player);

      for (SetNode<?> node : sets.getNodes()) {
         if (node.getSet() instanceof AssassinSet) {
            AssassinSet set = (AssassinSet)node.getSet();
            totalParryChance += set.getParryChance();
         } else if (node.getSet() instanceof NinjaSet) {
            NinjaSet set = (NinjaSet)node.getSet();
            totalParryChance += set.getBonusParry();
         }
      }

      AbilityTree abilities = PlayerAbilitiesData.get(player.func_71121_q()).getAbilities(player);
      AbilityNode<?, ?> tankNode = abilities.getNodeByName("Tank");
      if (player.func_70660_b(ModEffects.TANK) != null && "Tank_Parry".equals(tankNode.getSpecialization())) {
         TankParryConfig parryConfig = (TankParryConfig)tankNode.getAbilityConfig();
         totalParryChance += parryConfig.getParryChance();
      }

      AbilityNode<?, ?> ghostWalk = abilities.getNodeByName("Ghost Walk");
      if (player.func_70660_b(ModEffects.GHOST_WALK) != null && "Ghost Walk_Parry".equals(ghostWalk.getSpecialization())) {
         GhostWalkParryConfig parryConfig = (GhostWalkParryConfig)ghostWalk.getAbilityConfig();
         totalParryChance += parryConfig.getAdditionalParryChance();
      }

      for (ActiveAura aura : AuraManager.getInstance().getAurasAffecting(player)) {
         if (aura.getAura() instanceof ParryAuraConfig) {
            totalParryChance += ((ParryAuraConfig)aura.getAura()).getAdditionalParryChance();
         }
      }

      VaultRaid vault = VaultRaidData.get(player.func_71121_q()).getActiveFor(player);
      if (vault != null) {
         for (ParryInfluence influence : vault.getInfluences().getInfluences(ParryInfluence.class)) {
            totalParryChance += influence.getAdditionalParry();
         }

         for (StatModifier modifier : vault.getActiveModifiersFor(PlayerFilter.of(player), StatModifier.class)) {
            if (modifier.getStat() == StatModifier.Statistic.PARRY) {
               totalParryChance *= modifier.getMultiplier();
            }
         }
      }

      return totalParryChance;
   }

   public static float getParryChance(LivingEntity entity) {
      float totalParryChance = 0.0F;
      totalParryChance += getGearParryChance(entity::func_184582_a);
      if (entity.func_70644_a(ModEffects.PARRY)) {
         totalParryChance += (entity.func_70660_b(ModEffects.PARRY).func_76458_c() + 1) / 100.0F;
      }

      return totalParryChance;
   }

   public static float getGearParryChance(Function<EquipmentSlotType, ItemStack> gearProvider) {
      float totalParryChance = 0.0F;

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = gearProvider.apply(slot);
         if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
            totalParryChance += ModAttributes.EXTRA_PARRY_CHANCE.getOrDefault(stack, 0.0F).getValue(stack);
            totalParryChance += ModAttributes.ADD_EXTRA_PARRY_CHANCE.getOrDefault(stack, 0.0F).getValue(stack);
         }
      }

      return totalParryChance;
   }
}
