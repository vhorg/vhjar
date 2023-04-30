package iskallia.vault.mixin;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Mob.class})
public abstract class MixinMob extends LivingEntity {
   protected MixinMob(EntityType<? extends LivingEntity> type, Level world) {
      super(type, world);
   }

   @Inject(
      method = {"getMeleeAttackRangeSqr"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getMeleeAttackRangeSqr(LivingEntity p_147273_, CallbackInfoReturnable<Double> ci) {
      AttributeInstance attribute = this.getAttribute(ModAttributes.REACH);
      if (attribute != null && attribute.getValue() > 0.0) {
         ci.setReturnValue(attribute.getValue() * attribute.getValue());
      }
   }

   @Inject(
      method = {"finalizeSpawn"},
      at = {@At("RETURN")}
   )
   public void finalizeSpawn(
      ServerLevelAccessor world,
      DifficultyInstance difficulty,
      MobSpawnType reason,
      SpawnGroupData data,
      CompoundTag nbt,
      CallbackInfoReturnable<SpawnGroupData> ci
   ) {
      CommonEvents.ENTITY_INITIALIZE.invoke((Mob)this, world, difficulty, reason, data, nbt);
   }
}
