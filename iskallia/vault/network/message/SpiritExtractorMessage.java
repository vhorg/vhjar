package iskallia.vault.network.message;

import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent.Context;

public class SpiritExtractorMessage {
   private final BlockPos extractorPos;
   private final SpiritExtractorMessage.Action action;

   public SpiritExtractorMessage(BlockPos extractorPos, SpiritExtractorMessage.Action action) {
      this.extractorPos = extractorPos;
      this.action = action;
   }

   public static void encode(SpiritExtractorMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.extractorPos);
      buffer.writeUtf(message.action.toString());
   }

   public static SpiritExtractorMessage decode(FriendlyByteBuf buffer) {
      return new SpiritExtractorMessage(buffer.readBlockPos(), SpiritExtractorMessage.Action.valueOf(buffer.readUtf()));
   }

   public static void handle(SpiritExtractorMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerLevel serverWorld = context.getSender().getLevel();
         if (serverWorld.getBlockEntity(message.extractorPos) instanceof SpiritExtractorTileEntity spiritExtractor) {
            if (message.action == SpiritExtractorMessage.Action.REVIVE) {
               spiritExtractor.spewItems();
            } else if (message.action == SpiritExtractorMessage.Action.RECYCLE) {
               spiritExtractor.recycle();
            }
         }
      });
      context.setPacketHandled(true);
   }

   public static enum Action {
      REVIVE,
      RECYCLE;
   }
}
