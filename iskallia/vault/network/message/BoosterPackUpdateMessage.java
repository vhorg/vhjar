package iskallia.vault.network.message;

import iskallia.vault.client.gui.screen.BoosterPackSelectionScreen;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record BoosterPackUpdateMessage() {
   public static void encode(BoosterPackUpdateMessage message, FriendlyByteBuf buffer) {
   }

   public static BoosterPackUpdateMessage decode(FriendlyByteBuf buffer) {
      return new BoosterPackUpdateMessage();
   }

   public static void handle(BoosterPackUpdateMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            openBoosterPackSelectionScreen(stack);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void openBoosterPackSelectionScreen(ItemStack stack) {
      Minecraft.getInstance().setScreen(new BoosterPackSelectionScreen(stack));
   }
}
