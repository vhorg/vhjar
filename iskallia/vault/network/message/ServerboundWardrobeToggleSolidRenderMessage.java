package iskallia.vault.network.message;

import iskallia.vault.block.entity.WardrobeTileEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundWardrobeToggleSolidRenderMessage {
   private final BlockPos wardrobePos;

   public ServerboundWardrobeToggleSolidRenderMessage(BlockPos wardrobePos) {
      this.wardrobePos = wardrobePos;
   }

   public static void encode(ServerboundWardrobeToggleSolidRenderMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.wardrobePos);
   }

   public static ServerboundWardrobeToggleSolidRenderMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundWardrobeToggleSolidRenderMessage(buffer.readBlockPos());
   }

   public static void handle(ServerboundWardrobeToggleSolidRenderMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer serverPlayer = context.getSender();
         ServerLevel serverWorld = serverPlayer.getLevel();
         if (serverWorld.getBlockEntity(message.wardrobePos) instanceof WardrobeTileEntity wardrobe) {
            wardrobe.toggleSolidRender();
         }
      });
      context.setPacketHandled(true);
   }
}
