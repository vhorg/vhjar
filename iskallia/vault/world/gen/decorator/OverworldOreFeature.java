package iskallia.vault.world.gen.decorator;

import com.mojang.serialization.Codec;
import iskallia.vault.VaultMod;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraftforge.event.RegistryEvent.Register;

public class OverworldOreFeature extends OreFeature {
   public static Feature<OreConfiguration> INSTANCE;

   public OverworldOreFeature(Codec<OreConfiguration> codec) {
      super(codec);
   }

   public boolean place(FeaturePlaceContext<OreConfiguration> context) {
      WorldGenLevel world = context.level();
      OreConfiguration config = (OreConfiguration)context.config();
      BlockPos pos = context.origin();
      Random random = context.random();
      if (world.getLevel().dimension() != Level.OVERWORLD) {
         return false;
      } else if (config.size == 1) {
         for (TargetBlockState targetState : config.targetStates) {
            if (targetState.target.test(world.getBlockState(pos), random)) {
               world.setBlock(pos, targetState.state, 2);
               return true;
            }
         }

         return false;
      } else {
         return super.place(context);
      }
   }

   public static void register(Register<Feature<?>> event) {
      INSTANCE = new OverworldOreFeature(OreConfiguration.CODEC);
      INSTANCE.setRegistryName(VaultMod.id("overworld_ore"));
      event.getRegistry().register(INSTANCE);
   }
}
