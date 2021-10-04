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

public class ExecuteEffect extends Effect {
   public ExecuteEffect(EffectType typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }

   public void func_111187_a(LivingEntity entityLiving, AttributeModifierManager attributeMapIn, int amplifier) {
      super.func_111187_a(entityLiving, attributeMapIn, amplifier);
      if (entityLiving instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
         PlayerAbilitiesData data = PlayerAbilitiesData.get(player.func_71121_q());
         AbilityTree abilities = data.getAbilities(player);
         AbilityNode<?, ?> execute = abilities.getNodeByName("Execute");
         abilities.putOnCooldown(player.func_184102_h(), execute, ModConfigs.ABILITIES.getCooldown(execute, player));
      }
   }

   public boolean func_76397_a(int duration, int amplifier) {
      return true;
   }
}
