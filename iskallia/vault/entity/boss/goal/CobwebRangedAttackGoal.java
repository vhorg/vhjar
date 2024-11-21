package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.ThrownCobwebEntity;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;

public class CobwebRangedAttackGoal extends RangedAttackGoalBase {
   public static final String TYPE = "cobweb_ranged_attack";
   private float inaccuracy = 12.0F;

   public CobwebRangedAttackGoal(VaultBossBaseEntity boss) {
      super(boss, 1.0, 60, 120, 25.0F, true);
   }

   public CobwebRangedAttackGoal setAttributes(
      double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius, float maxInaccuracy, boolean attackWhenInMeleeRange
   ) {
      this.setAttackAttributes(speedModifier, attackIntervalMin, attackIntervalMax, attackRadius, attackWhenInMeleeRange);
      this.inaccuracy = maxInaccuracy;
      return this;
   }

   @Override
   public boolean canUse() {
      return !super.canUse() ? false : this.targetNotTouchingCobweb();
   }

   private boolean targetNotTouchingCobweb() {
      return BlockPos.betweenClosedStream(this.target.getBoundingBox()).noneMatch(pos -> this.boss.getLevel().getBlockState(pos).getBlock() == Blocks.COBWEB);
   }

   @Override
   protected void performRangedAttack(LivingEntity target, float f1) {
      ThrownCobwebEntity cobweb = new ThrownCobwebEntity(this.boss.getLevel(), this.boss);
      cobweb.setPos(cobweb.getX(), this.boss.getY(0.5) + 0.5, cobweb.getZ());
      double vectorX = target.getX() - this.boss.getX();
      double vectorY = target.getY() - cobweb.getY();
      double vectorZ = target.getZ() - this.boss.getZ();
      double gravityAdjustment = Math.sqrt(vectorX * vectorX + vectorZ * vectorZ) * 0.2F;
      cobweb.shoot(vectorX, vectorY + gravityAdjustment, vectorZ, 0.8F, this.inaccuracy);
      this.boss.getLevel().addFreshEntity(cobweb);
   }

   @Override
   public String getType() {
      return "cobweb_ranged_attack";
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putFloat("Inaccuracy", this.inaccuracy);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      super.deserializeNBT(nbt, boss);
      this.inaccuracy = nbt.getFloat("Inaccuracy");
   }

   public void start() {
      super.start();
      if (this.boss.getActiveAttackMove().isEmpty()) {
         this.boss.setActiveAttackMove("cobweb_ranged_attack");
      }
   }

   @Override
   public void stop() {
      super.stop();
      if (this.boss.getActiveAttackMove().map(attackMove -> attackMove.equals("cobweb_ranged_attack")).orElse(false)) {
         this.boss.setActiveAttackMove("");
      }
   }
}
