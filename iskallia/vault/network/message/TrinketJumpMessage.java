package iskallia.vault.network.message;

import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.MultiJumpTrinket;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class TrinketJumpMessage {
   public static final TrinketJumpMessage INSTANCE = new TrinketJumpMessage();

   private TrinketJumpMessage() {
   }

   public static TrinketJumpMessage getInstance() {
      return INSTANCE;
   }

   public static void encode(TrinketJumpMessage message, FriendlyByteBuf buffer) {
   }

   public static TrinketJumpMessage decode(FriendlyByteBuf buffer) {
      return getInstance();
   }

   public static void handle(TrinketJumpMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            if (!TrinketHelper.getTrinkets(sender, MultiJumpTrinket.class).stream().noneMatch(trinket -> trinket.isUsable(sender))) {
               sender.fallDistance = 0.0F;
               sender.causeFoodExhaustion(0.2F);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
