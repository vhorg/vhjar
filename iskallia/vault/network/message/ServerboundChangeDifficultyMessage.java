package iskallia.vault.network.message;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.WorldSettings;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundChangeDifficultyMessage {
   private final VaultDifficulty vaultDifficulty;
   private final boolean vaultDifficultyLocked;

   public ServerboundChangeDifficultyMessage(VaultDifficulty vaultDifficulty, boolean vaultDifficultyLocked) {
      this.vaultDifficulty = vaultDifficulty;
      this.vaultDifficultyLocked = vaultDifficultyLocked;
   }

   public static void encode(ServerboundChangeDifficultyMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.vaultDifficulty.getId());
      buffer.writeBoolean(message.vaultDifficultyLocked);
   }

   public static ServerboundChangeDifficultyMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundChangeDifficultyMessage(VaultDifficulty.byId(buffer.readInt()), buffer.readBoolean());
   }

   public static void handle(ServerboundChangeDifficultyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
               WorldSettings worldSettings = WorldSettings.get(sender.getLevel());
               worldSettings.setVaultDifficulty(message.vaultDifficulty);
               worldSettings.setVaultDifficultyLocked(message.vaultDifficultyLocked);
               ModNetwork.CHANNEL
                  .send(
                     PacketDistributor.ALL.noArg(),
                     new ClientboundUpdateDifficultyMessage(worldSettings.getVaultDifficulty(), worldSettings.isVaultDifficultyLocked())
                  );
            }
         }
      );
      context.setPacketHandled(true);
   }
}
