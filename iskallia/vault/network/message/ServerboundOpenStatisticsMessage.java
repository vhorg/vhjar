package iskallia.vault.network.message;

import iskallia.vault.container.StatisticsTabContainer;
import iskallia.vault.core.vault.stat.StatTotals;
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

public class ServerboundOpenStatisticsMessage {
   public static final ServerboundOpenStatisticsMessage INSTANCE = new ServerboundOpenStatisticsMessage();

   public static void encode(ServerboundOpenStatisticsMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundOpenStatisticsMessage decode(FriendlyByteBuf buffer) {
      return INSTANCE;
   }

   public static void handle(ServerboundOpenStatisticsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            final StatTotals statTotals = StatTotals.of(sender.getUUID());
            NetworkHooks.openGui(sender, new MenuProvider() {
               @Nonnull
               public Component getDisplayName() {
                  return new TranslatableComponent("container.vault.skills");
               }

               @ParametersAreNonnullByDefault
               public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                  return new StatisticsTabContainer(i, playerInventory, statTotals);
               }
            }, buffer -> buffer.writeNbt(statTotals.serializeNBT()));
         }
      });
      context.setPacketHandled(true);
   }
}
