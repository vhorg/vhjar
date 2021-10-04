package iskallia.vault.network.message;

import iskallia.vault.client.ClientVaultRaidData;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class VaultOverlayMessage {
   private VaultOverlayMessage.OverlayType type;
   private int remainingTicks;
   private boolean earlyKill;

   protected VaultOverlayMessage() {
   }

   protected VaultOverlayMessage(int remainingTicks, boolean earlyKill, VaultOverlayMessage.OverlayType type) {
      this.remainingTicks = remainingTicks;
      this.earlyKill = earlyKill;
      this.type = type;
   }

   public static VaultOverlayMessage forArena(int ticks) {
      return new VaultOverlayMessage(ticks, false, VaultOverlayMessage.OverlayType.ARENA);
   }

   public static VaultOverlayMessage forVault(int ticks, boolean earlyKill) {
      return new VaultOverlayMessage(ticks, earlyKill, VaultOverlayMessage.OverlayType.VAULT);
   }

   public static VaultOverlayMessage hide() {
      return new VaultOverlayMessage(0, false, VaultOverlayMessage.OverlayType.NONE);
   }

   public static void encode(VaultOverlayMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.remainingTicks);
      buffer.func_179249_a(message.type);
      buffer.writeBoolean(message.earlyKill);
   }

   public static VaultOverlayMessage decode(PacketBuffer buffer) {
      VaultOverlayMessage message = new VaultOverlayMessage();
      message.remainingTicks = buffer.readInt();
      message.type = (VaultOverlayMessage.OverlayType)buffer.func_179257_a(VaultOverlayMessage.OverlayType.class);
      message.earlyKill = buffer.readBoolean();
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
