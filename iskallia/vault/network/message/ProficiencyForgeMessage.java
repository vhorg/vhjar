package iskallia.vault.network.message;

import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.container.VaultForgeContainer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ProficiencyForgeMessage {
   public static final ProficiencyForgeMessage INSTANCE = new ProficiencyForgeMessage();

   private ProficiencyForgeMessage() {
   }

   public static void encode(ProficiencyForgeMessage message, FriendlyByteBuf buffer) {
   }

   public static ProficiencyForgeMessage decode(FriendlyByteBuf buffer) {
      return INSTANCE;
   }

   public static void handle(ProficiencyForgeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            if (sender.containerMenu instanceof VaultForgeContainer forgeContainer) {
               VaultForgeTileEntity tile = forgeContainer.getTile();
               if (tile != null) {
                  tile.increaseProficiency(sender);
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
