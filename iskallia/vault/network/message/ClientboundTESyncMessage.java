package iskallia.vault.network.message;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundTESyncMessage {
   public final BlockPos pos;
   public final CompoundTag tag;

   public ClientboundTESyncMessage(BlockPos pos, CompoundTag tag) {
      this.pos = pos;
      this.tag = tag;
   }

   public static void encode(ClientboundTESyncMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
      buffer.writeNbt(message.tag);
   }

   public static ClientboundTESyncMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundTESyncMessage(buffer.readBlockPos(), buffer.readNbt());
   }

   public static void handle(ClientboundTESyncMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> sync(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void sync(ClientboundTESyncMessage message) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         BlockEntity be = level.getBlockEntity(message.pos);
         if (be != null) {
            be.load(message.tag);
            be.setChanged();
         }
      }
   }
}
