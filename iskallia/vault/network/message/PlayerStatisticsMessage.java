package iskallia.vault.network.message;

import iskallia.vault.client.ClientStatisticsData;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class PlayerStatisticsMessage {
   private final CompoundTag statisticsData;

   public PlayerStatisticsMessage(CompoundTag statisticsData) {
      this.statisticsData = statisticsData;
   }

   public static void encode(PlayerStatisticsMessage message, FriendlyByteBuf buffer) {
      buffer.writeNbt(message.statisticsData);
   }

   public static PlayerStatisticsMessage decode(FriendlyByteBuf buffer) {
      return new PlayerStatisticsMessage(buffer.readNbt());
   }

   public static void handle(PlayerStatisticsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientStatisticsData.receiveUpdate(message.statisticsData));
      context.setPacketHandled(true);
   }
}
