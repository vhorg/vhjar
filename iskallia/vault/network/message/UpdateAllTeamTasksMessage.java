package iskallia.vault.network.message;

import iskallia.vault.world.data.TeamTaskData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateAllTeamTasksMessage {
   private final Collection<TeamTaskData.TeamTasks> allTeamTasks;
   private final Map<String, String> completedTasks;

   public UpdateAllTeamTasksMessage(Collection<TeamTaskData.TeamTasks> allTeamTasks, Map<String, String> completedTasks) {
      this.allTeamTasks = allTeamTasks;
      this.completedTasks = completedTasks;
   }

   public static void encode(UpdateAllTeamTasksMessage message, FriendlyByteBuf buffer) {
      buffer.writeCollection(message.allTeamTasks, (buf, task) -> task.writeBytes(buf));
      buffer.writeMap(message.completedTasks, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
   }

   public static UpdateAllTeamTasksMessage decode(FriendlyByteBuf buffer) {
      return new UpdateAllTeamTasksMessage(
         buffer.readCollection(ArrayList::new, TeamTaskData.TeamTasks::readBytes), buffer.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf)
      );
   }

   public static void handle(UpdateAllTeamTasksMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         TeamTaskData teamTaskData = TeamTaskData.get();
         teamTaskData.updateAllTeamTasks(message.allTeamTasks);
         teamTaskData.updateCompletedTasks(message.completedTasks);
      });
      context.setPacketHandled(true);
   }
}
