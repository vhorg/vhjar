package iskallia.vault.entity.entity;

import iskallia.vault.init.ModSounds;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TreasureGoblinEntity extends Monster {
   protected int disappearTick;
   protected boolean shouldDisappear;
   protected Player lastAttackedPlayer;

   public TreasureGoblinEntity(EntityType<? extends Monster> type, Level worldIn) {
      super(type, worldIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Player.class, 6.0F, 1.7F, 2.0));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 20.0F));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
   }

   public boolean isHitByPlayer() {
      return this.lastAttackedPlayer != null;
   }

   protected int calcDisappearTicks(Player player) {
      return 200;
   }

   public boolean hurt(DamageSource source, float amount) {
      Entity entity = source.getEntity();
      if (entity instanceof Player player && !entity.level.isClientSide && !this.isHitByPlayer()) {
         this.lastAttackedPlayer = player;
         this.disappearTick = this.calcDisappearTicks(player);
         this.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.disappearTick));
      }

      return super.hurt(source, amount);
   }

   public void tick() {
      super.tick();
      if (this.isAlive()) {
         if (this.isHitByPlayer()) {
            if (this.disappearTick <= 0) {
               this.shouldDisappear = true;
            }

            this.disappearTick--;
         }

         if (this.level instanceof ServerLevel serverLevel && this.shouldDisappear) {
            this.disappear(serverLevel);
         }
      }
   }

   public void disappear(ServerLevel world) {
      this.setRemoved(RemovalReason.DISCARDED);
      if (this.lastAttackedPlayer != null) {
         TextComponent bailText = (TextComponent)new TextComponent("Treasure Goblin escaped from you.")
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(8042883)));
         this.lastAttackedPlayer.displayClientMessage(bailText, true);
         this.lastAttackedPlayer.playNotifySound(ModSounds.GOBLIN_BAIL, SoundSource.MASTER, 0.7F, 1.0F);
         world.playSound(this.lastAttackedPlayer, this.getX(), this.getY(), this.getZ(), ModSounds.GOBLIN_BAIL, SoundSource.MASTER, 0.7F, 1.0F);
      } else {
         world.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.GOBLIN_BAIL, SoundSource.MASTER, 0.7F, 1.0F);
      }
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return ModSounds.GOBLIN_IDLE;
   }

   public SoundEvent getDeathSound() {
      return ModSounds.GOBLIN_DEATH;
   }

   public SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
      return ModSounds.GOBLIN_HURT;
   }
}
