package iskallia.vault.util;

import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PlayerFilter implements Predicate<UUID> {
   private final List<UUID> playerUUIDs;

   private PlayerFilter(List<UUID> playerUUIDs) {
      this.playerUUIDs = playerUUIDs;
   }

   public static PlayerFilter any() {
      return new PlayerFilter(Collections.emptyList());
   }

   public static PlayerFilter of(UUID... playerIds) {
      return new PlayerFilter(Arrays.asList(playerIds));
   }

   public static PlayerFilter of(Player... players) {
      return new PlayerFilter(Arrays.stream(players).<UUID>map(Entity::getUUID).collect(Collectors.toList()));
   }

   public static PlayerFilter of(VaultPlayer... players) {
      return new PlayerFilter(Arrays.stream(players).map(VaultPlayer::getPlayerId).collect(Collectors.toList()));
   }

   public boolean test(UUID uuid) {
      return this.playerUUIDs.isEmpty() || this.playerUUIDs.contains(uuid);
   }
}
