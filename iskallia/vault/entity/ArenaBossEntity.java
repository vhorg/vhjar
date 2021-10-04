package iskallia.vault.entity;

import iskallia.vault.entity.ai.AOEGoal;
import iskallia.vault.entity.ai.TeleportGoal;
import iskallia.vault.entity.ai.TeleportRandomly;
import iskallia.vault.entity.ai.ThrowProjectilesGoal;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.sub.RampageDotAbility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class ArenaBossEntity extends FighterEntity {
   public TeleportRandomly<ArenaBossEntity> teleportTask = new TeleportRandomly(
      this, (entity, source, amount) -> !(source.func_76346_g() instanceof LivingEntity) ? 0.2 : 0.0
   );

   public ArenaBossEntity(EntityType<? extends ZombieEntity> type, World world) {
      super(type, world);
      if (!this.field_70170_p.field_72995_K) {
         this.func_110148_a(Attributes.field_233820_c_).func_111128_a(1000000.0);
      }

      this.bossInfo.func_186758_d(true);
   }

   protected void func_175456_n() {
      super.func_175456_n();
      this.field_70714_bg
         .func_75776_a(
            1,
            TeleportGoal.builder(this)
               .start(entity -> entity.func_70638_az() != null && entity.field_70173_aa % 60 == 0)
               .to(
                  entity -> entity.func_70638_az()
                     .func_213303_ch()
                     .func_72441_c(
                        (entity.field_70146_Z.nextDouble() - 0.5) * 8.0, entity.field_70146_Z.nextInt(16) - 8, (entity.field_70146_Z.nextDouble() - 0.5) * 8.0
                     )
               )
               .then(entity -> entity.func_184185_a(ModSounds.BOSS_TP_SFX, 1.0F, 1.0F))
               .build()
         );
      this.field_70714_bg.func_75776_a(1, new ThrowProjectilesGoal(this, 96, 10, SNOWBALLS));
      this.field_70714_bg.func_75776_a(1, new AOEGoal(this, e -> !(e instanceof ArenaBossEntity)));
      this.func_110148_a(Attributes.field_233819_b_).func_111128_a(100.0);
   }

   private float knockbackAttack(Entity entity) {
      for (int i = 0; i < 20; i++) {
         double d0 = this.field_70170_p.field_73012_v.nextGaussian() * 0.02;
         double d1 = this.field_70170_p.field_73012_v.nextGaussian() * 0.02;
         double d2 = this.field_70170_p.field_73012_v.nextGaussian() * 0.02;
         ((ServerWorld)this.field_70170_p)
            .func_195598_a(
               ParticleTypes.field_197598_I,
               entity.func_226277_ct_() + this.field_70170_p.field_73012_v.nextDouble() - d0,
               entity.func_226278_cu_() + this.field_70170_p.field_73012_v.nextDouble() - d1,
               entity.func_226281_cx_() + this.field_70170_p.field_73012_v.nextDouble() - d2,
               10,
               d0,
               d1,
               d2,
               1.0
            );
      }

      this.field_70170_p.func_184133_a(null, entity.func_233580_cy_(), SoundEvents.field_187602_cF, this.func_184176_by(), 1.0F, 1.0F);
      return 15.0F;
   }

   @Override
   public boolean func_70652_k(Entity entity) {
      boolean ret = false;
      if (this.field_70146_Z.nextInt(12) == 0) {
         double old = this.func_110148_a(Attributes.field_233824_g_).func_111125_b();
         this.func_110148_a(Attributes.field_233824_g_).func_111128_a(this.knockbackAttack(entity));
         boolean result = super.func_70652_k(entity);
         this.func_110148_a(Attributes.field_233824_g_).func_111128_a(old);
         ret |= result;
      }

      if (this.field_70146_Z.nextInt(6) == 0) {
         this.field_70170_p.func_72960_a(this, (byte)4);
         float f = (float)this.func_233637_b_(Attributes.field_233823_f_);
         float f1 = (int)f > 0 ? f / 2.0F + this.field_70146_Z.nextInt((int)f) : f;
         boolean flag = entity.func_70097_a(DamageSource.func_76358_a(this), f1);
         if (flag) {
            entity.func_213317_d(entity.func_213322_ci().func_72441_c(0.0, 0.6F, 0.0));
            this.func_174815_a(this, entity);
         }

         this.field_70170_p.func_184133_a(null, entity.func_233580_cy_(), SoundEvents.field_187602_cF, this.func_184176_by(), 1.0F, 1.0F);
         ret |= flag;
      }

      return ret || super.func_70652_k(entity);
   }

   public boolean func_70097_a(DamageSource source, float amount) {
      if (!(source instanceof RampageDotAbility.PlayerDamageOverTimeSource)
         && !(source.func_76346_g() instanceof PlayerEntity)
         && !(source.func_76346_g() instanceof EternalEntity)
         && source != DamageSource.field_76380_i) {
         return false;
      } else if (this.func_180431_b(source) || source == DamageSource.field_76379_h) {
         return false;
      } else {
         return this.teleportTask.attackEntityFrom(source, amount) ? true : super.func_70097_a(source, amount);
      }
   }

   public static MutableAttribute getAttributes() {
      return MonsterEntity.func_234295_eP_()
         .func_233815_a_(Attributes.field_233819_b_, 35.0)
         .func_233815_a_(Attributes.field_233821_d_, 0.23F)
         .func_233815_a_(Attributes.field_233823_f_, 3.0)
         .func_233815_a_(Attributes.field_233826_i_, 2.0)
         .func_233814_a_(Attributes.field_233829_l_);
   }
}
