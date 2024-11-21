package iskallia.vault.network.message;

import iskallia.vault.world.data.TeamTaskData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class RemoveTeamTasksMessage {
   private final String teamName;

   public RemoveTeamTasksMessage(String teamName) {
      this.teamName = teamName;
   }

   public static void encode(RemoveTeamTasksMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.teamName);
   }

   public static RemoveTeamTasksMessage decode(FriendlyByteBuf buffer) {
      return new RemoveTeamTasksMessage(buffer.readUtf());
   }

   public static void handle(RemoveTeamTasksMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> TeamTaskData.get().removeTeamTasks(message.teamName));
      context.setPacketHandled(true);
   }
}
