package iskallia.vault.core.event.client;

import iskallia.vault.core.event.Event;
import java.util.Optional;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;

public class AmbientParticlesEvent extends Event<AmbientParticlesEvent, AmbientParticlesEvent.Data> {
   public AmbientParticlesEvent() {
   }

   protected AmbientParticlesEvent(AmbientParticlesEvent parent) {
      super(parent);
   }

   public AmbientParticlesEvent createChild() {
      return new AmbientParticlesEvent(this);
   }

   public AmbientParticlesEvent.Data invoke(Biome biome, AmbientParticleSettings settings) {
      return this.invoke(new AmbientParticlesEvent.Data(biome, settings));
   }

   public static class Data {
      private final Biome biome;
      private AmbientParticleSettings settings;

      public Data(Biome biome, AmbientParticleSettings settings) {
         this.biome = biome;
         this.settings = settings;
      }

      public Biome getBiome() {
         return this.biome;
      }

      public Optional<AmbientParticleSettings> getSettings() {
         return Optional.ofNullable(this.settings);
      }

      public void setSettings(AmbientParticleSettings settings) {
         this.settings = settings;
      }
   }
}
