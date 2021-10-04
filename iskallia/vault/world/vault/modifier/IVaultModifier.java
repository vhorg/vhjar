package iskallia.vault.world.vault.modifier;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Random;
import net.minecraft.world.server.ServerWorld;

public interface IVaultModifier {
   void apply(VaultRaid var1, VaultPlayer var2, ServerWorld var3, Random var4);

   void remove(VaultRaid var1, VaultPlayer var2, ServerWorld var3, Random var4);

   void tick(VaultRaid var1, VaultPlayer var2, ServerWorld var3);
}
