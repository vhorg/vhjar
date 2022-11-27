package iskallia.vault.world.vault.chest;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.server.level.ServerPlayer;

public abstract class VaultChestEffect {
   @Expose
   private final String name;

   public VaultChestEffect(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public abstract void apply(VirtualWorld var1, Vault var2, ServerPlayer var3);
}
