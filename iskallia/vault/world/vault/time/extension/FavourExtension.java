package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import net.minecraft.resources.ResourceLocation;

public class FavourExtension extends TimeExtension {
   public static final ResourceLocation ID = VaultMod.id("favour");

   public FavourExtension() {
   }

   public FavourExtension(long extraTime) {
      super(ID, extraTime);
   }
}
