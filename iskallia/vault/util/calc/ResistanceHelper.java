package iskallia.vault.util.calc;

import iskallia.vault.aura.ActiveAura;
import iskallia.vault.aura.AuraManager;
import iskallia.vault.aura.type.ResistanceAuraConfig;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEffects;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.set.DreamSet;
import iskallia.vault.skill.set.GolemSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.influence.ResistanceInfluence;
import iskallia.vault.world.vault.modifier.StatModifier;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class ResistanceHelper {
   public static float getPlayerResistancePercent(ServerPlayerEntity player) {
      return MathHelper.func_76131_a(getPlayerResistancePercentUnlimited(player), 0.0F, AttributeLimitHelper.getResistanceLimit(player));
   }

   public static float getPlayerResistancePercentUnlimited(ServerPlayerEntity player) {
      float resistancePercent = 0.0F;
      resistancePercent += getResistancePercent(player);

      for (ActiveAura aura : AuraManager.getInstance().getAurasAffecting(player)) {
         if (aura.getAura() instanceof ResistanceAuraConfig) {
            resistancePercent += ((ResistanceAuraConfig)aura.getAura()).getAdditionalResistance();
         }
      }

      VaultRaid vault = VaultRaidData.get(player.func_71121_q()).getActiveFor(player);
      if (vault != null) {
         for (ResistanceInfluence influence : vault.getInfluences().getInfluences(ResistanceInfluence.class)) {
            resistancePercent += influence.getAdditionalResistance();
         }

         for (StatModifier modifier : vault.getActiveModifiersFor(PlayerFilter.of(player), StatModifier.class)) {
            if (modifier.getStat() == StatModifier.Statistic.RESISTANCE) {
               resistancePercent *= modifier.getMultiplier();
            }
         }
      }

      SetTree sets = PlayerSetsData.get((ServerWorld)player.field_70170_p).getSets(player);

      for (SetNode<?> node : sets.getNodes()) {
         if (node.getSet() instanceof GolemSet) {
            GolemSet set = (GolemSet)node.getSet();
            resistancePercent += set.getBonusResistance();
         }

         if (node.getSet() instanceof DreamSet) {
            DreamSet set = (DreamSet)node.getSet();
            resistancePercent += set.getIncreasedResistance();
         }
      }

      return resistancePercent + getResistancePercent(player);
   }

   public static float getResistancePercent(LivingEntity entity) {
      float resistancePercent = 0.0F;
      resistancePercent += getGearResistanceChance(entity::func_184582_a);
      if (entity.func_70644_a(ModEffects.RESISTANCE)) {
         resistancePercent += (entity.func_70660_b(ModEffects.RESISTANCE).func_76458_c() + 1) / 100.0F;
      }

      return resistancePercent;
   }

   public static float getGearResistanceChance(Function<EquipmentSlotType, ItemStack> gearProvider) {
      float resistancePercent = 0.0F;

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         ItemStack stack = gearProvider.apply(slot);
         if (!(stack.func_77973_b() instanceof VaultGear) || ((VaultGear)stack.func_77973_b()).isIntendedForSlot(slot)) {
            resistancePercent += ModAttributes.EXTRA_RESISTANCE.get(stack).map(attribute -> attribute.getValue(stack)).orElse(0.0F);
            resistancePercent += ModAttributes.ADD_EXTRA_RESISTANCE.get(stack).map(attribute -> attribute.getValue(stack)).orElse(0.0F);
         }
      }

      return MathHelper.func_76131_a(resistancePercent, 0.0F, 1.0F);
   }
}
