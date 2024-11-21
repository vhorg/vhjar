package iskallia.vault.network.message;

import iskallia.vault.client.gui.screen.BoosterPackSelectionScreen;
import iskallia.vault.client.gui.screen.JewelPouchSelectionScreen;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class OpenClientScreenMessage {
   private final OpenClientScreenMessage.Type type;

   public OpenClientScreenMessage(OpenClientScreenMessage.Type type) {
      this.type = type;
   }

   public static void encode(OpenClientScreenMessage message, FriendlyByteBuf buffer) {
      buffer.writeEnum(message.type);
   }

   public static OpenClientScreenMessage decode(FriendlyByteBuf buffer) {
      return new OpenClientScreenMessage((OpenClientScreenMessage.Type)buffer.readEnum(OpenClientScreenMessage.Type.class));
   }

   public static void handle(OpenClientScreenMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> openClient(message.type));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void openClient(OpenClientScreenMessage.Type type) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
         if (!stack.isEmpty()) {
            switch (type) {
               case BOOSTER_PACK:
                  Minecraft.getInstance().setScreen(new BoosterPackSelectionScreen(stack));
                  break;
               case JEWEL_POUCH:
                  Minecraft.getInstance().setScreen(new JewelPouchSelectionScreen(stack));
            }
         }
      }
   }

   public static enum Type {
      BOOSTER_PACK,
      JEWEL_POUCH;
   }
}
