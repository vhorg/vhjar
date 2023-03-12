package iskallia.vault.network.message;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.StreamData;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class RaffleServerMessage {
   public RaffleServerMessage.Opcode opcode;
   public CompoundTag payload;

   public static void encode(RaffleServerMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.opcode.ordinal());
      buffer.writeNbt(message.payload);
   }

   public static RaffleServerMessage decode(FriendlyByteBuf buffer) {
      RaffleServerMessage message = new RaffleServerMessage();
      message.opcode = RaffleServerMessage.Opcode.values()[buffer.readInt()];
      message.payload = buffer.readNbt();
      return message;
   }

   public static void handle(RaffleServerMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (message.opcode == RaffleServerMessage.Opcode.REQUEST_RAFFLE) {
            ServerPlayer sender = context.getSender();
            if (sender == null) {
               return;
            }

            WeightedList<String> occupants = getOccupants(sender);
            if (occupants.size() < 5) {
               TextComponent text = new TextComponent("Not enough people for Raffle");
               text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(-26266)));
               sender.displayClientMessage(text, true);
            } else {
               String winner = occupants.getRandom(sender.level.random);
               ModNetwork.CHANNEL.sendTo(RaffleClientMessage.openUI(occupants, winner), sender.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
         } else if (message.opcode == RaffleServerMessage.Opcode.DONE_ANIMATING) {
            ServerPlayer senderx = context.getSender();
            if (senderx != null) {
               String winner = message.payload.getString("Winner");
               PlayerStatsData.get().onRaffleCompleted(senderx.getUUID(), getOccupants(senderx), winner);
               StreamData streamData = StreamData.get(senderx.getLevel());
               streamData.resetDonos(senderx.getServer(), senderx.getUUID());
               dropRewards(senderx, winner);
            }
         }
      });
      context.setPacketHandled(true);
   }

   public static WeightedList<String> getOccupants(ServerPlayer sender) {
      StreamData streamData = StreamData.get(sender.getLevel());
      StreamData.Donations donations = streamData.getDonations(sender.getUUID());
      return donations.toWeightedList();
   }

   public static void dropRewards(ServerPlayer sender, String winner) {
   }

   public static RaffleServerMessage requestRaffle() {
      RaffleServerMessage message = new RaffleServerMessage();
      message.opcode = RaffleServerMessage.Opcode.REQUEST_RAFFLE;
      message.payload = new CompoundTag();
      return message;
   }

   public static RaffleServerMessage animationDone(String winner) {
      RaffleServerMessage message = new RaffleServerMessage();
      message.opcode = RaffleServerMessage.Opcode.DONE_ANIMATING;
      message.payload = new CompoundTag();
      message.payload.putString("Winner", winner);
      return message;
   }

   public static enum Opcode {
      REQUEST_RAFFLE,
      DONE_ANIMATING;
   }
}
