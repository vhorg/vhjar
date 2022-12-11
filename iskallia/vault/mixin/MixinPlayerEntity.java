package iskallia.vault.mixin;

import iskallia.vault.entity.entity.SpiritEntity;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.BlockChanceHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

   @Redirect(
      method = {"attack"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getSweepingDamageRatio(Lnet/minecraft/world/entity/LivingEntity;)F"
      )
   )
   private float getSweepingEdgeRatio(LivingEntity entity) {
      Player thisPlayer = (Player)this;
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(thisPlayer);
      float chance = snapshot.getAttributeValue(ModGearAttributes.SWEEPING_HIT_CHANCE, VaultGearAttributeTypeMerger.floatSum());
      if (this.random.nextFloat() >= chance) {
         return EnchantmentHelper.getSweepingDamageRatio(thisPlayer);
      } else {
         int level = 1;
         return 1.0F - 1.0F / (level + 1);
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

   public boolean canBeRiddenInWater(Entity rider) {
      return rider instanceof SpiritEntity ? true : super.canBeRiddenInWater(rider);
   }
}
