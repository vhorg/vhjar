package iskallia.vault.entity.boss.trait;

import iskallia.vault.entity.boss.VaultBossEntity;
import net.minecraft.world.entity.player.Player;

public interface IOnHitEffect {
   void onHit(VaultBossEntity var1, Player var2, float var3);
}
