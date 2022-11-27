package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import net.minecraft.resources.ResourceLocation;

public class AccelerationExtension extends TimeExtension {
   public static final ResourceLocation ID = VaultMod.id("acceleration");

   public AccelerationExtension() {
   }

   public AccelerationExtension(int value) {
      super(ID, value);
   }
}
