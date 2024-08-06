package iskallia.vault.network.message;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.task.Task;
import iskallia.vault.world.data.AchievementData;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateAchievementDataMessage {
   private final Task task;

   public UpdateAchievementDataMessage(Task task) {
      this.task = task;
   }

   public static void encode(UpdateAchievementDataMessage message, FriendlyByteBuf buffer) {
      Adapters.TASK.writeNbt(message.task).map(tag -> (CompoundTag)tag).ifPresent(buffer::writeNbt);
   }

   public static UpdateAchievementDataMessage decode(FriendlyByteBuf buffer) {
      Task task = Adapters.TASK.readNbt(buffer.readNbt()).orElseThrow();
      return new UpdateAchievementDataMessage(task);
   }

   public static void handle(UpdateAchievementDataMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> AchievementData.CLIENT_ACHIEVEMENTS = message.task);
      context.setPacketHandled(true);
   }
}
