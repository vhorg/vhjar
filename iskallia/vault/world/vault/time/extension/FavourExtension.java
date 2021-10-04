package iskallia.vault.world.vault.time.extension;

import iskallia.vault.Vault;
import net.minecraft.util.ResourceLocation;

public class FavourExtension extends TimeExtension {
   public static final ResourceLocation ID = Vault.id("relic_set");

   public FavourExtension() {
   }

   public FavourExtension(long extraTime) {
      super(ID, extraTime);
   }
}
