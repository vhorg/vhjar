package iskallia.vault.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.network.FriendlyByteBuf;

public record ArtifactProjectorParticleOptions(ParticleType<ArtifactProjectorParticleOptions> particleType, int lifetime, double dir) implements ParticleOptions {
   public static final Deserializer<ArtifactProjectorParticleOptions> DESERIALIZER = new Deserializer<ArtifactProjectorParticleOptions>() {
      @Nonnull
      @ParametersAreNonnullByDefault
      public ArtifactProjectorParticleOptions fromCommand(ParticleType<ArtifactProjectorParticleOptions> particleType, StringReader reader) throws CommandSyntaxException {
         return new ArtifactProjectorParticleOptions(particleType, reader.readInt(), reader.readDouble());
      }

      @Nonnull
      @ParametersAreNonnullByDefault
      public ArtifactProjectorParticleOptions fromNetwork(ParticleType<ArtifactProjectorParticleOptions> particleType, FriendlyByteBuf buffer) {
         return new ArtifactProjectorParticleOptions(particleType, buffer.readInt(), buffer.readDouble());
      }
   };

   public static Codec<ArtifactProjectorParticleOptions> codec(ParticleType<ArtifactProjectorParticleOptions> particleType) {
      return RecordCodecBuilder.create(
         c -> c.group(Codec.INT.fieldOf("lifetime").forGetter(data -> data.lifetime), Codec.DOUBLE.fieldOf("dir").forGetter(data -> data.dir))
            .apply(c, (range, dir) -> new ArtifactProjectorParticleOptions(particleType, range, dir))
      );
   }

   @Nonnull
   public ParticleType<ArtifactProjectorParticleOptions> getType() {
      return this.particleType;
   }

   public void writeToNetwork(@Nonnull FriendlyByteBuf buffer) {
      buffer.writeFloat(this.lifetime);
      buffer.writeDouble(this.dir);
   }

   @Nonnull
   public String writeToString() {
      return String.format(Locale.ROOT, "%s %d %.2f", this.getType().getRegistryName(), this.lifetime, this.dir);
   }
}
