package iskallia.vault.network.message.bounty;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.client.gui.screen.bounty.BountyProgressScreen;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundBountyProgressMessage(Bounty active) {
   public static void encode(ClientboundBountyProgressMessage message, FriendlyByteBuf buffer) {
      if (message.active != null) {
         buffer.writeNbt(message.active.serializeNBT());
      }
   }

   public static ClientboundBountyProgressMessage decode(FriendlyByteBuf buffer) {
      try {
         CompoundTag tag = buffer.readNbt();
         Bounty active = new Bounty(tag);
         return new ClientboundBountyProgressMessage(active);
      } catch (Exception var3) {
         return new ClientboundBountyProgressMessage(null);
      }
   }

   public static void handle(ClientboundBountyProgressMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> openScreen(message.active));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void openScreen(Bounty active) {
      Minecraft.getInstance().setScreen(new BountyProgressScreen(active));
   }
}
