package iskallia.vault.effect;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.type.RampageAbility;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class RampageEffect extends Effect {
   public AttributeModifier[] attributeModifiers;

   public RampageEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public boolean func_76397_a(int duration, int amplifier) {
      return true;
   }

   public void func_111185_a(LivingEntity livingEntity, AttributeModifierManager attributeMapIn, int amplifier) {
      RampageAbility rampageAbility = ModConfigs.ABILITIES.RAMPAGE.getAbility(amplifier + 1);
      this.initializeAttributeModifiers();
      if (rampageAbility != null) {
         int damageIncrease = rampageAbility.getDamageIncrease();
         ModifiableAttributeInstance damage = livingEntity.func_110148_a(Attributes.field_233823_f_);
         if (damage != null) {
            this.attributeModifiers[amplifier] = new AttributeModifier(this.getRegistryName().toString(), damageIncrease, Operation.ADDITION);
            damage.func_233767_b_(this.attributeModifiers[amplifier]);
         }

         super.func_111185_a(livingEntity, attributeMapIn, amplifier);
      }
   }

   private void initializeAttributeModifiers() {
      this.attributeModifiers = new AttributeModifier[ModConfigs.ABILITIES.RAMPAGE.getMaxLevel()];

      for (int i = 0; i < this.attributeModifiers.length; i++) {
         this.attributeModifiers[i] = new AttributeModifier(this.getRegistryName().toString(), (i + 1) * 0.2F, Operation.ADDITION);
      }
   }

   public void func_111187_a(LivingEntity livingEntity, AttributeModifierManager attributeMapIn, int amplifier) {
      ModifiableAttributeInstance damage = livingEntity.func_110148_a(Attributes.field_233823_f_);
      if (damage != null) {
         damage.func_188479_b(this.attributeModifiers[amplifier].func_111167_a());
      }

      super.func_111187_a(livingEntity, attributeMapIn, amplifier);
   }
}
