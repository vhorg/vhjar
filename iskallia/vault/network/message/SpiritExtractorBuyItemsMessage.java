package iskallia.vault.network.message;

import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent.Context;

public class SpiritExtractorBuyItemsMessage {
   private BlockPos extractorPos;

   public SpiritExtractorBuyItemsMessage(BlockPos extractorPos) {
      this.extractorPos = extractorPos;
   }

   public static void encode(SpiritExtractorBuyItemsMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.extractorPos);
   }

   public static SpiritExtractorBuyItemsMessage decode(FriendlyByteBuf buffer) {
      return new SpiritExtractorBuyItemsMessage(buffer.readBlockPos());
   }

   public static void handle(SpiritExtractorBuyItemsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerLevel serverWorld = context.getSender().getLevel();
         if (serverWorld.getBlockEntity(message.extractorPos) instanceof SpiritExtractorTileEntity spiritExtractor) {
            spiritExtractor.spewItems();
         }
      });
      context.setPacketHandled(true);
   }
}
