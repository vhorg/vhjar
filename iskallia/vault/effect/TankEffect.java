package iskallia.vault.effect;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class TankEffect extends Effect {
   public TankEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public boolean func_76397_a(int duration, int amplifier) {
      return true;
   }

   public void func_111187_a(LivingEntity livingEntity, AttributeModifierManager attributeMapIn, int amplifier) {
      if (livingEntity instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)livingEntity;
         PlayerAbilitiesData data = PlayerAbilitiesData.get(player.func_71121_q());
         AbilityTree abilities = data.getAbilities(player);
         AbilityNode<?, ?> tank = abilities.getNodeByName("Tank");
         abilities.putOnCooldown(player.func_184102_h(), tank, ModConfigs.ABILITIES.getCooldown(tank, player));
      }

      super.func_111187_a(livingEntity, attributeMapIn, amplifier);
   }
}
