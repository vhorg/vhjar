package iskallia.vault.effect;

import iskallia.vault.mana.Mana;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ManaStealEffect extends MobEffect {
   public ManaStealEffect(ResourceLocation key, MobEffectCategory category, int color) {
      super(category, color);
      this.setRegistryName(key);
   }

   public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
      if (!livingEntity.getLevel().isClientSide() && livingEntity instanceof Player player) {
         Mana.decrease(player, Mana.getMax(player) * 0.005F * (amplifier + 1));
      }
   }

   public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
      return true;
   }
}
