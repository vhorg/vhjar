package iskallia.vault.event.event;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class VaultJoinEvent extends Event {
   private final Vault vault;

   public VaultJoinEvent(Vault vault) {
      this.vault = vault;
   }

   public Vault getVault() {
      return this.vault;
   }

   public List<ServerPlayer> getPlayers() {
      return this.vault
         .get(Vault.LISTENERS)
         .getAll()
         .stream()
         .filter(l -> l instanceof Runner)
         .map(Listener::getPlayer)
         .filter(Optional::isPresent)
         .map(Optional::get)
         .toList();
   }
}
