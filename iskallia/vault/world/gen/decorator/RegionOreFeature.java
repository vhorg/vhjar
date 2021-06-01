package iskallia.vault.world.gen.decorator;

import com.mojang.serialization.Codec;
import iskallia.vault.Vault;
import iskallia.vault.config.VaultOreConfig;
import iskallia.vault.init.ModConfigs;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.event.RegistryEvent.Register;

public class RegionOreFeature extends OreFeature {
   public static Feature<OreFeatureConfig> INSTANCE;

   public RegionOreFeature(Codec<OreFeatureConfig> codec) {
      super(codec);
   }

   public boolean func_241855_a(ISeedReader view, ChunkGenerator gen, Random random, BlockPos pos, OreFeatureConfig config) {
      VaultOreConfig.Ore[] pool = ModConfigs.VAULT_ORES
         .getPool(view.func_72905_C(), pos.func_177958_n() >> 4, pos.func_177952_p() >> 4, new SharedSeedRandom());
      boolean result = false;

      for (VaultOreConfig.Ore ore : pool) {
         for (int i = 0; i < ore.TRIES; i++) {
            int x = random.nextInt(16);
            int y = random.nextInt(256);
            int z = random.nextInt(16);
            if (this.isNearTunnel(view, pos.func_177982_a(x, y, z))) {
               result |= super.func_241855_a(view, gen, random, pos.func_177982_a(x, y, z), ore.toConfig());
            }
         }
      }

      return result;
   }

   private boolean isNearTunnel(ISeedReader view, BlockPos pos) {
      for (int x = -3; x <= 3; x++) {
         for (int z = -3; z <= 3; z++) {
            for (int y = -3; y <= 3; y++) {
               if (view.func_180495_p(pos.func_177982_a(x, y, z)).func_177230_c() == Blocks.field_201941_jj) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static void register(Register<Feature<?>> event) {
      INSTANCE = new RegionOreFeature(OreFeatureConfig.field_236566_a_);
      INSTANCE.setRegistryName(Vault.id("vault_ore"));
      event.getRegistry().register(INSTANCE);
   }
}
