package iskallia.vault.container.oversized;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.SyncOverSizedContentMessage;
import iskallia.vault.network.message.SyncOverSizedStackMessage;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;

public class OverSizedContainerSynchronizer implements ContainerSynchronizer {
   private final ContainerSynchronizer decorated;
   private final ServerPlayer sPlayer;

   public OverSizedContainerSynchronizer(ContainerSynchronizer decorated, ServerPlayer sPlayer) {
      this.decorated = decorated;
      this.sPlayer = sPlayer;
   }

   public void sendInitialData(AbstractContainerMenu container, NonNullList<ItemStack> remoteSlots, ItemStack remoteCarried, int[] remoteDataSlots) {
      ModNetwork.CHANNEL
         .sendTo(
            new SyncOverSizedContentMessage(container.containerId, container.incrementStateId(), remoteSlots, remoteCarried),
            this.sPlayer.connection.connection,
            NetworkDirection.PLAY_TO_CLIENT
         );
   }

   public void sendSlotChange(AbstractContainerMenu container, int slotId, ItemStack remoteStack) {
      ModNetwork.CHANNEL
         .sendTo(
            new SyncOverSizedStackMessage(container.containerId, container.incrementStateId(), slotId, remoteStack),
            this.sPlayer.connection.connection,
            NetworkDirection.PLAY_TO_CLIENT
         );
   }

   public void sendCarriedChange(AbstractContainerMenu container, ItemStack remoteCarried) {
      this.decorated.sendCarriedChange(container, remoteCarried);
   }

   public void sendDataChange(AbstractContainerMenu container, int slotId, int data) {
      this.decorated.sendDataChange(container, slotId, data);
   }
}
