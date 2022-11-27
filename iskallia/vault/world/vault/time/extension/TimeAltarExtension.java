package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import net.minecraft.resources.ResourceLocation;

public class TimeAltarExtension extends TimeExtension {
   public static final ResourceLocation ID = VaultMod.id("time_altar");

   public TimeAltarExtension() {
   }

   public TimeAltarExtension(int value) {
      super(ID, value);
   }
}
