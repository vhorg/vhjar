package iskallia.vault.mixin;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.modifier.spi.predicate.IModifierImmunity;
import iskallia.vault.core.vault.modifier.spi.predicate.ModifierPredicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.ITeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public abstract class MixinEntity implements IModifierImmunity {
   @Unique
   private ModifierPredicate modifierImmunity;

   @Override
   public ModifierPredicate getImmunity() {
      return this.modifierImmunity;
   }

   @Override
   public void setImmunity(ModifierPredicate predicate) {
      this.modifierImmunity = predicate;
   }

   @Shadow
   @Nullable
   public abstract Entity changeDimension(ServerLevel var1, ITeleporter var2);

   @Inject(
      method = {"changeDimension(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/world/entity/Entity;"},
      at = {@At("HEAD")},
      remap = false,
      cancellable = true
   )
   public void changeDimension(ServerLevel destination, ITeleporter teleporter, CallbackInfoReturnable<Entity> ci) {
      if (!destination.getServer().isSameThread()) {
         destination.getServer().execute(() -> this.changeDimension(destination, teleporter));
         ci.setReturnValue(null);
      }
   }

   @Inject(
      method = {"saveWithoutId"},
      at = {@At("RETURN")}
   )
   public void writeNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> ci) {
      Adapters.MODIFIER_PREDICATE.writeNbt(this.modifierImmunity).ifPresent(tag -> nbt.put("modifierImmunity", tag));
   }

   @Inject(
      method = {"load"},
      at = {@At("RETURN")}
   )
   public void readNbt(CompoundTag nbt, CallbackInfo ci) {
      this.modifierImmunity = Adapters.MODIFIER_PREDICATE.readNbt(nbt.get("modifierImmunity")).orElse(null);
      CommonEvents.ENTITY_READ.invoke((Entity)this, nbt);
   }
}
