package iskallia.vault.effect;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.GhostWalkDamageConfig;
import iskallia.vault.util.PlayerDamageHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class GhostWalkEffect extends Effect {
   private AttributeModifier[] attributeModifiers = null;
   private static final Map<UUID, PlayerDamageHelper.DamageMultiplier> multiplierMap = new HashMap<>();

   public GhostWalkEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   private void initializeAttributeModifiers() {
      this.attributeModifiers = new AttributeModifier[ModConfigs.ABILITIES.RAMPAGE.getMaxLevel()];

      for (int i = 0; i < this.attributeModifiers.length; i++) {
         this.attributeModifiers[i] = new AttributeModifier(this.getRegistryName().toString(), (i + 1) * 0.1F, Operation.ADDITION);
      }
   }

   public boolean func_76397_a(int duration, int amplifier) {
      return true;
   }

   public void func_111185_a(LivingEntity livingEntity, AttributeModifierManager attributeMap, int amplifier) {
      if (this.attributeModifiers == null) {
         this.initializeAttributeModifiers();
      }

      ModifiableAttributeInstance movementSpeed = livingEntity.func_110148_a(Attributes.field_233821_d_);
      if (movementSpeed != null) {
         AttributeModifier attributeModifier = this.attributeModifiers[MathHelper.func_76125_a(amplifier + 1, 0, this.attributeModifiers.length - 1)];
         movementSpeed.func_233767_b_(attributeModifier);
      }

      if (livingEntity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)livingEntity;
         this.removeExistingDamageBuff(player);
         AbilityTree abilities = PlayerAbilitiesData.get((ServerWorld)player.func_130014_f_()).getAbilities(player);
         AbilityNode<?, ?> ghostWalkNode = abilities.getNodeByName("Ghost Walk");
         if (ghostWalkNode.getAbilityConfig() instanceof GhostWalkDamageConfig) {
            float dmgIncrease = ((GhostWalkDamageConfig)ghostWalkNode.getAbilityConfig()).getDamageMultiplierInGhostWalk();
            PlayerDamageHelper.DamageMultiplier multiplier = PlayerDamageHelper.applyMultiplier(
               player, dmgIncrease, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY
            );
            multiplierMap.put(player.func_110124_au(), multiplier);
         }
      }

      super.func_111185_a(livingEntity, attributeMap, amplifier);
   }

   public void func_111187_a(LivingEntity livingEntity, AttributeModifierManager attributeMap, int amplifier) {
      ModifiableAttributeInstance movementSpeed = livingEntity.func_110148_a(Attributes.field_233821_d_);
      if (movementSpeed != null && this.attributeModifiers != null) {
         AttributeModifier attributeModifier = this.attributeModifiers[MathHelper.func_76125_a(amplifier + 1, 0, this.attributeModifiers.length - 1)];
         movementSpeed.func_188479_b(attributeModifier.func_111167_a());
      }

      if (livingEntity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)livingEntity;
         this.removeExistingDamageBuff(player);
         PlayerAbilitiesData.setAbilityOnCooldown(player, "Ghost Walk");
      }

      super.func_111187_a(livingEntity, attributeMap, amplifier);
   }

   private void removeExistingDamageBuff(ServerPlayerEntity player) {
      PlayerDamageHelper.DamageMultiplier existing = multiplierMap.get(player.func_110124_au());
      if (existing != null) {
         PlayerDamageHelper.removeMultiplier(player, existing);
      }
   }
}
