package iskallia.vault.effect;

import iskallia.vault.init.ModConfigs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GhostWalkEffect extends Effect {
   public AttributeModifier[] attributeModifiers;

   public GhostWalkEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   private void initializeAttributeModifiers() {
      this.attributeModifiers = new AttributeModifier[ModConfigs.ABILITIES.RAMPAGE.getMaxLevel()];

      for (int i = 0; i < this.attributeModifiers.length; i++) {
         this.attributeModifiers[i] = new AttributeModifier(this.getRegistryName().toString(), (i + 1) * 0.2F, Operation.ADDITION);
      }
   }

   public boolean func_76397_a(int duration, int amplifier) {
      return true;
   }

   public void func_111185_a(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
      ModifiableAttributeInstance movementSpeed = entityLivingBaseIn.func_110148_a(Attributes.field_233821_d_);
      this.initializeAttributeModifiers();
      if (movementSpeed != null) {
         AttributeModifier attributeModifier = this.attributeModifiers[MathHelper.func_76125_a(amplifier + 1, 0, this.attributeModifiers.length - 1)];
         movementSpeed.func_233767_b_(attributeModifier);
      }

      entityLivingBaseIn.func_184224_h(true);
      super.func_111185_a(entityLivingBaseIn, attributeMapIn, amplifier);
   }

   public void func_111187_a(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
      ModifiableAttributeInstance movementSpeed = entityLivingBaseIn.func_110148_a(Attributes.field_233821_d_);
      if (movementSpeed != null) {
         AttributeModifier attributeModifier = this.attributeModifiers[MathHelper.func_76125_a(amplifier + 1, 0, this.attributeModifiers.length - 1)];
         movementSpeed.func_188479_b(attributeModifier.func_111167_a());
      }

      entityLivingBaseIn.func_184224_h(false);
      super.func_111187_a(entityLivingBaseIn, attributeMapIn, amplifier);
   }
}
