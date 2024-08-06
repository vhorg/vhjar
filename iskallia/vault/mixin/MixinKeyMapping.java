package iskallia.vault.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.BingoObjective;
import iskallia.vault.init.ModKeybinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({KeyMapping.class})
public class MixinKeyMapping {
   @Shadow
   private Key key;

   @Inject(
      method = {"isDown"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void isDown(CallbackInfoReturnable<Boolean> ci) {
      boolean down = (Boolean)ci.getReturnValue();
      Minecraft minecraft = Minecraft.getInstance();
      boolean hasBingo = ClientVaults.getActive().map(vault -> !vault.get(Vault.OBJECTIVES).getAll(BingoObjective.class).isEmpty()).orElse(false);
      if (hasBingo && this == minecraft.options.keyPlayerList && this.key.getValue() == ModKeybinds.openBingo.getKey().getValue()) {
         boolean shiftDown = InputConstants.isKeyDown(minecraft.getWindow().getWindow(), 340)
            || InputConstants.isKeyDown(minecraft.getWindow().getWindow(), 344);
         ci.setReturnValue(down && shiftDown);
      }
   }
}
