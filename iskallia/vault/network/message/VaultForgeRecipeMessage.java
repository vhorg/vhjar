package iskallia.vault.network.message;

import iskallia.vault.client.ClientVaultForgeData;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultForgeRecipeMessage {
   private final List<VaultForgeRecipe> recipes;

   public VaultForgeRecipeMessage(List<VaultForgeRecipe> recipes) {
      this.recipes = recipes;
   }

   public static void encode(VaultForgeRecipeMessage message, FriendlyByteBuf buffer) {
      buffer.writeCollection(message.recipes, (buf, recipe) -> recipe.write(buf));
   }

   public static VaultForgeRecipeMessage decode(FriendlyByteBuf buffer) {
      return new VaultForgeRecipeMessage(buffer.readList(VaultForgeRecipe::read));
   }

   public static void handle(VaultForgeRecipeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientVaultForgeData.receiveMessage(message.recipes));
      context.setPacketHandled(true);
   }
}
