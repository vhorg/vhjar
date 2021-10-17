package iskallia.vault.effect;

import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.RampageConfig;
import iskallia.vault.util.PlayerDamageHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class RampageEffect extends Effect {
   private static final Map<UUID, PlayerDamageHelper.DamageMultiplier> multiplierMap = new HashMap<>();

   public RampageEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public boolean func_76397_a(int duration, int amplifier) {
      return true;
   }

   public void func_111185_a(LivingEntity livingEntity, AttributeModifierManager attributeMap, int amplifier) {
      if (livingEntity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)livingEntity;
         this.removeExistingDamageBuff(player);
         AbilityTree abilities = PlayerAbilitiesData.get((ServerWorld)player.func_130014_f_()).getAbilities(player);
         AbilityNode<?, ?> rampageNode = abilities.getNodeByName("Rampage");
         RampageConfig cfg = (RampageConfig)rampageNode.getAbilityConfig();
         if (cfg != null) {
            PlayerDamageHelper.DamageMultiplier multiplier = PlayerDamageHelper.applyMultiplier(
               player, cfg.getDamageIncrease(), PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY
            );
            multiplierMap.put(player.func_110124_au(), multiplier);
         }
      }

      super.func_111185_a(livingEntity, attributeMap, amplifier);
   }

   public void func_111187_a(LivingEntity livingEntity, AttributeModifierManager attributeMapIn, int amplifier) {
      if (livingEntity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)livingEntity;
         this.removeExistingDamageBuff(player);
         PlayerAbilitiesData.setAbilityOnCooldown(player, "Rampage");
      }

      super.func_111187_a(livingEntity, attributeMapIn, amplifier);
   }

   private void removeExistingDamageBuff(ServerPlayerEntity player) {
      PlayerDamageHelper.DamageMultiplier existing = multiplierMap.get(player.func_110124_au());
      if (existing != null) {
         PlayerDamageHelper.removeMultiplier(player, existing);
      }
   }
}
