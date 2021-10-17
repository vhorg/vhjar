package iskallia.vault.effect;

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
         PlayerAbilitiesData.setAbilityOnCooldown(player, "Tank");
      }

      super.func_111187_a(livingEntity, attributeMapIn, amplifier);
   }
}
