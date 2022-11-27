package iskallia.vault.mixin;

import iskallia.vault.block.property.HiddenIntegerProperty;
import iskallia.vault.world.data.ServerVaults;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DebugScreenOverlay.class})
public class MixinDebugOverlayGui {
   @Inject(
      method = {"getPropertyValueString"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void hidePropertyString(Entry<Property<?>, Comparable<?>> entryIn, CallbackInfoReturnable<String> cir) {
      if (entryIn.getKey() instanceof HiddenIntegerProperty) {
         cir.setReturnValue(entryIn.getKey().getName() + ": unknown");
      }
   }

   @Inject(
      method = {"getGameInformation"},
      at = {@At("RETURN")}
   )
   public void hideCoordinateInformation(CallbackInfoReturnable<List<String>> cir) {
      Player player = Minecraft.getInstance().player;
      if (player != null && ServerVaults.isInVault(player)) {
         List<String> information = (List<String>)cir.getReturnValue();
         information.removeIf(str -> str.startsWith("XYZ: ") || str.startsWith("Block: ") || str.startsWith("Chunk: "));
      }
   }

   @Inject(
      method = {"getSystemInformation"},
      at = {@At("RETURN")}
   )
   public void hideCoordinateBlockInformation(CallbackInfoReturnable<List<String>> cir) {
      Player player = Minecraft.getInstance().player;
      if (player != null && ServerVaults.isInVault(player)) {
         List<String> information = (List<String>)cir.getReturnValue();

         for (int i = 0; i < information.size(); i++) {
            String text = information.get(i);
            if (text.contains("Targeted Block")) {
               information.set(i, ChatFormatting.UNDERLINE + "Targeted Block:");
            }

            if (text.contains("Targeted Fluid")) {
               information.set(i, ChatFormatting.UNDERLINE + "Targeted Fluid:");
            }
         }
      }
   }
}
