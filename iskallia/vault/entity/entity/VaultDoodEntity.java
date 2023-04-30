package iskallia.vault.entity.entity;

import iskallia.vault.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class VaultDoodEntity extends IronGolem {
   public VaultDoodEntity(EntityType<? extends IronGolem> entityType, Level world) {
      super(entityType, world);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return ModSounds.VAULT_DOOD_IDLE;
   }

   public int getAmbientSoundInterval() {
      return 200;
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide) {
         Player nearestPlayer = this.level.getNearestPlayer(this, 6.0);
         if (nearestPlayer != null) {
            this.getNavigation().moveTo(nearestPlayer, 1.0);
         }
      }
   }
}
