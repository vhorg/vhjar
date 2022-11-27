package iskallia.vault.entity.entity;

import iskallia.vault.skill.ability.effect.sub.NovaDotAbility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class VaultGuardianEntity extends PiglinBrute {
   public VaultGuardianEntity(EntityType<? extends PiglinBrute> type, Level world) {
      super(type, world);
      this.setCanPickUpLoot(false);
      AttributeInstance attribute = this.getAttribute(Attributes.ATTACK_KNOCKBACK);
      if (attribute != null) {
         attribute.setBaseValue(6.0);
      }
   }

   protected void dropFromLootTable(DamageSource source, boolean attackedRecently) {
   }

   public boolean hurt(DamageSource source, float amount) {
      if (!(source instanceof NovaDotAbility.PlayerDamageOverTimeSource)
         && !(source.getEntity() instanceof Player)
         && !(source.getEntity() instanceof EternalEntity)
         && source != DamageSource.OUT_OF_WORLD) {
         return false;
      } else if (!this.isInvulnerableTo(source) && source != DamageSource.FALL) {
         this.playHurtSound(source);
         return super.hurt(source, amount);
      } else {
         return false;
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return super.isInvulnerableTo(source) || source.isProjectile();
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setImmuneToZombification(true);
      this.timeInOverworld = compound.getInt("TimeInOverworld");
   }

   public void knockback(double p_147241_, double p_147242_, double p_147243_) {
   }

   protected float getBlockSpeedFactor() {
      return 0.75F;
   }
}
