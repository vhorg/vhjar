package iskallia.vault.network.message;

import iskallia.vault.client.ClientStatisticsData;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PlayerStatisticsMessage {
   private final CompoundNBT statisticsData;

   public PlayerStatisticsMessage(CompoundNBT statisticsData) {
      this.statisticsData = statisticsData;
   }

   public static void encode(PlayerStatisticsMessage message, PacketBuffer buffer) {
      buffer.func_150786_a(message.statisticsData);
   }

   public static PlayerStatisticsMessage decode(PacketBuffer buffer) {
      return new PlayerStatisticsMessage(buffer.func_150793_b());
   }

   public static void handle(PlayerStatisticsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientStatisticsData.receiveUpdate(message.statisticsData));
      context.setPacketHandled(true);
   }
}
