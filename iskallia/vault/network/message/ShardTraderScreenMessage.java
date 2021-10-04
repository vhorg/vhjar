package iskallia.vault.network.message;

import iskallia.vault.Vault;
import iskallia.vault.world.data.SoulShardTraderData;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ShardTraderScreenMessage {
   public static void encode(ShardTraderScreenMessage message, PacketBuffer buffer) {
   }

   public static ShardTraderScreenMessage decode(PacketBuffer buffer) {
      return new ShardTraderScreenMessage();
   }

   public static void handle(ShardTraderScreenMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerWorld sWorld = context.getSender().func_71121_q();
         RegistryKey<World> dimKey = sWorld.func_234923_W_();
         if (dimKey != Vault.VAULT_KEY) {
            SoulShardTraderData.get(sWorld).openTradeContainer(context.getSender());
         }
      });
      context.setPacketHandled(true);
   }
}
