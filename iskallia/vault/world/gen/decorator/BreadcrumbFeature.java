package iskallia.vault.world.gen.decorator;

import com.mojang.serialization.Codec;
import iskallia.vault.VaultMod;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.event.RegistryEvent.Register;

public class BreadcrumbFeature extends Feature<NoneFeatureConfiguration> {
   public static Feature<NoneFeatureConfiguration> INSTANCE;

   public BreadcrumbFeature(Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      return false;
   }

   public static void register(Register<Feature<?>> event) {
      INSTANCE = new BreadcrumbFeature(NoneFeatureConfiguration.CODEC);
      INSTANCE.setRegistryName(VaultMod.id("breadcrumb_chest"));
      event.getRegistry().register(INSTANCE);
   }
}
