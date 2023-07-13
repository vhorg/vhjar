package iskallia.vault.entity.champion;

import net.minecraft.world.entity.LivingEntity;

public interface IChampionTickableAffix extends IChampionAffix {
   void tick(LivingEntity var1);
}
