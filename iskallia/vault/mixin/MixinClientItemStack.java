package iskallia.vault.mixin;

import iskallia.vault.init.ModItems;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
public abstract class MixinClientItemStack {
   @Shadow
   public abstract Item getItem();

   @Inject(
      method = {"getHoverName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getHoverName(CallbackInfoReturnable<Component> ci) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         if (this.getItem() == ModItems.SPICY_HEARTY_BURGER) {
            if (!player.getUUID().equals(UUID.fromString("5f820c39-5883-4392-b174-3125ac05e38c"))) {
               return;
            }

            ci.setReturnValue(new TextComponent("Spicy Farty Burger"));
         } else if (this.getItem() == ModItems.BITTER_LEMON) {
            if (!player.getUUID().equals(UUID.fromString("7ac3c39f-23d5-472a-a7c9-24798265fa15"))) {
               return;
            }

            ci.setReturnValue(new TextComponent("Bitter Melon"));
         }
      }
   }
}
