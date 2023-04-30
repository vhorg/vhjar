package iskallia.vault.network.message;

import iskallia.vault.client.gui.component.toast.GenericToast;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundToastMessage(String title, String message, ResourceLocation icon) {
   public static void encode(ClientboundToastMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeUtf(pkt.title(), pkt.title().length());
      buffer.writeUtf(pkt.message(), pkt.message().length());
      buffer.writeResourceLocation(pkt.icon());
   }

   public static ClientboundToastMessage decode(FriendlyByteBuf buffer) {
      String questName = buffer.readUtf();
      String message = buffer.readUtf();
      ResourceLocation icon = buffer.readResourceLocation();
      return new ClientboundToastMessage(questName, message, icon);
   }

   public static void handle(ClientboundToastMessage pkt, Supplier<Context> contextSupplier) {
      toast(pkt.title(), pkt.message(), pkt.icon());
      contextSupplier.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void toast(String title, String message, ResourceLocation icon) {
      GenericToast.add(title, message, icon);
   }
}
