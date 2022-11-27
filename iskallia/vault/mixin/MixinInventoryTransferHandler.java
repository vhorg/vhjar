package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.handler.InventoryTransferHandler;

@Mixin({InventoryTransferHandler.class})
public class MixinInventoryTransferHandler {
   @Inject(
      remap = false,
      method = {"transfer"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onTransfer(Player player, boolean isRestock, boolean smart, CallbackInfo ci) {
      Level level = player.getLevel();
      if (ServerVaults.isVaultWorld(level.dimension())) {
         ci.cancel();
      }
   }
}
