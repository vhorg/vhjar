package iskallia.vault.effect;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
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

public class GhostWalkEffect extends Effect {
   private AttributeModifier[] attributeModifiers = null;

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
         PlayerAbilitiesData data = PlayerAbilitiesData.get(player.func_71121_q());
         AbilityTree abilities = data.getAbilities(player);
         AbilityNode<?, ?> ghostWalk = abilities.getNodeByName("Ghost Walk");
         abilities.putOnCooldown(player.func_184102_h(), ghostWalk, ModConfigs.ABILITIES.getCooldown(ghostWalk, player));
      }

      super.func_111187_a(livingEntity, attributeMap, amplifier);
   }
}
