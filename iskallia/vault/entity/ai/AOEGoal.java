package iskallia.vault.entity.ai;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class AOEGoal<T extends MobEntity> extends GoalTask<T> {
   protected boolean completed = false;
   protected boolean started = false;
   protected int tick = 0;
   protected int delay = 0;
   protected BlockPos shockwave;
   private final Predicate<LivingEntity> filter;

   public AOEGoal(T entity, Predicate<LivingEntity> filter) {
      super(entity);
      this.filter = filter;
   }

   public boolean func_75250_a() {
      return this.getRandom().nextInt(120) == 0 && this.getEntity().func_70638_az() != null;
   }

   public boolean func_75253_b() {
      return !this.completed;
   }

   public void func_75249_e() {
      this.getEntity().func_213317_d(this.getEntity().func_213322_ci().func_72441_c(0.0, 1.1, 0.0));
      this.delay = 5;
   }

   public void func_75246_d() {
      if (!this.completed) {
         if (!this.started && this.delay < 0 && this.getEntity().func_233570_aj_()) {
            this.getWorld()
               .func_184148_a(
                  null,
                  this.getEntity().func_226277_ct_(),
                  this.getEntity().func_226278_cu_(),
                  this.getEntity().func_226281_cx_(),
                  SoundEvents.field_187523_aM,
                  this.getEntity().func_184176_by(),
                  1.0F,
                  1.0F
               );
            ((ServerWorld)this.getWorld())
               .func_195598_a(
                  ParticleTypes.field_197627_t,
                  this.getEntity().func_226277_ct_() + 0.5,
                  this.getEntity().func_226278_cu_() + 0.1,
                  this.getEntity().func_226281_cx_() + 0.5,
                  10,
                  this.getRandom().nextGaussian() * 0.02,
                  this.getRandom().nextGaussian() * 0.02,
                  this.getRandom().nextGaussian() * 0.02,
                  1.0
               );
            this.shockwave = this.getEntity().func_233580_cy_();
            this.started = true;
         }

         if (this.started) {
            double max = 50.0;
            double distance = this.tick * 2;
            double nextDistance = this.tick * 2 + 2;
            if (distance >= max) {
               this.completed = true;
               return;
            }

            this.getWorld()
               .func_175647_a(LivingEntity.class, new AxisAlignedBB(this.shockwave).func_72314_b(max, max, max), e -> {
                  if (e != this.getEntity() && !e.func_175149_v() && this.filter.test(e)) {
                     double d = Math.sqrt(e.func_233580_cy_().func_177951_i(this.shockwave));
                     return d >= distance && d < nextDistance;
                  } else {
                     return false;
                  }
               })
               .forEach(
                  e -> {
                     Vector3d direction = new Vector3d(
                           e.func_226277_ct_() - this.shockwave.func_177958_n(),
                           e.func_226278_cu_() - this.shockwave.func_177956_o(),
                           e.func_226281_cx_() - this.shockwave.func_177952_p()
                        )
                        .func_186678_a(0.5);
                     direction = direction.func_72432_b().func_72441_c(0.0, 1.0 - 0.02 * (this.tick + 1), 0.0);
                     e.func_213317_d(e.func_213322_ci().func_178787_e(direction));
                     e.func_70097_a(DamageSource.field_76377_j, 8.0F / (this.tick + 1));
                  }
               );
            this.tick++;
         } else {
            this.delay--;
         }
      }
   }

   public void func_75251_c() {
      this.completed = false;
      this.started = false;
      this.tick = 0;
      this.delay = 0;
      this.shockwave = null;
   }
}
