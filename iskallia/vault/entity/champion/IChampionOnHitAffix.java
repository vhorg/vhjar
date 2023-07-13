package iskallia.vault.entity.champion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface IChampionOnHitAffix extends IChampionAffix {
   void onChampionHitPlayer(LivingEntity var1, Player var2, float var3);
}
