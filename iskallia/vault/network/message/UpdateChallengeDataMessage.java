package iskallia.vault.network.message;

import iskallia.vault.block.entity.challenge.ChallengeManager;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.world.data.ChallengeData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateChallengeDataMessage {
   private final List<ChallengeManager> managers;

   public UpdateChallengeDataMessage(List<ChallengeManager> managers) {
      this.managers = managers;
   }

   public static void encode(UpdateChallengeDataMessage message, FriendlyByteBuf buffer) {
      buffer.writeCollection(message.managers, (buf, manager) -> buf.writeNbt((CompoundTag)Adapters.CHALLENGE_MANAGER.writeNbt(manager).orElseThrow()));
   }

   public static UpdateChallengeDataMessage decode(FriendlyByteBuf buffer) {
      return new UpdateChallengeDataMessage(
         (List<ChallengeManager>)buffer.readCollection(ArrayList::new, buf -> Adapters.CHALLENGE_MANAGER.readNbt(buf.readNbt()).orElseThrow())
      );
   }

   public static void handle(UpdateChallengeDataMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ChallengeData.CLIENT = message.managers);
      context.setPacketHandled(true);
   }
}
