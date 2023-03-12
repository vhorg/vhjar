package iskallia.vault.network.message;

import iskallia.vault.container.VaultEndContainer;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundOpenVaultExitMessage {
   private VaultSnapshot snapshot;

   public ServerboundOpenVaultExitMessage(VaultSnapshot snapshot) {
      this.snapshot = snapshot;
   }

   public static void encode(ServerboundOpenVaultExitMessage message, FriendlyByteBuf buffer) {
      ArrayBitBuffer buffer2 = ArrayBitBuffer.empty();
      message.snapshot.writeBits(buffer2);
      buffer.writeLongArray(buffer2.toLongArray());
   }

   public static ServerboundOpenVaultExitMessage decode(FriendlyByteBuf buffer) {
      ArrayBitBuffer buffer2 = ArrayBitBuffer.backing(buffer.readLongArray(), 0);
      return new ServerboundOpenVaultExitMessage(new VaultSnapshot(buffer2));
   }

   public static void handle(ServerboundOpenVaultExitMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            ArrayBitBuffer buf = ArrayBitBuffer.empty();
            message.snapshot.writeBits(buf);
            NetworkHooks.openGui(sender, new MenuProvider() {
               @Nonnull
               public Component getDisplayName() {
                  return new TranslatableComponent("Vault Exit");
               }

               @ParametersAreNonnullByDefault
               public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                  return new VaultEndContainer(i, playerInventory, message.snapshot);
               }
            }, friendlyByteBuf -> friendlyByteBuf.writeLongArray(buf.toLongArray()));
         }
      });
      context.setPacketHandled(true);
   }
}
