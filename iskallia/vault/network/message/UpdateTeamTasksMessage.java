package iskallia.vault.network.message;

import iskallia.vault.world.data.TeamTaskData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateTeamTasksMessage {
   private final TeamTaskData.TeamTasks teamTasks;

   public UpdateTeamTasksMessage(TeamTaskData.TeamTasks teamTasks) {
      this.teamTasks = teamTasks;
   }

   public static void encode(UpdateTeamTasksMessage message, FriendlyByteBuf buffer) {
      message.teamTasks.writeBytes(buffer);
   }

   public static UpdateTeamTasksMessage decode(FriendlyByteBuf buffer) {
      return new UpdateTeamTasksMessage(TeamTaskData.TeamTasks.readBytes(buffer));
   }

   public static void handle(UpdateTeamTasksMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> TeamTaskData.get().updateTeamTasks(message.teamTasks));
      context.setPacketHandled(true);
   }
}
