package iskallia.vault.world.vault.time.extension;

import iskallia.vault.Vault;
import net.minecraft.util.ResourceLocation;

public class RoomGenerationExtension extends TimeExtension {
   public static final ResourceLocation ID = Vault.id("room_generation");

   public RoomGenerationExtension() {
   }

   public RoomGenerationExtension(int value) {
      super(ID, value);
   }
}
