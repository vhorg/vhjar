package iskallia.vault.network.message;

import iskallia.vault.block.entity.WardrobeTileEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundWardrobeSwapMessage {
   private final BlockPos wardrobePos;
   private final boolean isShiftKeyDown;

   public ServerboundWardrobeSwapMessage(BlockPos wardrobePos, boolean isShiftKeyDown) {
      this.wardrobePos = wardrobePos;
      this.isShiftKeyDown = isShiftKeyDown;
   }

   public static void encode(ServerboundWardrobeSwapMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.wardrobePos);
      buffer.writeBoolean(message.isShiftKeyDown);
   }

   public static ServerboundWardrobeSwapMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundWardrobeSwapMessage(buffer.readBlockPos(), buffer.readBoolean());
   }

   public static void handle(ServerboundWardrobeSwapMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer serverPlayer = context.getSender();
         ServerLevel serverWorld = serverPlayer.getLevel();
         if (serverWorld.getBlockEntity(message.wardrobePos) instanceof WardrobeTileEntity wardrobe) {
            wardrobe.swap(serverPlayer, message.isShiftKeyDown);
         }
      });
      context.setPacketHandled(true);
   }
}
