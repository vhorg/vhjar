package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.trait.ITrait;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;

public class HealGoal extends Goal implements ITrait {
   public static final String TYPE = "heal";
   private final VaultBossBaseEntity boss;
   private int healCooldown = 0;
   private int healInterval = 20;
   private float healPercentage = 0.1F;

   public HealGoal(VaultBossBaseEntity boss) {
      this.boss = boss;
   }

   public HealGoal setAttributes(int healInterval, float healPercentage) {
      this.healInterval = healInterval;
      this.healPercentage = healPercentage;
      return this;
   }

   @Override
   public String getType() {
      return "heal";
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addTraitGoal(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof HealGoal healGoal) {
         int stackSize = healGoal.healInterval / this.healInterval;
         this.healInterval = healGoal.healInterval * ++stackSize;
      }
   }

   public boolean canUse() {
      return this.boss.getHealth() < this.boss.getMaxHealth();
   }

   public void start() {
      super.start();
      this.healCooldown = this.healInterval;
   }

   public void tick() {
      super.tick();
      if (this.healCooldown > 0) {
         this.healCooldown--;
      } else {
         this.boss.heal(this.boss.getMaxHealth() * this.healPercentage);
         if (this.boss.getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((SimpleParticleType)ModParticles.HEAL.get(), this.boss.getX(), this.boss.getY(), this.boss.getZ(), 10, 0.5, 0.5, 0.5, 0.0);
            serverLevel.playSound(null, this.boss.getX(), this.boss.getY(), this.boss.getZ(), ModSounds.HEAL, SoundSource.HOSTILE, 0.5F, 0.95F);
         }

         this.healCooldown = this.healInterval;
      }
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("HealCooldown", this.healCooldown);
      nbt.putInt("HealInterval", this.healInterval);
      nbt.putFloat("HealPercentage", this.healPercentage);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      this.healCooldown = nbt.getInt("HealCooldown");
      this.healInterval = nbt.getInt("HealInterval");
      this.healPercentage = nbt.getFloat("HealPercentage");
   }
}
