package iskallia.vault.mixin;

import iskallia.vault.util.IBiomeAccessor;
import iskallia.vault.util.IBiomeGen;
import iskallia.vault.util.IBiomeUpdate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.SimplexNoiseGenerator;
import net.minecraft.world.gen.settings.NoiseSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({NoiseChunkGenerator.class})
public class MixinNoiseChunkGenerator implements IBiomeUpdate {
   @Mutable
   @Shadow
   @Final
   protected SharedSeedRandom field_222558_e;
   @Mutable
   @Shadow
   @Final
   private OctavesNoiseGenerator field_222568_o;
   @Mutable
   @Shadow
   @Final
   private OctavesNoiseGenerator field_222569_p;
   @Mutable
   @Shadow
   @Final
   private OctavesNoiseGenerator field_222570_q;
   @Mutable
   @Shadow
   @Final
   private INoiseGenerator field_222571_r;
   @Mutable
   @Shadow
   @Final
   private OctavesNoiseGenerator field_236082_u_;
   @Mutable
   @Shadow
   @Final
   private SimplexNoiseGenerator field_236083_v_;
   @Shadow
   @Final
   protected Supplier<DimensionSettings> field_236080_h_;

   @Override
   public void update(BiomeProvider source) {
      if (source instanceof OverworldBiomeProvider) {
         IBiomeAccessor s = (IBiomeAccessor)source;
         if (((IBiomeGen)this).getProvider1() instanceof OverworldBiomeProvider) {
            OverworldBiomeProvider owSource = (OverworldBiomeProvider)((IBiomeGen)this).getProvider1();
            ((IBiomeAccessor)owSource).setSeed(s.getSeed());
            ((IBiomeAccessor)owSource).setLegacyBiomes(s.getLegacyBiomes());
            ((IBiomeAccessor)owSource).setLargeBiomes(s.getLargeBiomes());
         }

         if (((IBiomeGen)this).getProvider2() instanceof OverworldBiomeProvider) {
            OverworldBiomeProvider owSource = (OverworldBiomeProvider)((IBiomeGen)this).getProvider2();
            ((IBiomeAccessor)owSource).setSeed(s.getSeed());
            ((IBiomeAccessor)owSource).setLegacyBiomes(s.getLegacyBiomes());
            ((IBiomeAccessor)owSource).setLargeBiomes(s.getLargeBiomes());
         }

         NoiseSettings noiseSettings = this.field_236080_h_.get().func_236113_b_();
         this.field_222558_e = new SharedSeedRandom(s.getSeed());
         this.field_222568_o = new OctavesNoiseGenerator(this.field_222558_e, IntStream.rangeClosed(-15, 0));
         this.field_222569_p = new OctavesNoiseGenerator(this.field_222558_e, IntStream.rangeClosed(-15, 0));
         this.field_222570_q = new OctavesNoiseGenerator(this.field_222558_e, IntStream.rangeClosed(-7, 0));
         this.field_222571_r = (INoiseGenerator)(noiseSettings.func_236178_i_()
            ? new PerlinNoiseGenerator(this.field_222558_e, IntStream.rangeClosed(-3, 0))
            : new OctavesNoiseGenerator(this.field_222558_e, IntStream.rangeClosed(-3, 0)));
         this.field_222558_e.func_202423_a(2620);
         this.field_236082_u_ = new OctavesNoiseGenerator(this.field_222558_e, IntStream.rangeClosed(-15, 0));
         if (noiseSettings.func_236180_k_()) {
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom(s.getSeed());
            sharedseedrandom.func_202423_a(17292);
            this.field_236083_v_ = new SimplexNoiseGenerator(sharedseedrandom);
         } else {
            this.field_236083_v_ = null;
         }
      }
   }
}
