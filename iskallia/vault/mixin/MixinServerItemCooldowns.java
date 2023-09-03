package iskallia.vault.mixin;

import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ServerItemCooldowns;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerItemCooldowns.class})
public class MixinServerItemCooldowns {
   @Shadow
   @Final
   private ServerPlayer player;

   @Inject(
      method = {"onCooldownStarted"},
      at = {@At("TAIL")}
   )
   public void onStarted(Item item, int tick, CallbackInfo ci) {
      if (item instanceof VaultGearItem) {
         AttributeSnapshotHelper.getInstance().refreshSnapshot(this.player);
      }
   }

   @Inject(
      method = {"onCooldownEnded"},
      at = {@At("TAIL")}
   )
   public void onEnd(Item item, CallbackInfo ci) {
      if (item instanceof VaultGearItem) {
         AttributeSnapshotHelper.getInstance().refreshSnapshot(this.player);
      }
   }
}
