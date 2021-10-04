package iskallia.vault.world.vault.time.extension;

import iskallia.vault.Vault;
import net.minecraft.util.ResourceLocation;

public class AccelerationExtension extends TimeExtension {
   public static final ResourceLocation ID = Vault.id("acceleration");

   public AccelerationExtension() {
   }

   public AccelerationExtension(int value) {
      super(ID, value);
   }
}
