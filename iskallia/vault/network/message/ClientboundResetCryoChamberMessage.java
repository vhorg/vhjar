package iskallia.vault.network.message;

import iskallia.vault.block.entity.CryoChamberTileEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundResetCryoChamberMessage {
   private final BlockPos pos;

   public ClientboundResetCryoChamberMessage(BlockPos pos) {
      this.pos = pos;
   }

   public static void encode(ClientboundResetCryoChamberMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
   }

   public static ClientboundResetCryoChamberMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundResetCryoChamberMessage(buffer.readBlockPos());
   }

   public static void handle(ClientboundResetCryoChamberMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> handle(message));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void handle(ClientboundResetCryoChamberMessage message) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (minecraft.level != null) {
         if (minecraft.level.getBlockEntity(message.pos) instanceof CryoChamberTileEntity cryoChamberTileEntity) {
            cryoChamberTileEntity.resetAll();
         }
      }
   }
}
