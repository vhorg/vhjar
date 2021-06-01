package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.easteregg.GrasshopperNinja;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.MathUtilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class DashAbility extends PlayerAbility {
   @Expose
   private final int extraRadius;

   public DashAbility(int cost, int extraRadius) {
      super(cost, PlayerAbility.Behavior.RELEASE_TO_PERFORM);
      this.extraRadius = extraRadius;
   }

   public int getExtraRadius() {
      return this.extraRadius;
   }

   @Override
   public void onAction(PlayerEntity player, boolean active) {
      Vector3d lookVector = player.func_70040_Z();
      double magnitude = (10 + this.extraRadius) * 0.15;
      double extraPitch = 10.0;
      Vector3d dashVector = new Vector3d(lookVector.func_82615_a(), lookVector.func_82617_b(), lookVector.func_82616_c());
      float initialYaw = (float)MathUtilities.extractYaw(dashVector);
      dashVector = MathUtilities.rotateYaw(dashVector, initialYaw);
      double dashPitch = Math.toDegrees(MathUtilities.extractPitch(dashVector));
      if (dashPitch + extraPitch > 90.0) {
         dashVector = new Vector3d(0.0, 1.0, 0.0);
         dashPitch = 90.0;
      } else {
         dashVector = MathUtilities.rotateRoll(dashVector, (float)Math.toRadians(-extraPitch));
         dashVector = MathUtilities.rotateYaw(dashVector, -initialYaw);
         dashVector = dashVector.func_72432_b();
      }

      double coef = 1.6 - MathUtilities.map(Math.abs(dashPitch), 0.0, 90.0, 0.6, 1.0);
      dashVector = dashVector.func_186678_a(magnitude * coef);
      player.func_70024_g(dashVector.func_82615_a(), dashVector.func_82617_b(), dashVector.func_82616_c());
      player.field_70133_I = true;
      ((ServerWorld)player.field_70170_p)
         .func_195598_a(ParticleTypes.field_197598_I, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), 50, 1.0, 0.5, 1.0, 0.0);
      if (GrasshopperNinja.isGrasshopperShape(player)) {
         player.field_70170_p
            .func_184148_a(
               player,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               ModSounds.GRASSHOPPER_BRRR,
               SoundCategory.MASTER,
               1.0F,
               1.0F
            );
         player.func_213823_a(ModSounds.GRASSHOPPER_BRRR, SoundCategory.MASTER, 1.0F, 1.0F);
         GrasshopperNinja.achieve((ServerPlayerEntity)player);
      } else {
         player.field_70170_p
            .func_184148_a(
               player, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), ModSounds.DASH_SFX, SoundCategory.MASTER, 1.0F, 1.0F
            );
         player.func_213823_a(ModSounds.DASH_SFX, SoundCategory.MASTER, 1.0F, 1.0F);
      }
   }
}
