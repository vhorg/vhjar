package iskallia.vault.network.message;

import iskallia.vault.client.ClientForgeRecipesData;
import iskallia.vault.config.entry.recipe.ConfigForgeRecipe;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ForgeRecipeSyncMessage {
   private final List<? extends VaultForgeRecipe> recipes;
   private final ForgeRecipeType type;

   public ForgeRecipeSyncMessage(List<? extends VaultForgeRecipe> recipes, ForgeRecipeType type) {
      this.recipes = recipes;
      this.type = type;
   }

   public static ForgeRecipeSyncMessage fromConfig(List<? extends ConfigForgeRecipe> configuredRecipes, ForgeRecipeType type) {
      List<VaultForgeRecipe> recipes = new ArrayList<>();
      configuredRecipes.forEach(recipe -> recipes.add(recipe.makeRecipe()));
      return new ForgeRecipeSyncMessage(recipes, type);
   }

   public static void encode(ForgeRecipeSyncMessage message, FriendlyByteBuf buffer) {
      buffer.writeCollection(message.recipes, (buf, recipe) -> recipe.write(buf));
      buffer.writeEnum(message.type);
   }

   public static ForgeRecipeSyncMessage decode(FriendlyByteBuf buffer) {
      return new ForgeRecipeSyncMessage(buffer.readList(VaultForgeRecipe::read), (ForgeRecipeType)buffer.readEnum(ForgeRecipeType.class));
   }

   public static void handle(ForgeRecipeSyncMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientForgeRecipesData.receiveMessage(message.recipes, message.type));
      context.setPacketHandled(true);
   }
}
