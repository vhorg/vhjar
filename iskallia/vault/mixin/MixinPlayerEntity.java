package iskallia.vault.mixin;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.entity.entity.SpiritEntity;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.WingsTrinket;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.BlockChanceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {Player.class},
   priority = 1111
)
public abstract class MixinPlayerEntity extends LivingEntity implements BlockChanceHelper.PlayerBlockAnimationAccess {
   private boolean actualOnGround;
   @Shadow
   @Final
   private Inventory inventory;
   private static EntityDataAccessor<Boolean> PLAYER_BLOCKING;
   private int shieldActiveTimeout = 0;

   @Shadow
   public abstract void die(DamageSource var1);

   @Shadow
   public abstract void remove(RemovalReason var1);

   @Shadow
   public abstract void handleEntityEvent(byte var1);

   @Shadow
   protected abstract void actuallyHurt(DamageSource var1, float var2);

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
      method = {"tryToStartFallFlying"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
      )},
      cancellable = true
   )
   protected void tryToStartFallFlying(CallbackInfoReturnable<Boolean> cir) {
      Player player = (Player)this;
      TrinketHelper.getTrinkets(player, WingsTrinket.class).forEach(wings -> {
         if (wings.isUsable(player)) {
            player.startFallFlying();
            cir.setReturnValue(true);
         }
      });
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
      ActiveFlags.IS_AOE_ATTACKING.push();
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

   @Inject(
      method = {"attack"},
      at = {@At(
         value = "INVOKE",
         shift = Shift.AFTER,
         target = "Lnet/minecraft/world/entity/player/Player;sweepAttack()V"
      )}
   )
   public void sweepAttack(Entity pTargetEntity, CallbackInfo ci) {
      ActiveFlags.IS_AOE_ATTACKING.pop();
   }

   @Inject(
      method = {"attack"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
      )},
      cancellable = true
   )
   private void preventDamageIndicatorHearts(Entity entity, CallbackInfo ci) {
      ci.cancel();
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

   @Redirect(
      method = {"getDigSpeed"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Inventory;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"
      )
   )
   public float getDigSpeed(Inventory inventory, BlockState state) {
      float base = inventory.getDestroySpeed(state);
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(this);
      float bonus = snapshot.getAttributeValue(ModGearAttributes.MINING_SPEED, VaultGearAttributeTypeMerger.floatSum());
      if (inventory.getSelected().getItem() == ModItems.TOOL) {
         return base > 1.0F ? bonus : base;
      } else {
         return base > 1.0F ? base + bonus : base;
      }
   }

   @Inject(
      method = {"getDigSpeed"},
      at = {@At("RETURN")},
      remap = false,
      cancellable = true
   )
   private void adjustBreakSpeed(BlockState state, BlockPos pos, CallbackInfoReturnable<Float> cir) {
      Player thisPlayer = (Player)this;
      cir.setReturnValue(CommonEvents.BLOCK_BREAK_SPEED.invoke(thisPlayer, pos, state, cir.getReturnValueF()).getSpeed());
   }

   @ModifyArg(
      method = {"attack"},
      index = 4,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
      )
   )
   private SoundEvent playNoDamageSoundWhenBroken(SoundEvent sound) {
      Player thisPlayer = (Player)this;
      ItemStack mainHandItem = thisPlayer.getMainHandItem();
      return mainHandItem.getItem() instanceof VaultGearItem gearItem && gearItem.isBroken(mainHandItem) ? SoundEvents.PLAYER_ATTACK_NODAMAGE : sound;
   }
}
