package iskallia.vault.mixin;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.mana.ManaPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Player.class})
public abstract class MixinManaPlayerEntity extends LivingEntity implements ManaPlayer {
   private static EntityDataAccessor<Float> MANA;

   protected MixinManaPlayerEntity(EntityType<? extends LivingEntity> type, Level worldIn) {
      super(type, worldIn);
   }

   @Inject(
      method = {"defineSynchedData"},
      at = {@At("TAIL")}
   )
   protected void ifYouImmediatelyKnowTheCandleLightIsFireThenTheMealWasCookedALongTimeAgo(CallbackInfo ci) {
      if (MANA == null) {
         MANA = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
      }

      this.player().getEntityData().define(MANA, 100.0F);
   }

   @Inject(
      method = {"readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"},
      at = {@At("TAIL")}
   )
   protected void load(CompoundTag nbt, CallbackInfo ci) {
      if (nbt.contains("mana", 5)) {
         this.player().getEntityData().set(MANA, nbt.getFloat("mana"));
      }
   }

   @Inject(
      method = {"addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"},
      at = {@At("TAIL")}
   )
   protected void save(CompoundTag nbt, CallbackInfo ci) {
      float mana = (Float)this.player().getEntityData().get(MANA);
      nbt.putFloat("mana", mana);
   }

   private Player player() {
      return (Player)this;
   }

   @Override
   public float getMana() {
      return (Float)this.player().getEntityData().get(MANA);
   }

   @Override
   public float setMana(float amount) {
      Player player = this.player();
      ManaPlayer manaPlayer = (ManaPlayer)player;
      float manaMax = manaPlayer.getManaMax();
      float clampedAmount = Mth.clamp(amount, 0.0F, manaMax);
      if (player instanceof ServerPlayer serverPlayer) {
         serverPlayer.getEntityData().set(MANA, clampedAmount);
         return clampedAmount;
      } else {
         throw new IllegalStateException("Don't call this from the client!");
      }
   }

   @Override
   public float getManaMax() {
      AttributeInstance attributeInstance = this.player().getAttribute(ModAttributes.MANA_MAX);
      return attributeInstance != null ? (float)attributeInstance.getValue() : 0.0F;
   }

   @Override
   public float getManaRegenPerSecond() {
      AttributeInstance attributeInstance = this.player().getAttribute(ModAttributes.MANA_REGEN);
      return attributeInstance != null ? (float)attributeInstance.getValue() : 0.0F;
   }

   @Override
   public float increaseMana(float amount) {
      return this.setMana(this.getMana() + amount);
   }

   @Override
   public float decreaseMana(float amount) {
      return this.increaseMana(-amount);
   }
}
