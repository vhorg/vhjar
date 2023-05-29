package iskallia.vault.network.message;

import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class JewelCuttingParticleMessage {
   private final BlockPos pos;
   private final ItemStack stack;

   public JewelCuttingParticleMessage(BlockPos pos, ItemStack stack) {
      this.pos = pos;
      this.stack = stack;
   }

   public static void encode(JewelCuttingParticleMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
      buffer.writeItem(message.stack);
   }

   public static JewelCuttingParticleMessage decode(FriendlyByteBuf buffer) {
      return new JewelCuttingParticleMessage(buffer.readBlockPos(), buffer.readItem());
   }

   public static void handle(JewelCuttingParticleMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (Minecraft.getInstance().level != null) {
            VaultJewelCuttingStationTileEntity.spawnBreakParticles(message.pos, message.stack);
         }
      });
      context.setPacketHandled(true);
   }
}
