package iskallia.vault.network.message;

import iskallia.vault.client.ClientVaultRaidData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultOverlayMessage {
   private VaultOverlayMessage.OverlayType type;
   private int remainingTicks;
   private boolean earlyKill;
   private boolean showTimer;

   protected VaultOverlayMessage() {
   }

   protected VaultOverlayMessage(int remainingTicks, boolean earlyKill, VaultOverlayMessage.OverlayType type, boolean showTimer) {
      this.remainingTicks = remainingTicks;
      this.earlyKill = earlyKill;
      this.type = type;
      this.showTimer = showTimer;
   }

   public static VaultOverlayMessage forArena(int ticks) {
      return new VaultOverlayMessage(ticks, false, VaultOverlayMessage.OverlayType.ARENA, true);
   }

   public static VaultOverlayMessage forVault(int ticks, boolean earlyKill, boolean showTimer) {
      return new VaultOverlayMessage(ticks, earlyKill, VaultOverlayMessage.OverlayType.VAULT, showTimer);
   }

   public static VaultOverlayMessage hide() {
      return new VaultOverlayMessage(0, false, VaultOverlayMessage.OverlayType.NONE, true);
   }

   public static void encode(VaultOverlayMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.remainingTicks);
      buffer.writeEnum(message.type);
      buffer.writeBoolean(message.earlyKill);
      buffer.writeBoolean(message.showTimer);
   }

   public static VaultOverlayMessage decode(FriendlyByteBuf buffer) {
      VaultOverlayMessage message = new VaultOverlayMessage();
      message.remainingTicks = buffer.readInt();
      message.type = (VaultOverlayMessage.OverlayType)buffer.readEnum(VaultOverlayMessage.OverlayType.class);
      message.earlyKill = buffer.readBoolean();
      message.showTimer = buffer.readBoolean();
      return message;
   }

   public int getRemainingTicks() {
      return this.remainingTicks;
   }

   public boolean canGetRecordTime() {
      return this.earlyKill;
   }

   public VaultOverlayMessage.OverlayType getOverlayType() {
      return this.type;
   }

   public boolean showTimer() {
      return this.showTimer;
   }

   public static void handle(VaultOverlayMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientVaultRaidData.receiveOverlayUpdate(message));
      context.setPacketHandled(true);
   }

   public static enum OverlayType {
      VAULT,
      ARENA,
      NONE;
   }
}
