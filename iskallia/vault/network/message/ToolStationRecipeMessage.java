package iskallia.vault.network.message;

import iskallia.vault.client.ClientToolStationData;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ToolStationRecipeMessage {
   private final List<VaultForgeRecipe> recipes;

   public ToolStationRecipeMessage(List<VaultForgeRecipe> recipes) {
      this.recipes = recipes;
   }

   public static void encode(ToolStationRecipeMessage message, FriendlyByteBuf buffer) {
      buffer.writeCollection(message.recipes, (buf, recipe) -> recipe.write(buf));
   }

   public static ToolStationRecipeMessage decode(FriendlyByteBuf buffer) {
      return new ToolStationRecipeMessage(buffer.readList(VaultForgeRecipe::read));
   }

   public static void handle(ToolStationRecipeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientToolStationData.receiveMessage(message.recipes));
      context.setPacketHandled(true);
   }
}
