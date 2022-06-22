package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.EyesoreEntity;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.gen.piece.FinalVaultBoss;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class LaserAttackTask extends EyesoreTask<EyesoreEntity> {
   public int tick = 0;
   public UUID target;

   public LaserAttackTask(EyesoreEntity entity) {
      super(entity);
   }

   @Override
   public void tick() {
      if (!this.isFinished()) {
         if (this.target == null) {
            List<ServerPlayerEntity> players = this.getVault()
               .getPlayers()
               .stream()
               .filter(player -> player instanceof VaultRunner)
               .map(p -> p.getServerPlayer(this.getWorld().func_73046_m()))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .collect(Collectors.toList());
            if (players.size() == 0) {
               return;
            }

            ServerPlayerEntity player = players.get(this.getRandom().nextInt(players.size()));
            this.target = player.func_110124_au();
            this.getEntity().func_184212_Q().func_187227_b(EyesoreEntity.LASER_TARGET, Optional.of(player.func_110124_au()));
            this.getVault()
               .getPlayers()
               .stream()
               .map(p -> p.getServerPlayer(this.getWorld().func_73046_m()))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .forEach(
                  p -> this.getWorld()
                     .func_184148_a(
                        null, p.func_226277_ct_(), p.func_226278_cu_(), p.func_226281_cx_(), SoundEvents.field_187514_aD, SoundCategory.HOSTILE, 10.0F, 1.0F
                     )
               );
         }

         Entity entity = this.getWorld().func_217461_a(this.target);
         LivingEntity targetEntity = entity instanceof LivingEntity ? (LivingEntity)entity : null;
         if (targetEntity != null) {
            double distance = this.getEntity().func_233580_cy_().func_177951_i(targetEntity.func_233580_cy_());
            Optional<FinalVaultBoss> finalVault = this.getVault().getGenerator().getPieces(FinalVaultBoss.class).stream().findFirst();
            if (finalVault.isPresent()) {
               Vector3i center = finalVault.get().getCenter();
               this.getEntity()
                  .path
                  .stayInRange(new Vector3d(center.func_177958_n(), center.func_177956_o(), center.func_177952_p()), targetEntity, 0.15, 30.0, 2.0);
            } else {
               this.getEntity().path.stayInRange(this.getEntity(), targetEntity, 0.15, 30.0, 2.0);
            }

            Vector3d eyePos1 = this.getEntity().func_174824_e(1.0F);
            Vector3d eyePos2 = this.getPosition(targetEntity);
            RayTraceContext context = new RayTraceContext(eyePos1, eyePos2, BlockMode.COLLIDER, FluidMode.NONE, this.getEntity());
            BlockRayTraceResult result = this.getWorld().func_217299_a(context);
            if (result.func_216346_c() == Type.MISS) {
               targetEntity.func_195064_c(new EffectInstance(Effects.field_76421_d, 60, 2, false, false));
               DamageSource source = new EntityDamageSource("laser", this.getEntity()).func_82726_p();
               float damage = ModConfigs.EYESORE.laserAttack.getDamage(this.getEntity(), this.tick);
               if (damage > 0.0F) {
                  targetEntity.func_70097_a(source, damage);
               }

               if (this.getWorld().func_82737_E() % 10L == 0L) {
                  this.getWorld()
                     .func_195598_a(
                        ParticleTypes.field_197601_L,
                        targetEntity.func_226277_ct_(),
                        targetEntity.func_226278_cu_(),
                        targetEntity.func_226281_cx_(),
                        300,
                        0.0,
                        0.0,
                        0.0,
                        0.001
                     );
               }
            } else {
               if (this.getWorld().func_82737_E() % 10L == 0L) {
                  this.getWorld().func_225521_a_(result.func_216350_a(), true, this.getEntity());
               }

               if (this.getWorld().func_82737_E() % 10L == 0L) {
                  this.getWorld()
                     .func_195598_a(
                        ParticleTypes.field_197601_L,
                        result.func_216347_e().field_72450_a,
                        result.func_216347_e().field_72448_b,
                        result.func_216347_e().field_72449_c,
                        300,
                        0.0,
                        0.0,
                        0.0,
                        0.001
                     );
               }
            }
         }

         this.tick++;
         if (this.isFinished()) {
            this.getEntity().func_184212_Q().func_187227_b(EyesoreEntity.LASER_TARGET, Optional.empty());
         }
      }
   }

   protected void lookAtTarget(LivingEntity target) {
      this.getEntity().field_70125_A = this.getTargetPitch(target);
      this.getEntity().field_70759_as = this.getTargetYaw(target);
   }

   private double getEyePosition(Entity entity) {
      return entity instanceof LivingEntity ? entity.func_226280_cw_() : (entity.func_174813_aQ().field_72338_b + entity.func_174813_aQ().field_72337_e) / 2.0;
   }

   protected float getTargetPitch(LivingEntity target) {
      double d0 = target.func_226277_ct_() - this.getEntity().func_226277_ct_();
      double d1 = this.getEyePosition(target) - this.getEntity().func_226280_cw_();
      double d2 = target.func_226281_cx_() - this.getEntity().func_226281_cx_();
      double d3 = MathHelper.func_76133_a(d0 * d0 + d2 * d2);
      return (float)(-(MathHelper.func_181159_b(d1, d3) * 180.0F / (float)Math.PI));
   }

   protected float getTargetYaw(LivingEntity target) {
      double d0 = target.func_226277_ct_() - this.getEntity().func_226277_ct_();
      double d1 = target.func_226281_cx_() - this.getEntity().func_226281_cx_();
      return (float)(MathHelper.func_181159_b(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
   }

   private Vector3d getPosition(Entity entityLivingBaseIn) {
      double d0 = entityLivingBaseIn.func_226277_ct_();
      double d1 = entityLivingBaseIn.func_226278_cu_() + entityLivingBaseIn.func_213302_cg() / 2.0F;
      double d2 = entityLivingBaseIn.func_226281_cx_();
      return new Vector3d(d0, d1, d2);
   }

   @Override
   public boolean isFinished() {
      return this.getVault() == null ? true : this.tick >= 100;
   }

   @Override
   public void reset() {
      this.tick = 0;
      this.target = null;
   }
}
