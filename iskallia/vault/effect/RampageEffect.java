package iskallia.vault.effect;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.RampageConfig;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class RampageEffect extends Effect {
   private static final UUID RAMPAGE_MOD_UUID = UUID.fromString("DBC7E5EC-EF77-422D-B23B-AC22B1816A1E");

   public RampageEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public boolean func_76397_a(int duration, int amplifier) {
      return true;
   }

   public void func_111185_a(LivingEntity livingEntity, AttributeModifierManager attributeMap, int amplifier) {
      if (livingEntity instanceof PlayerEntity && livingEntity.func_130014_f_() instanceof ServerWorld) {
         PlayerEntity player = (PlayerEntity)livingEntity;
         AbilityTree abilities = PlayerAbilitiesData.get((ServerWorld)player.func_130014_f_()).getAbilities(player);
         AbilityNode<?, ?> rampageNode = abilities.getNodeByName("Rampage");
         RampageConfig cfg = (RampageConfig)rampageNode.getAbilityConfig();
         if (cfg != null) {
            float dmgIncrease = cfg.getDamageIncrease();
            ModifiableAttributeInstance damage = attributeMap.func_233779_a_(Attributes.field_233823_f_);
            if (damage != null && dmgIncrease > 1.0E-4) {
               AttributeModifier mod = new AttributeModifier(RAMPAGE_MOD_UUID, "Rampage damage", dmgIncrease, Operation.MULTIPLY_BASE);
               damage.func_233767_b_(mod);
            }
         }
      }

      super.func_111185_a(livingEntity, attributeMap, amplifier);
   }

   public void func_111187_a(LivingEntity livingEntity, AttributeModifierManager attributeMapIn, int amplifier) {
      ModifiableAttributeInstance damage = livingEntity.func_110148_a(Attributes.field_233823_f_);
      if (damage != null) {
         damage.func_188479_b(RAMPAGE_MOD_UUID);
      }

      if (livingEntity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)livingEntity;
         PlayerAbilitiesData data = PlayerAbilitiesData.get(player.func_71121_q());
         AbilityTree abilities = data.getAbilities(player);
         AbilityNode<?, ?> rampage = abilities.getNodeByName("Rampage");
         abilities.putOnCooldown(player.func_184102_h(), rampage, ModConfigs.ABILITIES.getCooldown(rampage, player));
      }

      super.func_111187_a(livingEntity, attributeMapIn, amplifier);
   }
}
