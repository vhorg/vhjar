package iskallia.vault.gear.attribute.custom.effect;

import java.util.List;
import net.minecraft.world.effect.MobEffect;

public interface IEffectAvoidanceChanceAttribute {
   List<MobEffect> getEffects();

   default boolean mayAvoid(MobEffect effect) {
      return this.getEffects().contains(effect);
   }

   float getChance();
}
