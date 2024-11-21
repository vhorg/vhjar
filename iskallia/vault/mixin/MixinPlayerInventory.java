package iskallia.vault.mixin;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.item.AntiqueStampCollectorBook;
import iskallia.vault.item.ItemShardPouch;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.data.InventorySnapshotData;
import iskallia.vault.world.data.ServerVaults;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Inventory.class})
public abstract class MixinPlayerInventory implements InventorySnapshotData.InventoryAccessor {
   @Shadow
   @Final
   public Player player;
   @Shadow
   @Final
   private List<NonNullList<ItemStack>> compartments;

   @Redirect(
      method = {"hurtArmor"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"
      )
   )
   public void hurtItemStack(ItemStack stack, int amount, LivingEntity entity, Consumer<LivingEntity> onBroken) {
      if (!(stack.getItem() instanceof VaultGearItem gear && gear.isBroken(stack))) {
         if (ServerVaults.get(this.player.level).isPresent()) {
            amount = Math.min(amount, 5);
         } else if (stack.getItem() instanceof VaultGearItem) {
            return;
         }

         if (this.player.getCommandSenderWorld() instanceof ServerLevel) {
            amount = (int)CommonEvents.PLAYER_STAT.invoke(PlayerStat.DURABILITY_DAMAGE, this.player, amount).getValue();
         }

         stack.hurtAndBreak(amount, entity, onBroken);
         if (stack.getItem() instanceof VaultGearItem gearx && gearx.isBroken(stack) && this.player instanceof ServerPlayer serverPlayer) {
            AttributeSnapshotHelper.getInstance().refreshSnapshot(serverPlayer);
         }
      }
   }

   @Inject(
      method = {"add(Lnet/minecraft/world/item/ItemStack;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void interceptItemAddition(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      Inventory thisInventory = (Inventory)this;
      if (ItemShardPouch.interceptPlayerInventoryItemAddition(thisInventory, stack)) {
         cir.setReturnValue(true);
      } else {
         if (AntiqueStampCollectorBook.interceptPlayerInventoryItemAddition(thisInventory, stack)) {
            cir.setReturnValue(true);
         }
      }
   }

   @Override
   public int getSize() {
      return this.compartments.stream().mapToInt(NonNullList::size).sum();
   }
}
