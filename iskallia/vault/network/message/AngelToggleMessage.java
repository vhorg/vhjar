package iskallia.vault.network.message;

import iskallia.vault.skill.expertise.type.AngelExpertise;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class AngelToggleMessage {
   public static final AngelToggleMessage INSTANCE = new AngelToggleMessage();

   public static void encode(AngelToggleMessage pkt, FriendlyByteBuf buffer) {
   }

   public static AngelToggleMessage decode(FriendlyByteBuf buffer) {
      return new AngelToggleMessage();
   }

   public static void handle(AngelToggleMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            AngelExpertise.toggleAngel(sender);
         }
      });
      context.setPacketHandled(true);
   }
}
