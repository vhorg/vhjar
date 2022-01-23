package iskallia.vault.mixin;

import iskallia.vault.util.IBiomeAccessor;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.LayerUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({OverworldBiomeProvider.class})
public class MixinOverworldBiomeProvider implements IBiomeAccessor {
   @Shadow
   @Final
   @Mutable
   private long field_235298_h_;
   @Shadow
   @Final
   @Mutable
   private boolean field_235299_i_;
   @Shadow
   @Final
   @Mutable
   private boolean field_235300_j_;
   @Shadow
   @Final
   @Mutable
   private Layer field_201543_c;

   @Override
   public void setSeed(long seed) {
      this.field_235298_h_ = seed;
      this.field_201543_c = LayerUtil.func_237215_a_(this.field_235298_h_, this.field_235299_i_, this.field_235300_j_ ? 6 : 4, 4);
   }

   @Override
   public void setLegacyBiomes(boolean legacyBiomes) {
      this.field_235299_i_ = legacyBiomes;
      this.field_201543_c = LayerUtil.func_237215_a_(this.field_235298_h_, this.field_235299_i_, this.field_235300_j_ ? 6 : 4, 4);
   }

   @Override
   public void setLargeBiomes(boolean largeBiomes) {
      this.field_235300_j_ = largeBiomes;
      this.field_201543_c = LayerUtil.func_237215_a_(this.field_235298_h_, this.field_235299_i_, this.field_235300_j_ ? 6 : 4, 4);
   }

   @Override
   public long getSeed() {
      return this.field_235298_h_;
   }

   @Override
   public boolean getLegacyBiomes() {
      return this.field_235299_i_;
   }

   @Override
   public boolean getLargeBiomes() {
      return this.field_235300_j_;
   }
}
