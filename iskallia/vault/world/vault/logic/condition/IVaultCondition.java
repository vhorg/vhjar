package iskallia.vault.world.vault.logic.condition;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Objects;
import net.minecraft.world.server.ServerWorld;

@FunctionalInterface
public interface IVaultCondition {
   boolean test(VaultRaid var1, VaultPlayer var2, ServerWorld var3);

   default IVaultCondition negate() {
      return (vault, player, world) -> !this.test(vault, player, world);
   }

   default IVaultCondition and(IVaultCondition other) {
      Objects.requireNonNull(other);
      return (vault, player, world) -> this.test(vault, player, world) && other.test(vault, player, world);
   }

   default IVaultCondition or(IVaultCondition other) {
      Objects.requireNonNull(other);
      return (vault, player, world) -> this.test(vault, player, world) || other.test(vault, player, world);
   }

   default IVaultCondition xor(IVaultCondition other) {
      Objects.requireNonNull(other);
      return (vault, player, world) -> {
         boolean a = this.test(vault, player, world);
         boolean b = other.test(vault, player, world);
         return !a && b || a && !b;
      };
   }
}
