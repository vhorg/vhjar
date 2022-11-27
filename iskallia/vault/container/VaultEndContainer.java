package iskallia.vault.container;

import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.init.ModContainers;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class VaultEndContainer extends AbstractElementContainer {
   private final VaultSnapshot snapshot;

   public VaultEndContainer(int id, Inventory playerInventory, VaultSnapshot snapshot) {
      super(ModContainers.VAULT_END_CONTAINER, id, playerInventory.player);
      this.snapshot = snapshot;
   }

   public VaultSnapshot getSnapshot() {
      return this.snapshot;
   }

   public boolean stillValid(@Nonnull Player player) {
      return true;
   }
}
