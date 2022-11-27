package iskallia.vault.network.message;

import iskallia.vault.init.ModGameRules;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundSyncVaultAllowWaypointsMessage {
   private final boolean vaultAllowWaypoints;

   public ClientboundSyncVaultAllowWaypointsMessage(boolean vaultAllowWaypoints) {
      this.vaultAllowWaypoints = vaultAllowWaypoints;
   }

   public static void encode(ClientboundSyncVaultAllowWaypointsMessage message, FriendlyByteBuf buffer) {
      buffer.writeBoolean(message.vaultAllowWaypoints);
   }

   public static ClientboundSyncVaultAllowWaypointsMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundSyncVaultAllowWaypointsMessage(buffer.readBoolean());
   }

   public static void handle(ClientboundSyncVaultAllowWaypointsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> handle(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void handle(ClientboundSyncVaultAllowWaypointsMessage message) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player != null) {
         ((BooleanValue)player.getLevel().getGameRules().getRule(ModGameRules.VAULT_ALLOW_WAYPOINTS)).set(message.vaultAllowWaypoints, null);
      }
   }
}
