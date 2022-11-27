package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import net.minecraft.resources.ResourceLocation;

public class RoomGenerationExtension extends TimeExtension {
   public static final ResourceLocation ID = VaultMod.id("room_generation");

   public RoomGenerationExtension() {
   }

   public RoomGenerationExtension(int value) {
      super(ID, value);
   }
}
