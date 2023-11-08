package iskallia.vault.network.message;

import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;

public class DiscoverModifierMessage {
   private final BlockPos pos;
   private final Tuple<Item, ResourceLocation> gearModifier;

   public DiscoverModifierMessage(BlockPos pos, Tuple<Item, ResourceLocation> gearModifier) {
      this.pos = pos;
      this.gearModifier = gearModifier;
   }

   public static void encode(DiscoverModifierMessage message, FriendlyByteBuf buffer) {
      buffer.writeBlockPos(message.pos);
      buffer.writeUtf(((Item)message.gearModifier.getA()).getRegistryName().toString());
      buffer.writeUtf(((ResourceLocation)message.gearModifier.getB()).toString());
   }

   public static DiscoverModifierMessage decode(FriendlyByteBuf buffer) {
      BlockPos pos = buffer.readBlockPos();
      return new DiscoverModifierMessage(
         pos, new Tuple((Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(buffer.readUtf())), new ResourceLocation(buffer.readUtf()))
      );
   }

   public static void handle(DiscoverModifierMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         BlockPos pos = message.pos;
         if (player.getLevel().getBlockEntity(pos) instanceof ModifierDiscoveryTileEntity modifierDiscoveryTile) {
            modifierDiscoveryTile.discoverGearModifier(player, message.gearModifier);
         }
      });
      context.setPacketHandled(true);
   }
}
