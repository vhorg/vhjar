package iskallia.vault.world.gen.decorator;

import com.mojang.serialization.Codec;
import iskallia.vault.Vault;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.event.RegistryEvent.Register;

public class OverworldOreFeature extends OreFeature {
   public static Feature<OreFeatureConfig> INSTANCE;

   public OverworldOreFeature(Codec<OreFeatureConfig> codec) {
      super(codec);
   }

   public boolean func_241855_a(ISeedReader world, ChunkGenerator gen, Random random, BlockPos pos, OreFeatureConfig config) {
      if (world.func_201672_e().func_234923_W_() != World.field_234918_g_) {
         return false;
      } else if (config.field_202443_c == 1) {
         if (config.field_202442_b.func_215181_a(world.func_180495_p(pos), random)) {
            world.func_180501_a(pos, config.field_202444_d, 2);
            return true;
         } else {
            return false;
         }
      } else {
         return super.func_241855_a(world, gen, random, pos, config);
      }
   }

   public static void register(Register<Feature<?>> event) {
      INSTANCE = new OverworldOreFeature(OreFeatureConfig.field_236566_a_);
      INSTANCE.setRegistryName(Vault.id("overworld_ore"));
      event.getRegistry().register(INSTANCE);
   }
}
