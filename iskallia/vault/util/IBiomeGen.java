package iskallia.vault.util;

import net.minecraft.world.level.biome.BiomeSource;

public interface IBiomeGen {
   BiomeSource getProvider1();

   BiomeSource getProvider2();
}
