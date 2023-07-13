package iskallia.vault.entity.champion;

import net.minecraft.nbt.CompoundTag;

public interface IChampionAffix {
   CompoundTag serialize();

   String getType();

   String getName();
}
