package iskallia.vault.config.recipe;

import iskallia.vault.config.Config;
import iskallia.vault.config.entry.recipe.ConfigForgeRecipe;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ForgeRecipeSyncMessage;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public abstract class ForgeRecipesConfig<T extends ConfigForgeRecipe<V>, V extends VaultForgeRecipe> extends Config {
   private final Set<Item> inputItemCache = new HashSet<>();
   private final ForgeRecipeType recipeType;

   protected ForgeRecipesConfig(ForgeRecipeType recipeType) {
      this.recipeType = recipeType;
   }

   @Override
   public String getName() {
      return "recipes%s%s_recipes".formatted(File.separator, this.recipeType.name().toLowerCase(Locale.ROOT));
   }

   public abstract List<T> getConfigRecipes();

   public Set<Item> getInputItemCache() {
      return Collections.unmodifiableSet(this.inputItemCache);
   }

   @Nullable
   public V getRecipe(ResourceLocation id) {
      for (T recipe : this.getConfigRecipes()) {
         if (id.equals(recipe.getId())) {
            return recipe.makeRecipe();
         }
      }

      return null;
   }

   @Override
   public <C extends Config> C readConfig() {
      C cfg = super.readConfig();
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      if (srv != null) {
         srv.getPlayerList().getPlayers().forEach(player -> this.syncTo((ForgeRecipesConfig<?, ?>)cfg, player));
      }

      return cfg;
   }

   @Override
   protected void onLoad(@Nullable Config oldConfigInstance) {
      this.inputItemCache.clear();

      for (T recipe : this.getConfigRecipes()) {
         recipe.makeRecipe().getInputs().forEach(inputStack -> {
            if (!inputStack.isEmpty()) {
               this.inputItemCache.add(inputStack.getItem());
            }
         });
      }
   }

   public void syncTo(ForgeRecipesConfig<?, ?> cfg, ServerPlayer player) {
      ModNetwork.CHANNEL
         .sendTo(
            ForgeRecipeSyncMessage.fromConfig((List<? extends ConfigForgeRecipe>)cfg.getConfigRecipes(), this.recipeType),
            player.connection.connection,
            NetworkDirection.PLAY_TO_CLIENT
         );
   }
}
