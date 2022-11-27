package iskallia.vault.network.message;

import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.world.data.EternalsData;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundToggleEternalPlayerSkinMessage {
   private final BlockPos pos;

   private ServerboundToggleEternalPlayerSkinMessage(BlockPos pos) {
      this.pos = pos;
   }

   public static void send(BlockPos pos) {
      ModNetwork.CHANNEL.sendToServer(new ServerboundToggleEternalPlayerSkinMessage(pos));
   }

   public static void encode(ServerboundToggleEternalPlayerSkinMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
   }

   public static ServerboundToggleEternalPlayerSkinMessage decode(FriendlyByteBuf buffer) {
      BlockPos pos = buffer.readBlockPos();
      return new ServerboundToggleEternalPlayerSkinMessage(pos);
   }

   public static void handle(ServerboundToggleEternalPlayerSkinMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer serverPlayer = context.getSender();
         if (serverPlayer != null) {
            if (serverPlayer.getLevel().getBlockEntity(message.pos) instanceof CryoChamberTileEntity cryoChamberTileEntity) {
               cryoChamberTileEntity.usingPlayerSkin = !cryoChamberTileEntity.usingPlayerSkin;
               EternalData eternalData = cryoChamberTileEntity.getEternal();
               if (eternalData != null) {
                  eternalData.setUsingPlayerSkin(cryoChamberTileEntity.usingPlayerSkin);
                  EternalsData.get(serverPlayer.getLevel()).setDirty();
               }

               cryoChamberTileEntity.sendUpdates();
               NetworkHooks.openGui(serverPlayer, cryoChamberTileEntity, buffer -> buffer.writeBlockPos(cryoChamberTileEntity.getBlockPos()));
            } else if (serverPlayer.getLevel().getBlockEntity(message.pos.below()) instanceof CryoChamberTileEntity cryoChamberTileEntity) {
               cryoChamberTileEntity.usingPlayerSkin = !cryoChamberTileEntity.usingPlayerSkin;
               EternalData eternalData = cryoChamberTileEntity.getEternal();
               if (eternalData != null) {
                  eternalData.setUsingPlayerSkin(cryoChamberTileEntity.usingPlayerSkin);
                  EternalsData.get(serverPlayer.getLevel()).setDirty();
               }

               cryoChamberTileEntity.sendUpdates();
               NetworkHooks.openGui(serverPlayer, cryoChamberTileEntity, buffer -> buffer.writeBlockPos(cryoChamberTileEntity.getBlockPos()));
            }
         }
      });
      context.setPacketHandled(true);
   }
}
