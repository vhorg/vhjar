package iskallia.vault.network.message;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.WorldSettings;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
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
               if (FMLEnvironment.dist == Dist.DEDICATED_SERVER && !sender.hasPermissions(2)) {
                  sender.sendMessage(new TextComponent("You do not have permission to change server difficulty.").withStyle(ChatFormatting.RED), Util.NIL_UUID);
                  return;
               }

               WorldSettings worldSettings = WorldSettings.get(sender.getLevel());
               worldSettings.setGlobalVaultDifficulty(message.vaultDifficulty);
               worldSettings.setVaultDifficultyLocked(message.vaultDifficultyLocked);
               ModNetwork.CHANNEL
                  .send(
                     PacketDistributor.ALL.noArg(),
                     new ClientboundUpdateDifficultyMessage(worldSettings.getGlobalVaultDifficulty(), worldSettings.isVaultDifficultyLocked())
                  );
            }
         }
      );
      context.setPacketHandled(true);
   }
}
