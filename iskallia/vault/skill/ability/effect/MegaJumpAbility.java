package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.MegaJumpConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.server.ServerWorld;

public class MegaJumpAbility<C extends MegaJumpConfig> extends AbilityEffect<C> {
   @Override
   public String getAbilityGroupName() {
      return "Mega Jump";
   }

   public boolean onAction(C config, ServerPlayerEntity player, boolean active) {
      double magnitude = config.getHeight() * 0.15;
      double addY = -Math.min(0.0, player.func_213322_ci().func_82617_b());
      player.func_70024_g(0.0, addY + magnitude, 0.0);
      player.func_226567_ej_();
      player.field_70133_I = true;
      player.func_213823_a(ModSounds.MEGA_JUMP_SFX, SoundCategory.MASTER, 0.3F, 1.0F);
      ((ServerWorld)player.field_70170_p)
         .func_195598_a(ParticleTypes.field_197598_I, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), 50, 1.0, 0.5, 1.0, 0.0);
      return true;
   }
}
