package iskallia.vault.entity;

import iskallia.vault.entity.ai.AOEGoal;
import iskallia.vault.entity.ai.RegenAfterAWhile;
import iskallia.vault.entity.ai.TeleportGoal;
import iskallia.vault.entity.ai.TeleportRandomly;
import iskallia.vault.entity.ai.ThrowProjectilesGoal;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.sub.RampageDotAbility;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraft.world.server.ServerBossInfo;

public class BoogiemanEntity extends ZombieEntity implements VaultBoss {
   public TeleportRandomly<BoogiemanEntity> teleportTask = new TeleportRandomly(
      this, (entity, source, amount) -> !(source.func_76346_g() instanceof LivingEntity) ? 0.2 : 0.0
   );
   public final ServerBossInfo bossInfo = new ServerBossInfo(this.func_145748_c_(), Color.PURPLE, Overlay.PROGRESS);
   public RegenAfterAWhile<BoogiemanEntity> regenAfterAWhile = new RegenAfterAWhile(this);

   public BoogiemanEntity(EntityType<? extends ZombieEntity> type, World worldIn) {
      super(type, worldIn);
   }

   protected void func_213354_a(DamageSource damageSource, boolean attackedRecently) {
   }

   protected void func_207302_dI() {
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
      this.field_70714_bg.func_75776_a(1, new ThrowProjectilesGoal(this, 96, 10, FighterEntity.SNOWBALLS));
      this.field_70714_bg.func_75776_a(1, new AOEGoal(this, e -> !(e instanceof VaultBoss)));
      this.field_70715_bh.func_75776_a(1, new NearestAttackableTargetGoal(this, PlayerEntity.class, false));
      this.func_110148_a(Attributes.field_233819_b_).func_111128_a(100.0);
   }

   protected boolean func_190730_o() {
      return false;
   }

   public boolean func_70097_a(DamageSource source, float amount) {
      if (!(source instanceof RampageDotAbility.PlayerDamageOverTimeSource)
         && !(source.func_76346_g() instanceof PlayerEntity)
         && !(source.func_76346_g() instanceof EternalEntity)
         && source != DamageSource.field_76380_i) {
         return false;
      } else if (this.func_180431_b(source) || source == DamageSource.field_76379_h) {
         return false;
      } else if (this.teleportTask.attackEntityFrom(source, amount)) {
         return true;
      } else {
         this.regenAfterAWhile.onDamageTaken();
         return super.func_70097_a(source, amount);
      }
   }

   @Override
   public ServerBossInfo getServerBossInfo() {
      return this.bossInfo;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K) {
         this.bossInfo.func_186735_a(this.func_110143_aJ() / this.func_110138_aP());
         this.regenAfterAWhile.tick();
      }
   }

   public void func_184178_b(ServerPlayerEntity player) {
      super.func_184178_b(player);
      this.bossInfo.func_186760_a(player);
   }

   public void func_184203_c(ServerPlayerEntity player) {
      super.func_184203_c(player);
      this.bossInfo.func_186761_b(player);
   }

   public SoundCategory func_184176_by() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent func_184639_G() {
      return ModSounds.BOOGIE_AMBIENT;
   }

   protected SoundEvent func_184601_bQ(DamageSource damageSourceIn) {
      return ModSounds.BOOGIE_HURT;
   }

   protected SoundEvent func_184615_bR() {
      return ModSounds.BOOGIE_DEATH;
   }
}
