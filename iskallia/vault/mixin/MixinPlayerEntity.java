package iskallia.vault.mixin;

import iskallia.vault.entity.entity.SpiritEntity;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.BlockChanceHelper;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({Player.class})
public abstract class MixinPlayerEntity extends LivingEntity implements BlockChanceHelper.PlayerBlockAnimationAccess {
   private static EntityDataAccessor<Boolean> PLAYER_BLOCKING;
   private int shieldActiveTimeout = 0;

   @Shadow
   public abstract void die(DamageSource var1);

   protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, Level worldIn) {
      super(type, worldIn);
   }

   @Inject(
      method = {"defineSynchedData"},
      at = {@At("TAIL")}
   )
   protected void addVisualBlockIndicatorEntry(CallbackInfo ci) {
      if (PLAYER_BLOCKING == null) {
         PLAYER_BLOCKING = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
      }

      this.getEntityData().define(PLAYER_BLOCKING, false);
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   protected void shieldTick(CallbackInfo ci) {
      if (!this.getLevel().isClientSide()) {
         if (this.shieldActiveTimeout > 0) {
            this.shieldActiveTimeout--;
         }

         if (this.shieldActiveTimeout <= 0) {
            this.getEntityData().set(PLAYER_BLOCKING, false);
         }
      }
   }

   @Inject(
      method = {"attack"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
         shift = Shift.BY,
         by = 2
      )},
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   public void triggerSweeping(
      Entity target,
      CallbackInfo ci,
      float damage,
      float damageBonus,
      float attackScale,
      boolean isHighScale,
      boolean canAttackKnockback,
      float knockbackStrength,
      boolean isCriticalHit,
      CriticalHitEvent criticalHitResult,
      boolean willSweepingAttack,
      double walkDistance,
      float targetHealth,
      boolean didIgnite,
      int fireAspectStrength,
      Vec3 targetMovement,
      boolean didHurt
   ) {
      if (didHurt && !willSweepingAttack) {
         Player thisPlayer = (Player)this;
         AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(thisPlayer);
         float chance = snapshot.getAttributeValue(ModGearAttributes.SWEEPING_HIT_CHANCE, VaultGearAttributeTypeMerger.floatSum());
         if (this.random.nextFloat() >= chance) {
            return;
         }

         float sweepingDamage = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * damage;

         for (LivingEntity livingentity : this.level
            .getEntitiesOfClass(LivingEntity.class, this.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(thisPlayer, target))) {
            if (livingentity != this
               && livingentity != target
               && !this.isAlliedTo(livingentity)
               && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker())
               && thisPlayer.canHit(livingentity, 0.0)) {
               livingentity.knockback(0.4F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
               livingentity.hurt(DamageSource.playerAttack(thisPlayer), sweepingDamage);
            }
         }

         this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
         thisPlayer.sweepAttack();
      }
   }

   @Override
   public void setForceBlocking() {
      this.shieldActiveTimeout = 20;
      this.getEntityData().set(PLAYER_BLOCKING, true);
   }

   @Override
   public boolean isForceBlocking() {
      return (Boolean)this.getEntityData().get(PLAYER_BLOCKING);
   }

   @Redirect(
      method = {"dropEquipment"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"
      )
   )
   public boolean ignoreKeepInventoryGameRuleInVault(GameRules instance, Key<BooleanValue> key) {
      return ServerVaults.isVaultWorld(this.level) || instance.getBoolean(key);
   }

   public boolean canBeRiddenInWater(Entity rider) {
      return rider instanceof SpiritEntity ? true : super.canBeRiddenInWater(rider);
   }
}
