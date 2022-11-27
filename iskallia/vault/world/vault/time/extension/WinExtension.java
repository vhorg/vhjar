package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import iskallia.vault.world.vault.time.VaultTimer;
import net.minecraft.resources.ResourceLocation;

public class WinExtension extends TimeExtension {
   public static final ResourceLocation ID = VaultMod.id("win");

   public WinExtension() {
   }

   public WinExtension(VaultTimer timer, int target) {
      this(ID, timer, target);
   }

   public WinExtension(ResourceLocation id, VaultTimer timer, int target) {
      super(id, -(timer.getTimeLeft() - target));
   }
}
