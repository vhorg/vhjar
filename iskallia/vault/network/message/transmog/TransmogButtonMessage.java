package iskallia.vault.network.message.transmog;

import iskallia.vault.block.TransmogTableBlock;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.world.data.DiscoveredModelsData;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public record TransmogButtonMessage() {
   private static final TransmogButtonMessage instance = new TransmogButtonMessage();

   public static void encode(TransmogButtonMessage message, FriendlyByteBuf buffer) {
   }

   public static TransmogButtonMessage decode(FriendlyByteBuf buffer) {
      return instance;
   }

   public static void handle(TransmogButtonMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer sender = context.getSender();
            if (sender.containerMenu instanceof TransmogTableContainer container
               && sender != null
               && !container.getPreviewItemStack().isEmpty()
               && container.priceFulfilled()) {
               Slot gearSlot = container.getSlot(container.getInternalInventoryIndexRange().getContainerIndex(0));
               Slot bronzeSlot = container.getSlot(container.getInternalInventoryIndexRange().getContainerIndex(1));
               Slot outputSlot = container.getSlot(
                  container.getInternalInventoryIndexRange().getContainerIndex(container.getInternalInventory().outputSlotIndex())
               );
               int copperCost = container.copperCost();
               DiscoveredModelsData discoveredModelsData = DiscoveredModelsData.get((ServerLevel)sender.level);
               Set<ResourceLocation> discoveredModels = discoveredModelsData.getDiscoveredModels(sender.getUUID());
               if (!TransmogTableBlock.canTransmogModel(sender, discoveredModels, container.getSelectedModelId())) {
                  return;
               }

               ItemStack resultingStack = gearSlot.getItem().copy();
               VaultGearData gearData = VaultGearData.read(resultingStack);
               gearData.createOrReplaceAttributeValue(ModGearAttributes.GEAR_MODEL, container.getSelectedModelId());
               gearData.write(resultingStack);
               gearSlot.set(ItemStack.EMPTY);
               ItemStack bronze = bronzeSlot.getItem().copy();
               bronze.shrink(copperCost);
               container.getInternalInventory().setItem(1, bronze);
               outputSlot.set(resultingStack);
               container.broadcastChanges();
            }
         }
      );
      context.setPacketHandled(true);
   }
}
