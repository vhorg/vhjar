package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class GhostWalkAbility extends EffectAbility {
   @Expose
   private int durationTicks;

   public int getDurationTicks() {
      return this.durationTicks;
   }

   public GhostWalkAbility(int cost, Effect effect, int level, int durationTicks, EffectAbility.Type type, PlayerAbility.Behavior behavior) {
      super(cost, effect, level, type, behavior);
      this.durationTicks = durationTicks;
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
            player, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), ModSounds.GHOST_WALK_SFX, SoundCategory.MASTER, 0.7F, 1.0F
         );
      player.func_213823_a(ModSounds.GHOST_WALK_SFX, SoundCategory.MASTER, 0.7F, 1.0F);
   }

   @Override
   public void onBlur(PlayerEntity player) {
   }

   @SubscribeEvent
   public static void onDamage(LivingDamageEvent event) {
      Entity e = event.getSource().func_76346_g();
      if (e instanceof LivingEntity) {
         LivingEntity living = (LivingEntity)e;
         EffectInstance ghostWalk = living.func_70660_b(ModEffects.GHOST_WALK);
         if (ghostWalk != null) {
            living.func_195063_d(ModEffects.GHOST_WALK);
         }
      }
   }
}
