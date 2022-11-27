package iskallia.vault.event.event;

import iskallia.vault.core.vault.Vault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class VaultLeaveForgeEvent extends Event {
   private final Vault vault;
   private final ServerPlayer player;

   public VaultLeaveForgeEvent(ServerPlayer player, Vault vault) {
      this.player = player;
      this.vault = vault;
   }

   public ServerPlayer getPlayer() {
      return this.player;
   }

   public Vault getVault() {
      return this.vault;
   }
}
