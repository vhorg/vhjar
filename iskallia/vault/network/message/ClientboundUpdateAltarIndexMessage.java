package iskallia.vault.network.message;

import iskallia.vault.block.entity.VaultAltarTileEntity;
import java.util.HashMap;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundUpdateAltarIndexMessage(BlockPos pos, HashMap<String, Integer> displayedIndex) {
   public static void encode(ClientboundUpdateAltarIndexMessage message, FriendlyByteBuf buffer) {
      CompoundTag tag = new CompoundTag();
      CompoundTag displayed = new CompoundTag();
      message.displayedIndex.forEach(displayed::putInt);
      tag.put("Displayed", displayed);
      buffer.writeNbt(tag);
      buffer.writeBlockPos(message.pos);
   }

   public static ClientboundUpdateAltarIndexMessage decode(FriendlyByteBuf buffer) {
      HashMap<String, Integer> displayedIndex = new HashMap<>();
      CompoundTag tag = buffer.readNbt();
      if (tag != null && tag.contains("Displayed")) {
         CompoundTag displayed = tag.getCompound("Displayed");

         for (String poolId : displayed.getAllKeys()) {
            displayedIndex.put(poolId, displayed.getInt(poolId));
         }
      }

      BlockPos pos = buffer.readBlockPos();
      return new ClientboundUpdateAltarIndexMessage(pos, displayedIndex);
   }

   public static void handle(ClientboundUpdateAltarIndexMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> updateDisplayedIndex(message, context));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void updateDisplayedIndex(ClientboundUpdateAltarIndexMessage message, Context context) {
      BlockPos pos = message.pos;
      HashMap<String, Integer> displayedIndex = message.displayedIndex;
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         if (level.getBlockEntity(pos) instanceof VaultAltarTileEntity altar) {
            altar.setDisplayedIndex(displayedIndex);
         }
      }
   }
}
