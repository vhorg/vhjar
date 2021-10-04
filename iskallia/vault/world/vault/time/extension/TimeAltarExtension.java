package iskallia.vault.world.vault.time.extension;

import iskallia.vault.Vault;
import net.minecraft.util.ResourceLocation;

public class TimeAltarExtension extends TimeExtension {
   public static final ResourceLocation ID = Vault.id("time_altar");

   public TimeAltarExtension() {
   }

   public TimeAltarExtension(int value) {
      super(ID, value);
   }
}
