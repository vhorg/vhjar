package iskallia.vault.event.event;

import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ForgeGearEvent extends PlayerEvent {
   private final VaultForgeRecipe recipe;

   public ForgeGearEvent(Player player, @NotNull VaultForgeRecipe recipe) {
      super(player);
      this.recipe = recipe;
   }

   public VaultForgeRecipe getRecipe() {
      return this.recipe;
   }

   public ResourceLocation getRecipeId() {
      return this.recipe.getId();
   }
}
