package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.VaultRaidOverlay;
import iskallia.vault.world.raid.VaultRaid;
import iskallia.vault.world.raid.modifier.VaultModifiers;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class VaultInfoMessage {
   private int rarity;
   private VaultModifiers modifiers;

   public VaultInfoMessage() {
   }

   public VaultInfoMessage(VaultRaid raid) {
      this.rarity = raid.rarity;
      this.modifiers = raid.modifiers;
   }

   public static void encode(VaultInfoMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.rarity);
      message.modifiers.encode(buffer);
   }

   public static VaultInfoMessage decode(PacketBuffer buffer) {
      VaultInfoMessage message = new VaultInfoMessage();
      message.rarity = buffer.readInt();
      message.modifiers = VaultModifiers.decode(buffer);
      return message;
   }

   public static void handle(VaultInfoMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         VaultRaidOverlay.currentRarity = message.rarity;
         VaultModifiers.CLIENT = message.modifiers;
      });
      context.setPacketHandled(true);
   }
}
