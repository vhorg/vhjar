package iskallia.vault.mixin;

import iskallia.vault.item.LegacyMagnetItem;
import iskallia.vault.item.MagnetItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemEntity.class})
public abstract class ItemEntityMixin extends Entity {
   private ItemStack previousStack;

   @Shadow
   public abstract ItemStack getItem();

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
      at = {@At("HEAD")}
   )
   public void onPickupHead(Player entity, CallbackInfo ci) {
      this.previousStack = this.getItem().copy();
   }

   @Inject(
      method = {"playerTouch"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;onItemPickup(Lnet/minecraft/world/entity/item/ItemEntity;)V"
      )}
   )
   public void onPickupVanilla(Player entity, CallbackInfo ci) {
      MagnetItem.onPlayerPickup(entity, (ItemEntity)this);
   }

   @Inject(
      method = {"playerTouch"},
      at = {@At(
         value = "RETURN",
         ordinal = 2
      )}
   )
   public void onPickupModded(Player player, CallbackInfo ci) {
      if (!ItemStack.matches(this.getItem(), this.previousStack)) {
         MagnetItem.onPlayerPickup(player, (ItemEntity)this);
      }
   }

   @Inject(
      method = {"playerTouch"},
      at = {@At(
         value = "RETURN",
         ordinal = 1
      )}
   )
   public void onPickupCanceled(Player player, CallbackInfo ci) {
      if (!ItemStack.matches(this.getItem(), this.previousStack)) {
         MagnetItem.onPlayerPickup(player, (ItemEntity)this);
         this.previousStack = this.getItem().copy();
      }
   }
}
