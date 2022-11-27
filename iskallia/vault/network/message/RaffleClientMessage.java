package iskallia.vault.network.message;

import iskallia.vault.client.gui.screen.RaffleScreen;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class RaffleClientMessage {
   public RaffleClientMessage.Opcode opcode;
   public CompoundTag payload;

   public static void encode(RaffleClientMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.opcode.ordinal());
      buffer.writeNbt(message.payload);
   }

   public static RaffleClientMessage decode(FriendlyByteBuf buffer) {
      RaffleClientMessage message = new RaffleClientMessage();
      message.opcode = RaffleClientMessage.Opcode.values()[buffer.readInt()];
      message.payload = buffer.readNbt();
      return message;
   }

   public static void handle(RaffleClientMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (message.opcode == RaffleClientMessage.Opcode.OPEN_UI) {
            List<String> occupants = NBTHelper.readList(message.payload, "Occupants", StringTag.class, StringTag::getAsString);
            String winner = message.payload.getString("Winner");
            displayGUI(occupants, winner);
         }
      });
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void displayGUI(List<String> occupants, String winner) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.setScreen(new RaffleScreen(occupants, winner));
   }

   public static RaffleClientMessage openUI(List<WeightedList.Entry<String>> occupants, String winner) {
      RaffleClientMessage message = new RaffleClientMessage();
      message.opcode = RaffleClientMessage.Opcode.OPEN_UI;
      message.payload = new CompoundTag();
      NBTHelper.writeCollection(message.payload, "Occupants", occupants, StringTag.class, occupant -> StringTag.valueOf((String)occupant.value));
      message.payload.putString("Winner", winner);
      return message;
   }

   public static enum Opcode {
      OPEN_UI;
   }
}
