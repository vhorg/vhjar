package iskallia.vault.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class NoAiEffect extends MobEffect {
   public NoAiEffect(ResourceLocation key, int liquidColor) {
      super(MobEffectCategory.NEUTRAL, liquidColor);
      this.setRegistryName(key);
   }

   public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
      super.addAttributeModifiers(entity, attributeMap, amplifier);
      if (entity instanceof Mob mob) {
         mob.setNoAi(true);
      }
   }

   public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
      super.removeAttributeModifiers(entity, attributeMap, amplifier);
      if (entity instanceof Mob mob) {
         mob.setNoAi(false);
      }
   }
}
