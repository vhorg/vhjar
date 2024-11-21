package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class ThrowPotionGoal extends RangedAttackGoalBase {
   public static final String TYPE = "throw_potion";
   private final VaultBossBaseEntity boss;
   @Nullable
   private MobEffect mobEffect = null;
   private int duration;
   private int amplifier;
   private boolean lingering;

   public ThrowPotionGoal(VaultBossBaseEntity boss) {
      super(boss, 1.0, 100, 200, 10.0F, true);
      this.boss = boss;
   }

   public ThrowPotionGoal setAttributes(
      double speedModifier,
      int throwIntervalMin,
      int throwIntervalMax,
      float attackRadius,
      MobEffect mobEffect,
      int duration,
      int amplifier,
      boolean lingering,
      boolean attackWhenInMeleeRange
   ) {
      this.setAttackAttributes(speedModifier, throwIntervalMin, throwIntervalMax, attackRadius, attackWhenInMeleeRange);
      this.mobEffect = mobEffect;
      this.duration = duration;
      this.amplifier = amplifier;
      this.lingering = lingering;
      return this;
   }

   @Override
   public String getType() {
      return "throw_potion";
   }

   @Override
   protected void performRangedAttack(LivingEntity target, float normalizedDistance) {
      Vec3 targetDeltaMovement = target.getDeltaMovement();
      ThrownPotion thrownpotion = new ThrownPotion(this.boss.level, this.boss);
      double xDiff = target.getX() + targetDeltaMovement.x - this.boss.getX();
      double yDiff = target.getEyeY() - thrownpotion.getY();
      double zDiff = target.getZ() + targetDeltaMovement.z - this.boss.getZ();
      double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
      ItemStack potionStack = new ItemStack(this.lingering ? Items.LINGERING_POTION : Items.SPLASH_POTION);
      thrownpotion.setItem(PotionUtils.setCustomEffects(potionStack, List.of(new MobEffectInstance(this.mobEffect, this.duration, this.amplifier))));
      thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
      thrownpotion.shoot(xDiff, yDiff + distance * 0.2, zDiff, (float)(0.75 * (Math.min(distance, 4.0) / 4.0)), 4.0F);
      this.boss.playSound(SoundEvents.WITCH_THROW, 1.0F, 0.8F + this.boss.getRandom().nextFloat() * 0.4F);
      this.boss.getLevel().addFreshEntity(thrownpotion);
   }

   @Override
   public boolean canUse() {
      return super.canUse() && this.mobEffect != null;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      if (this.mobEffect == null) {
         return nbt;
      } else {
         nbt.putString("MobEffect", this.mobEffect.getRegistryName().toString());
         nbt.putInt("Duration", this.duration);
         nbt.putInt("Amplifier", this.amplifier);
         nbt.putBoolean("Lingering", this.lingering);
         return nbt;
      }
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      super.deserializeNBT(nbt, boss);
      if (nbt.contains("MobEffect")) {
         this.mobEffect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(nbt.getString("MobEffect")));
         this.duration = nbt.getInt("Duration");
         this.amplifier = nbt.getInt("Amplifier");
         this.lingering = nbt.getBoolean("Lingering");
      }
   }
}
