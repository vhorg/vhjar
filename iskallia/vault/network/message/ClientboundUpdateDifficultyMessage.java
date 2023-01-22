package iskallia.vault.network.message;

import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.WorldSettings;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundUpdateDifficultyMessage {
   private final VaultDifficulty vaultDifficulty;
   private final boolean vaultDifficultyLocked;

   public ClientboundUpdateDifficultyMessage(VaultDifficulty vaultDifficulty, boolean vaultDifficultyLocked) {
      this.vaultDifficulty = vaultDifficulty;
      this.vaultDifficultyLocked = vaultDifficultyLocked;
   }

   public static void encode(ClientboundUpdateDifficultyMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.vaultDifficulty.getId());
      buffer.writeBoolean(message.vaultDifficultyLocked);
   }

   public static ClientboundUpdateDifficultyMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundUpdateDifficultyMessage(VaultDifficulty.byId(buffer.readInt()), buffer.readBoolean());
   }

   public static void handle(ClientboundUpdateDifficultyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> updateDifficulty(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void updateDifficulty(ClientboundUpdateDifficultyMessage message) {
      WorldSettings worldSettings = WorldSettings.get(Minecraft.getInstance().level);
      worldSettings.setGlobalVaultDifficulty(message.vaultDifficulty);
      worldSettings.setVaultDifficultyLocked(message.vaultDifficultyLocked);
   }
}
