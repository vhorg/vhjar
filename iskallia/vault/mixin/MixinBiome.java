package iskallia.vault.mixin;

import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.event.client.BiomeColorsEvent;
import java.util.Optional;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Biome.class})
public abstract class MixinBiome {
   @Shadow
   @Final
   private BiomeSpecialEffects specialEffects;

   @Shadow
   protected abstract int getGrassColorFromTexture();

   @Shadow
   protected abstract int getFoliageColorFromTexture();

   @Inject(
      method = {"getFogColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getFogColor(CallbackInfoReturnable<Integer> ci) {
      int color = this.specialEffects.getFogColor();
      ci.setReturnValue(ClientEvents.BIOME_COLORS.invoke((Biome)this, 0.0, 0.0, color, BiomeColorsEvent.Type.FOG).getColor());
   }

   @Inject(
      method = {"getGrassColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getGrassColor(double posX, double posZ, CallbackInfoReturnable<Integer> ci) {
      int i = this.specialEffects.getGrassColorOverride().orElseGet(this::getGrassColorFromTexture);
      int color = this.specialEffects.getGrassColorModifier().modifyColor(posX, posZ, i);
      ci.setReturnValue(ClientEvents.BIOME_COLORS.invoke((Biome)this, posX, posZ, color, BiomeColorsEvent.Type.GRASS).getColor());
   }

   @Inject(
      method = {"getFoliageColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getFoliageColor(CallbackInfoReturnable<Integer> ci) {
      int color = this.specialEffects.getFoliageColorOverride().orElseGet(this::getFoliageColorFromTexture);
      ci.setReturnValue(ClientEvents.BIOME_COLORS.invoke((Biome)this, 0.0, 0.0, color, BiomeColorsEvent.Type.FOLIAGE).getColor());
   }

   @Inject(
      method = {"getWaterColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getWaterColor(CallbackInfoReturnable<Integer> ci) {
      int color = this.specialEffects.getWaterColor();
      ci.setReturnValue(ClientEvents.BIOME_COLORS.invoke((Biome)this, 0.0, 0.0, color, BiomeColorsEvent.Type.WATER).getColor());
   }

   @Inject(
      method = {"getWaterFogColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getWaterFogColor(CallbackInfoReturnable<Integer> ci) {
      int color = this.specialEffects.getWaterFogColor();
      ci.setReturnValue(ClientEvents.BIOME_COLORS.invoke((Biome)this, 0.0, 0.0, color, BiomeColorsEvent.Type.WATER_FOG).getColor());
   }

   @Inject(
      method = {"getAmbientParticle"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getAmbientParticle(CallbackInfoReturnable<Optional<AmbientParticleSettings>> ci) {
      AmbientParticleSettings settings = (AmbientParticleSettings)this.specialEffects.getAmbientParticleSettings().orElse(null);
      ci.setReturnValue(ClientEvents.AMBIENT_PARTICLE.invoke((Biome)this, settings).getSettings());
   }
}
