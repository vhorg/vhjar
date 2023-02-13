package iskallia.vault.mixin;

import iskallia.vault.item.LegacyMagnetItem;
import iskallia.vault.item.MagnetItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemEntity.class})
public abstract class ItemEntityMixin extends Entity {
   public ItemEntityMixin(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @Inject(
      method = {"playerTouch"},
      at = {@At(
         value = "RETURN",
         ordinal = 1
      )}
   )
   public void onPickupEventCancelled(Player entity, CallbackInfo ci) {
      LegacyMagnetItem.onAfterItemPickup(entity, (ItemEntity)this);
   }

   @Inject(
      method = {"playerTouch"},
      at = {@At(
         value = "RETURN",
         ordinal = 2
      )}
   )
   public void onPartialStackPickup(Player entity, CallbackInfo ci) {
      LegacyMagnetItem.onAfterItemPickup(entity, (ItemEntity)this);
   }

   @Inject(
      method = {"playerTouch"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;onItemPickup(Lnet/minecraft/world/entity/item/ItemEntity;)V"
      )}
   )
   public void onPickup(Player entity, CallbackInfo ci) {
      MagnetItem.onPlayerPickup(entity, (ItemEntity)this);
   }
}
