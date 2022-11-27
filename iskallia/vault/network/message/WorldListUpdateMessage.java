package iskallia.vault.network.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class WorldListUpdateMessage {
   private List<ResourceLocation> ids;

   public WorldListUpdateMessage() {
   }

   public WorldListUpdateMessage(List<ResourceLocation> ids) {
      this.ids = ids;
   }

   public static void encode(WorldListUpdateMessage message, FriendlyByteBuf buffer) {
      buffer.writeCollection(message.ids, FriendlyByteBuf::writeResourceLocation);
   }

   public static WorldListUpdateMessage decode(FriendlyByteBuf buffer) {
      WorldListUpdateMessage message = new WorldListUpdateMessage();
      message.ids = (List<ResourceLocation>)buffer.readCollection(ArrayList::new, FriendlyByteBuf::readResourceLocation);
      return message;
   }

   public static void handle(WorldListUpdateMessage message, Supplier<Context> contextSupplier) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         Set<ResourceKey<Level>> list = player.connection.levels();
         message.ids.forEach(id -> list.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, id)));
         contextSupplier.get().setPacketHandled(true);
      }
   }
}
