package iskallia.vault.world.vault.chest;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import net.minecraft.world.server.ServerWorld;

public abstract class VaultChestEffect {
   @Expose
   private final String name;

   public VaultChestEffect(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean isTrapEffect() {
      return true;
   }

   public abstract void apply(VaultRaid var1, VaultPlayer var2, ServerWorld var3);
}
