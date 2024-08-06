package iskallia.vault.network.message;

import iskallia.vault.container.inventory.CardDeckContainer;
import iskallia.vault.container.inventory.CardDeckContainerMenu;
import iskallia.vault.core.vault.VaultUtils;
import iskallia.vault.item.CardDeckItem;
import java.util.function.Supplier;
import mekanism.common.integration.curios.CuriosIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkEvent.Context;

public class OpenCardDeckMessage {
   public static final OpenCardDeckMessage INSTANCE = new OpenCardDeckMessage();

   public static void encode(OpenCardDeckMessage pkt, FriendlyByteBuf buffer) {
   }

   public static OpenCardDeckMessage decode(FriendlyByteBuf buffer) {
      return new OpenCardDeckMessage();
   }

   public static void handle(OpenCardDeckMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
               if (VaultUtils.isVaultLevel(sender.getLevel())) {
                  sender.sendMessage(new TextComponent("You can't open the card deck in a Vault!").withStyle(ChatFormatting.RED), sender.getUUID());
               } else {
                  ItemStack deckStack = CuriosIntegration.getCurioStack(sender, "deck", 0);
                  if (deckStack.getItem() instanceof CardDeckItem) {
                     NetworkHooks.openGui(
                        sender,
                        new SimpleMenuProvider(
                           (windowId, inventory, pl) -> new CardDeckContainerMenu(windowId, inventory, 0, true, new CardDeckContainer(deckStack)),
                           new TextComponent("Card Deck")
                        ),
                        buf -> {
                           buf.writeItem(deckStack);
                           buf.writeInt(0);
                           buf.writeBoolean(true);
                        }
                     );
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
