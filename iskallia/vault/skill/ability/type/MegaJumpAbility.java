package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class MegaJumpAbility extends PlayerAbility {
   @Expose
   private final int extraHeight;

   public MegaJumpAbility(int cost, int extraHeight) {
      super(cost, PlayerAbility.Behavior.RELEASE_TO_PERFORM);
      this.extraHeight = extraHeight;
   }

   public int getExtraHeight() {
      return this.extraHeight;
   }

   @Override
   public void onAction(PlayerEntity player, boolean active) {
      double magnitude = (10 + this.extraHeight) * 0.15;
      Vector3d jumpVector = new Vector3d(0.0, 1.0, 0.0);
      jumpVector = jumpVector.func_186678_a(magnitude);
      player.func_70024_g(jumpVector.func_82615_a(), jumpVector.func_82617_b(), jumpVector.func_82616_c());
      player.func_226567_ej_();
      player.field_70133_I = true;
      player.func_213823_a(ModSounds.MEGA_JUMP_SFX, SoundCategory.MASTER, 0.7F, 1.0F);
      ((ServerWorld)player.field_70170_p)
         .func_195598_a(ParticleTypes.field_197598_I, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), 50, 1.0, 0.5, 1.0, 0.0);
   }
}
