package iskallia.vault.network.message;

import iskallia.vault.client.gui.screen.summary.VaultHistoricDataScreen;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultPlayerHistoricDataMessage {
   public static class S2C {
      private List<VaultSnapshot> snapshots;

      public S2C(List<VaultSnapshot> snapshots) {
         this.snapshots = snapshots;
      }

      public static void encode(VaultPlayerHistoricDataMessage.S2C message, FriendlyByteBuf buffer) {
         int size = message.snapshots.size();
         buffer.writeInt(size);

         for (int i = 0; i < size; i++) {
            ArrayBitBuffer buffer2 = ArrayBitBuffer.empty();
            message.snapshots.get(i).writeBits(buffer2);
            buffer.writeLongArray(buffer2.toLongArray());
         }
      }

      public static VaultPlayerHistoricDataMessage.S2C decode(FriendlyByteBuf buffer) {
         List<VaultSnapshot> snapshots = new ArrayList<>();
         int size = buffer.readInt();

         for (int i = 0; i < size; i++) {
            ArrayBitBuffer buffer2 = ArrayBitBuffer.backing(buffer.readLongArray(), 0);
            snapshots.add(new VaultSnapshot(buffer2));
         }

         return new VaultPlayerHistoricDataMessage.S2C(snapshots);
      }

      public static void handle(VaultPlayerHistoricDataMessage.S2C message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         context.enqueueWork(() -> openScreen(message));
         context.setPacketHandled(true);
      }

      @OnlyIn(Dist.CLIENT)
      private static void openScreen(VaultPlayerHistoricDataMessage.S2C message) {
         Minecraft.getInstance().setScreen(new VaultHistoricDataScreen(message.snapshots, new TextComponent("Historic Data")));
      }
   }
}
