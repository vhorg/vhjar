package iskallia.vault.entity.ai;

import iskallia.vault.entity.AggressiveCowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class CowDashAttackGoal extends Goal {
   protected final AggressiveCowEntity entity;
   private final float dashStrength;

   public CowDashAttackGoal(AggressiveCowEntity entity, float dashStrength) {
      this.entity = entity;
      this.dashStrength = 0.4F;
   }

   public boolean func_75250_a() {
      LivingEntity target = this.entity.func_70638_az();
      if (!(target instanceof PlayerEntity) || !target.func_70089_S()) {
         return false;
      } else if (!this.entity.canDash()) {
         return false;
      } else {
         double dist = this.entity.func_70092_e(target.func_226277_ct_(), target.func_226278_cu_(), target.func_226281_cx_());
         double attackReach = this.entity.func_213311_cf() * 2.0F * this.entity.func_213311_cf() * 2.0F + target.func_213311_cf();
         return dist >= attackReach * 4.0 && dist <= attackReach * 16.0;
      }
   }

   public boolean func_75253_b() {
      return false;
   }

   public void func_75246_d() {
      LivingEntity target = this.entity.func_70638_az();
      if (target instanceof PlayerEntity && target.func_70089_S()) {
         Vector3d dir = target.func_174824_e(1.0F).func_178788_d(this.entity.func_213303_ch());
         dir = dir.func_216372_d(this.dashStrength, this.dashStrength, this.dashStrength);
         if (dir.func_82617_b() <= 0.4) {
            dir = new Vector3d(dir.func_82615_a(), 0.4, dir.func_82616_c());
         }

         this.entity.func_70024_g(dir.field_72450_a, dir.field_72448_b, dir.field_72449_c);
         this.entity.onDash();
      }
   }
}
