package iskallia.vault.entity.entity;

import iskallia.vault.entity.VaultBoss;
import iskallia.vault.entity.ai.AOEGoal;
import iskallia.vault.entity.ai.RegenAfterAWhile;
import iskallia.vault.entity.ai.TeleportGoal;
import iskallia.vault.entity.ai.TeleportRandomly;
import iskallia.vault.entity.ai.ThrowProjectilesGoal;
import iskallia.vault.init.ModSounds;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BoogiemanEntity extends Zombie implements VaultBoss {
   public TeleportRandomly<BoogiemanEntity> teleportTask = new TeleportRandomly(
      this, (entity, source, amount) -> !(source.getEntity() instanceof LivingEntity) ? 0.2 : 0.0
   );
   public final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.PROGRESS);
   public RegenAfterAWhile<BoogiemanEntity> regenAfterAWhile = new RegenAfterAWhile(this);

   public BoogiemanEntity(EntityType<? extends Zombie> type, Level worldIn) {
      super(type, worldIn);
   }

   protected void dropFromLootTable(DamageSource damageSource, boolean attackedRecently) {
   }

   protected void doUnderWaterConversion() {
   }

   protected void addBehaviourGoals() {
      super.addBehaviourGoals();
      this.goalSelector
         .addGoal(
            1,
            TeleportGoal.builder(this)
               .start(entity -> entity.getTarget() != null && entity.tickCount % 60 == 0)
               .to(
                  entity -> entity.getTarget()
                     .position()
                     .add((entity.random.nextDouble() - 0.5) * 8.0, entity.random.nextInt(16) - 8, (entity.random.nextDouble() - 0.5) * 8.0)
               )
               .then(entity -> entity.playSound(ModSounds.BOSS_TP_SFX, 1.0F, 1.0F))
               .build()
         );
      this.goalSelector.addGoal(1, new ThrowProjectilesGoal(this, 96, 10, FighterEntity.SNOWBALLS));
      this.goalSelector.addGoal(1, new AOEGoal(this, e -> !(e instanceof VaultBoss)));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, false));
      this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(100.0);
   }

   protected boolean isSunSensitive() {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      if (!(source.getEntity() instanceof Player) && !(source.getEntity() instanceof EternalEntity) && source != DamageSource.OUT_OF_WORLD) {
         return false;
      } else if (this.isInvulnerableTo(source) || source == DamageSource.FALL) {
         return false;
      } else if (this.teleportTask.attackEntityFrom(source, amount)) {
         return true;
      } else {
         this.regenAfterAWhile.onDamageTaken();
         return super.hurt(source, amount);
      }
   }

   @Override
   public ServerBossEvent getServerBossInfo() {
      return this.bossInfo;
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide) {
         this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
         this.regenAfterAWhile.tick();
      }
   }

   public void startSeenByPlayer(ServerPlayer player) {
      super.startSeenByPlayer(player);
      this.bossInfo.addPlayer(player);
   }

   public void stopSeenByPlayer(ServerPlayer player) {
      super.stopSeenByPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.BOOGIE_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return ModSounds.BOOGIE_HURT;
   }

   public SoundEvent getDeathSound() {
      return ModSounds.BOOGIE_DEATH;
   }
}
