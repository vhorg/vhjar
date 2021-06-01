package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;

public class RampageAbility extends EffectAbility {
   @Expose
   private int durationTicks;
   @Expose
   private int damageIncrease;

   public int getDurationTicks() {
      return this.durationTicks;
   }

   public int getDamageIncrease() {
      return this.damageIncrease;
   }

   public RampageAbility(
      int cost, Effect effect, int level, int damageIncrease, int durationTicks, int cooldown, EffectAbility.Type type, PlayerAbility.Behavior behavior
   ) {
      super(cost, effect, level, type, behavior);
      this.damageIncrease = damageIncrease;
      this.durationTicks = durationTicks;
      this.cooldown = cooldown;
   }

   @Override
   public void onTick(PlayerEntity player, boolean active) {
   }

   @Override
   public void onAction(PlayerEntity player, boolean active) {
      EffectInstance activeEffect = player.func_70660_b(this.getEffect());
      EffectInstance newEffect = new EffectInstance(
         this.getEffect(), this.getDurationTicks(), this.getAmplifier(), false, this.getType().showParticles, this.getType().showIcon
      );
      if (activeEffect == null) {
         player.func_195064_c(newEffect);
      }

      player.field_70170_p
         .func_184148_a(
            null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), ModSounds.RAMPAGE_SFX, SoundCategory.MASTER, 0.175F, 1.0F
         );
      player.func_213823_a(ModSounds.RAMPAGE_SFX, SoundCategory.MASTER, 0.175F, 1.0F);
   }

   @Override
   public void onBlur(PlayerEntity player) {
   }
}
