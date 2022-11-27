package iskallia.vault.world.vault.modifier.spi;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import net.minecraft.server.level.ServerLevel;

public interface IVaultModifierBehaviorTick {
   void tick(VaultRaid var1, VaultPlayer var2, ServerLevel var3, int var4);
}
